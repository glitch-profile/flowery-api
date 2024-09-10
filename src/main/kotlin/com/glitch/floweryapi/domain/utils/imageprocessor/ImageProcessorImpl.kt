package com.glitch.floweryapi.domain.utils.imageprocessor

import com.glitch.floweryapi.domain.utils.filemanager.FileType
import com.glitch.floweryapi.domain.utils.filemanager.UnknownFileTypeExtension
import com.glitch.floweryapi.domain.utils.filemanager.type
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import kotlin.math.max

private const val WRITER_FORMAT_NAME = "JPEG"

class ImageProcessorImpl: ImageProcessor {

    override fun getImageDimensions(localPath: String): ImageDimensions {
        val imageFile = File(localPath)
        if (imageFile.type() != FileType.IMAGE || !imageFile.isFile || !imageFile.exists())
            throw UnknownFileTypeExtension()
        val image = ImageIO.read(imageFile)
        return ImageDimensions(
            width = image.width,
            height = image.height
        )
    }

    override fun compressImageWithProportions(
        inputFile: File,
        outputFile: File,
        maxDimSize: Int,
        compressionQuality: Float
    ) {
        if (inputFile.type() != FileType.IMAGE || !inputFile.isFile || !inputFile.exists())
            throw UnknownFileTypeExtension()
        val sourceImage = ImageIO.read(inputFile)

        val scaleFactor = maxDimSize.toFloat() / max(sourceImage.width, sourceImage.height)
        val newWidth = (sourceImage.width * scaleFactor).toInt()
        val newHeight = (sourceImage.height * scaleFactor).toInt()

        val newImage = BufferedImage(
            newWidth, newHeight, BufferedImage.TYPE_INT_RGB
        )
        newImage.createGraphics().apply {
            color = Color.BLACK
            drawRect(0, 0, newWidth, newHeight)
            drawImage(
                sourceImage,
                0,
                0,
                newWidth,
                newHeight,
                null
            )
            dispose()
        }

        val jpgWriter = ImageIO.getImageWritersByFormatName(WRITER_FORMAT_NAME).next()
        val jpgWriterParams = jpgWriter.defaultWriteParam
        jpgWriterParams.compressionMode = ImageWriteParam.MODE_EXPLICIT
        jpgWriterParams.compressionQuality = compressionQuality

        val outputStream = FileImageOutputStream(outputFile)
        jpgWriter.output = outputStream
        jpgWriter.write(null, IIOImage(newImage, null, null), jpgWriterParams)
        jpgWriter.dispose()
        outputStream.close()
    }

    override fun compressImageAndCrop(inputFile: File, outputFile: File, sideSize: Int, compressionQuality: Float) {
        if (inputFile.type() != FileType.IMAGE || !inputFile.isFile || !inputFile.exists())
            throw UnknownFileTypeExtension()
        val sourceImage = ImageIO.read(inputFile)

        val cropCoordinates = if (sourceImage.width == sourceImage.height) {
            ImageCropCoordinates(
                0,
                0,
                sourceImage.width,
                sourceImage.height
            )
        } else if (sourceImage.width > sourceImage.height) {
            ImageCropCoordinates(
                (sourceImage.width / 2) - (sourceImage.height / 2),
                0,
                sourceImage.width,
                sourceImage.height
            )
        } else {
            ImageCropCoordinates(
                0,
                (sourceImage.height / 2) - (sourceImage.width / 2),
                sourceImage.width,
                sourceImage.height
            )
        }
        val squareImage = sourceImage.getSubimage(
            cropCoordinates.topLeftXCord,
            cropCoordinates.topLeftYCord,
            cropCoordinates.width,
            cropCoordinates.width
        )

        val scaleFactor = sideSize.toFloat() / squareImage.width
        val newWidth = (squareImage.width * scaleFactor).toInt()
        val newHeight = (squareImage.height * scaleFactor).toInt()
        val newImage = BufferedImage(
            newWidth,
            newHeight,
            BufferedImage.TYPE_INT_RGB
        )
        newImage.createGraphics().apply {
            color = Color.BLACK
            drawRect(0, 0, newWidth, newHeight)
            drawImage(
                squareImage,
                0,
                0,
                newWidth,
                newHeight,
                null
            )
            dispose()
        }

        val jpgWriter = ImageIO.getImageWritersByFormatName(WRITER_FORMAT_NAME).next()
        val jpgWriterParams = jpgWriter.defaultWriteParam
        jpgWriterParams.compressionMode = ImageWriteParam.MODE_EXPLICIT
        jpgWriterParams.compressionQuality = compressionQuality

        val outputStream = FileImageOutputStream(outputFile)
        jpgWriter.output = outputStream
        jpgWriter.write(null, IIOImage(newImage, null, null), jpgWriterParams)
        jpgWriter.dispose()
        outputStream.close()
    }
}