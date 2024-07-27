package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.di.databaseModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {

    install(Koin) {
        modules(
            databaseModule
        )
    }

}