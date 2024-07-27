package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.session.AuthSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

fun Application.configureSecurity() {
    val auth_secret_key = environment.config.property("security.auth_secret").toString()

    install(Sessions) {
        header<AuthSession>(
            name = "auth_session",
//            storage = directorySessionStorage(File("${Paths.get("")}/sessions"))
            storage = directorySessionStorage(File("build/.sessions"))
        ) {
            val secret = hex(auth_secret_key)
            transform(SessionTransportTransformerMessageAuthentication(secret))
        }
    }
}
