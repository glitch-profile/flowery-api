package com.glitch.floweryapi.domain.utils.phoneverification

interface PhoneVerificationManager {

    fun generateVerificationCode(phone: String): String

    fun checkVerificationCode(phone: String, code: String): Boolean

}