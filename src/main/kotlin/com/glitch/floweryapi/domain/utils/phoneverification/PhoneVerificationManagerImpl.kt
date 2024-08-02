package com.glitch.floweryapi.domain.utils.phoneverification

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

private const val TAG = "PHONE VERIFICATION MANAGER"
private const val CODE_CLEAR_DELAY: Long = 300_000L // 5 minutes
//private const val CODE_CLEAR_DELAY: Long = 10_000L // 10 seconds

class PhoneVerificationManagerImpl(): PhoneVerificationManager {

    private val verificationCodes = ConcurrentHashMap<String, String>()
    private val clearCodesJobs = ConcurrentHashMap<String, Job>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun generateCodeString(): String {
        var code = Random.nextLong(10_000L, 99_999L).toString()
        while (verificationCodes.values.contains(code)) code = Random.nextLong(10_000L, 99_999L).toString()
        return code
    }

    private fun clearCodeJob(phone: String) = coroutineScope.launch {
        delay(CODE_CLEAR_DELAY)
        verificationCodes.remove(phone)
        clearCodesJobs.remove(phone)
        println("$TAG - code deleted")
        println("$TAG - codes - ${verificationCodes.keys().toList()}")
        println("$TAG - jobs - ${clearCodesJobs.keys().toList()}")
    }

    override fun generateVerificationCode(phone: String): String {
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