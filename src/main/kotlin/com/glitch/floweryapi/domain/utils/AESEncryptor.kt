package com.glitch.floweryapi.domain.utils

import io.ktor.server.config.*
import io.ktor.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val TAG = "AES ENCRYPTOR"
private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"

object AESEncryptor {

    private val secret = ApplicationConfig(null).tryGetString("security.encrypt_secret")
    private val salt = ApplicationConfig(null).tryGetString("security.encrypt_salt")
    private val iv = ApplicationConfig(null).tryGetString("security.encrypt_iv")

    fun encrypt(normalString: String): String? {
        return try {
            val secretKey = SecretKeySpec(secret!!.toByteArray(), "AES")
            val ivParameterSpec = IvParameterSpec(iv!!.toByteArray())

            val plainText = normalString.toByteArray()

            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

            val encryptedString = cipher.doFinal(plainText)
            encryptedString.encodeBase64()
        } catch (e: Exception) {
            println("$TAG - unable to encrypt string")
            e.printStackTrace()
            null
        }
    }

    fun decrypt(encryptedString: String): String? {
        return try {
            val secretKey = SecretKeySpec(secret!!.toByteArray(), "AES")
            val ivParameterSpec = IvParameterSpec(iv!!.toByteArray())

            val textToDecrypt = encryptedString.decodeBase64Bytes()

            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

            val decryptedString = cipher.doFinal(textToDecrypt)
            decryptedString.decodeToString()
        } catch (e: Exception) {
            println("$TAG - unable to decrypt string")
            e.printStackTrace()
            null
        }
    }

}