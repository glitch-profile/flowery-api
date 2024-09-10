package com.glitch.floweryapi.domain.utils.filemanager

import io.ktor.server.config.*
import java.io.File
import kotlin.io.path.Path

class FileManagerImpl: FileManager {

    private val baseUrl = ApplicationConfig(null).tryGetString("storage.base_url")!!

    override fun uploadFile(fileName: String, byteArray: ByteArray): String {
        val directory = when (File(fileName).type()) {
            FileType.IMAGE -> "images"
            FileType.VIDEO -> "videos"
            FileType.DOCUMENT -> "documents"
            FileType.OTHER -> "other"
        }
        val localPath = "${Path("").toAbsolutePath()}/resources/$directory"
        val uploadedFile = File(localPath, fileName)
        uploadedFile.writeBytes(byteArray)
        return getUrlPath(uploadedFile.path)
    }

    override fun getFile(urlPath: String): File? {
        val file = File(getLocalPath(urlPath))
        return if (file.exists() && file.canRead())
            file
        else null
    }

    override fun deleteFile(urlPath: String): Boolean {
        val file = File(getLocalPath(urlPath))
        return file.delete()
    }

    override fun getUrlPath(localPath: String): String {
        val absolutePath = "${Path("").toAbsolutePath()}/resources"
        val staticPath = "$baseUrl/static"
        return localPath.replace(
            oldValue = absolutePath,
            newValue = staticPath
        )
    }

    override fun getLocalPath(urlPath: String): String {
        val staticPath = "$baseUrl/static"
        val absolutePath = "${Path("").toAbsolutePath()}/resources"
        return urlPath.replace(
            oldValue = staticPath,
            newValue = absolutePath
        )
    }
}