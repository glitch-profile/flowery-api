package com.glitch.floweryapi.domain.utils.filemanager

import java.io.File

interface FileManager {

    fun uploadFile(
        fileName: String,
        byteArray: ByteArray
    ): String?

    fun getFile(urlPath: String): File?

    fun deleteFile(urlPath: String): Boolean

    fun getUrlPath(localPath: String): String

    fun getLocalPath(urlPath: String): String



}