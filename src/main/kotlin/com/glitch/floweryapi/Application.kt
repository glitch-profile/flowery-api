package com.glitch.floweryapi

import com.glitch.floweryapi.plugins.configureAuthentication
import com.glitch.floweryapi.plugins.configureRouting
import com.glitch.floweryapi.plugins.configureSessions
import com.glitch.floweryapi.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSessions()
    configureAuthentication()
    configureRouting()
}
