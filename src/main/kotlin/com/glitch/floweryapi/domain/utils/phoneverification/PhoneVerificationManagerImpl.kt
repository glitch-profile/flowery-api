package com.glitch.floweryapi.domain.utils.phoneverification

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

private const val TAG = "PHONE VERIFICATION MANAGER"
private const val SHORT_CODE_DURATION = 10_000L
private const val DEFAULT_CODE_DURATION = 300_000L
private const val LONG_CODE_DURATION = 600_000L

class PhoneVerificationManagerImpl(): PhoneVerificationManager {

    private val verificationCodes = ConcurrentHashMap<String, String>()
    private val newPhoneVerificationCodes = ConcurrentHashMap<String, String>()
    private val clearCodesJobs = ConcurrentHashMap<String, Job>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun generateCodeString(): String {
        var code = Random.nextLong(10_000L, 99_999L).toString()
        while (verificationCodes.values.contains(code)) code = Random.nextLong(10_000L, 99_999L).toString()
        return code
    }

    private fun generateNewPhoneCodeString(): String {
        var code = Random.nextLong(10_000L, 99_999L).toString()
        while (newPhoneVerificationCodes.values.contains(code)) code = Random.nextLong(10_000L, 99_999L).toString()
        return code
    }

    private fun clearCodeJob(
        phone: String,
        isNewAccount: Boolean = false
    ): Job {
        return coroutineScope.launch {
            val delay = if (isNewAccount) LONG_CODE_DURATION else DEFAULT_CODE_DURATION
            delay(delay)
            if (isNewAccount) {
                newPhoneVerificationCodes.remove(phone)
            } else {
                verificationCodes.remove(phone)
            }
            clearCodesJobs.remove(phone)
            println("$TAG - code for $phone is deleted")
        }
    }

    override fun generateVerificationCode(phone: String, isNewAccount: Boolean): String {
        val code = generateCodeString()
        if (isNewAccount) {
            if (newPhoneVerificationCodes.contains(key = phone)) {
                clearCodesJobs[phone]?.cancel()
                clearCodesJobs.remove(phone)
            }
            newPhoneVerificationCodes[phone] = code
        } else {
            if (verificationCodes.contains(key = phone)) {
                clearCodesJobs[phone]?.cancel()
                clearCodesJobs.remove(phone)
            }
            verificationCodes[phone] = code
        }
        val clearKeyJob = clearCodeJob(phone, isNewAccount)
        clearCodesJobs[phone] = clearKeyJob
        println("$TAG - new code $code registered")
        return code
    }

    override fun checkVerificationCode(phone: String, code: String, isNewAccount: Boolean): Boolean {
        val verificationCode = if (isNewAccount) {
            newPhoneVerificationCodes.getOrElse(phone) {
                throw PhoneNotFoundException()
            }
        } else {
            verificationCodes.getOrElse(phone) {
                throw PhoneNotFoundException()
            }
        }
        return if (verificationCode == code) {
            if (isNewAccount) newPhoneVerificationCodes.remove(phone)
            else verificationCodes.remove(phone)
            clearCodesJobs[phone]?.cancel()
            clearCodesJobs.remove(phone)
            true
        } else false
    }

}

