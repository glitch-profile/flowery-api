package com.glitch.floweryapi

import com.glitch.floweryapi.domain.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureSessions()
    configureAuthentication()
    configureRouting()
}
