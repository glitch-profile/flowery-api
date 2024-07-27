package com.glitch.floweryapi

import com.glitch.floweryapi.plugins.configureRouting
import com.glitch.floweryapi.plugins.configureSecurity
import com.glitch.floweryapi.plugins.configureSerialization
import com.glitch.floweryapi.plugins.configureSockets
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
