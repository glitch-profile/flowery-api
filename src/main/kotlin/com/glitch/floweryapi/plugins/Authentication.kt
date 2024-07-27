package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.session.AuthSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {

    install(Authentication) {
        session<AuthSession> {
            validate { session ->
                if (session.personId.isNotEmpty()) session else null
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        session<AuthSession>("client") {
            validate { session ->
                if (session.personId.isNotEmpty()) session else null //TODO
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
        session<AuthSession>("admin") {
            validate { session ->
                if (session.personId.isNotEmpty()) session else null //TODO
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

}