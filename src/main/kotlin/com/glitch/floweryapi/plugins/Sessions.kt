package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.session.AuthSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

fun Application.configureSessions() {
    val auth_secret_key = environment.config.property("security.auth_sign").getString()

    install(Sessions) {
        val secret = hex(auth_secret_key)

        header<AuthSession>(
            name = "auth_session",
//            storage = directorySessionStorage(File("${Paths.get("")}/sessions"))
            storage = directorySessionStorage(File("build/.sessions"))
        ) {
            transform(SessionTransportTransformerMessageAuthentication(secret))
        }
    }
}
