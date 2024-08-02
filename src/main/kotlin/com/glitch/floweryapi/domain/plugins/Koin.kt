package com.glitch.floweryapi.domain.plugins

import com.glitch.floweryapi.di.dataSourceModule
import com.glitch.floweryapi.di.databaseModule
import com.glitch.floweryapi.di.utilsModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {

    install(Koin) {
        modules(
            databaseModule,
            utilsModule,
            dataSourceModule
        )
    }

}