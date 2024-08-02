package com.glitch.floweryapi.di

import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManager
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManagerImpl
import org.koin.dsl.module

val utilsModule = module {
    single<PhoneVerificationManager> {
        PhoneVerificationManagerImpl()
    }
}