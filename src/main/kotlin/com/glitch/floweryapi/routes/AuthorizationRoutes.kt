package com.glitch.floweryapi.routes

import com.glitch.floweryapi.data.model.ApiResponse
import com.glitch.floweryapi.session.AuthSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File

private const val PATH = "/apiV1/auth"

fun Routing.authorizationRoutes() {

    post("$PATH/login-admin") {
        call.sessions.set(AuthSession("12345"))
    }

    post("$PATH/login-client") {

    }

    post("$PATH/client-verification") {

    }

    post("$PATH/login-guest") {

    }

    authenticate {

        post("$PATH/session-verification") {
            val currentSession = call.sessions.get<AuthSession>()
            if (currentSession != null) {
                // add session info to person model
                call.sessions.set(currentSession.copy(isRegistered = true))
                call.respond(
                    ApiResponse(
                        status = true,
                        message = "session registered.",
                        data = Unit
                    )
                )
            } else {
                call.respond(
                    ApiResponse(
                        status = false,
                        message = "unable to get session. Please login again.",
                        data = Unit
                    )
                )
            }
        }

        post("$PATH/logout") {
            call.sessions.clear<AuthSession>()
        }

        get("$PATH/test") {
            call.respond(HttpStatusCode.Forbidden)
            val sessionStorage = directorySessionStorage(File("build/.sessions"))
            val sessionId = call.request.queryParameters["session_id"]
            println(sessionId)
            sessionStorage.invalidate(sessionId!!)
        }
    }

}