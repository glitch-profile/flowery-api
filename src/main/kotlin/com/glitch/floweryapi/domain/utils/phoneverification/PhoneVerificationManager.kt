package com.glitch.floweryapi.domain.utils.phoneverification

interface PhoneVerificationManager {

    fun generateVerificationCode(
        phone: String,
        isNewAccount: Boolean
    ): String

    fun checkVerificationCode(
        phone: String,
        code: String,
        isNewAccount: Boolean
    ): Boolean

}