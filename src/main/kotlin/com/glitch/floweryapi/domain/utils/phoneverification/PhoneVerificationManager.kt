package com.glitch.floweryapi.domain.utils.phoneverification

interface PhoneVerificationManager {

    fun generateVerificationCode(
        phone: String,
        duration: VerificationCodeDuration = VerificationCodeDuration.DEFAULT
    ): String

    fun checkVerificationCode(phone: String, code: String): Boolean

}