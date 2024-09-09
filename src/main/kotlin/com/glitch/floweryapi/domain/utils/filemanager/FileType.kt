package com.glitch.floweryapi.domain.utils.filemanager

import java.io.File

enum class FileType(
) {
    IMAGE,
    VIDEO,
    DOCUMENT,
    OTHER
}

fun File.type(): FileType {
    val imageExtensions = listOf("png", "jpg", "jpeg", "heif", "svg", "webp")
    val videoExtensions = listOf("mp4", "avi", "mov")
    val documentsExtensions = listOf("pdf", "docx", "doc", "txt")
    val filename = this.extension.lowercase()
    if (filename == "") throw UnknownFileTypeExtension()
    return when (filename) {
        in imageExtensions -> FileType.IMAGE
        in videoExtensions -> FileType.VIDEO
        in documentsExtensions -> FileType.DOCUMENT
        else -> FileType.OTHER
    }

}