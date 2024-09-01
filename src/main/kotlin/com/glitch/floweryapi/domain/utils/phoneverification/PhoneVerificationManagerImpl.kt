package com.glitch.floweryapi.domain.utils.phoneverification

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

private const val TAG = "PHONE VERIFICATION MANAGER"

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

    private fun clearCodeJob(
        phone: String,
        duration: VerificationCodeDuration = VerificationCodeDuration.DEFAULT
    ): Job {
        return coroutineScope.launch {
            delay(duration.delay)
            verificationCodes.remove(phone)
            clearCodesJobs.remove(phone)
            println("$TAG - code deleted")
            println("$TAG - codes - ${verificationCodes.keys().toList()}")
            println("$TAG - jobs - ${clearCodesJobs.keys().toList()}")
        }
    }

    override fun generateVerificationCode(phone: String, duration: VerificationCodeDuration): String {
        val code = generateCodeString()
        if (verificationCodes.keys().toList().contains(phone)) {
            clearCodesJobs[phone]?.cancel() ?: println("$TAG - unable to cancel clear job for $phone")
            clearCodesJobs.remove(phone)
        }
        val clearKeyJob = clearCodeJob(phone)
        verificationCodes[phone] = code
        clearCodesJobs[phone] = clearKeyJob
        println("$TAG - new code $code registered")
        return code
    }

    override fun checkVerificationCode(phone: String, code: String): Boolean {
        val verificationCode = verificationCodes.getOrElse(phone) {
            throw PhoneNotFoundException()
        }
        return if (verificationCode == code) {
            verificationCodes.remove(phone)
            clearCodesJobs[phone]?.cancel()
            clearCodesJobs.remove(phone)
            true
        } else false
    }

}

enum class VerificationCodeDuration(val delay: Long) {
    SHORT(10_000L), // 10 seconds
    DEFAULT(300_000L), // 5 minutes
    LONG(600_000L) // 10 minutes
}

