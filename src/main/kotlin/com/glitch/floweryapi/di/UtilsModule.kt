package com.glitch.floweryapi.di

import com.glitch.floweryapi.domain.utils.filemanager.FileManager
import com.glitch.floweryapi.domain.utils.filemanager.FileManagerImpl
import com.glitch.floweryapi.domain.utils.imageprocessor.ImageProcessor
import com.glitch.floweryapi.domain.utils.imageprocessor.ImageProcessorImpl
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManager
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManagerImpl
import org.koin.dsl.module

val utilsModule = module {
    single<PhoneVerificationManager> {
        PhoneVerificationManagerImpl()
    }
    single<FileManager> {
        FileManagerImpl()
    }
    single<ImageProcessor> {
        ImageProcessorImpl()
    }
}