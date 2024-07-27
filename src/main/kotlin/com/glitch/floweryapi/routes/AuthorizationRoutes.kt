package com.glitch.floweryapi.routes

import com.glitch.floweryapi.session.AuthSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

private const val PATH = "/apiV1"

fun Routing.authorizationRoutes() {

    post("$PATH/login-admin") {
        call.sessions.set(AuthSession("12345"))
    }

    post("$PATH/login_client") {

    }

    post("$PATH/client_verification") {

    }

    post("$PATH/login_guest") {

    }

    authenticate {
        post("$PATH/logout") {
            call.sessions.clear<AuthSession>()
        }
    }

}