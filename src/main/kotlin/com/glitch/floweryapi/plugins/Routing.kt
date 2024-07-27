package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.routes.authorizationRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        authorizationRoutes()
    }

}
