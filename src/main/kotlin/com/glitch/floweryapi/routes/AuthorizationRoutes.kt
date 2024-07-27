package com.glitch.floweryapi.routes

import io.ktor.server.routing.*

private const val PATH = "/apiV1"

fun Routing.authorizationRoutes() {

    post("$PATH/login-admin") {

    }

    post("$PATH/login_client") {

    }

    post("$PATH/client_verification") {

    }

    post("$PATH/login_guest") {

    }

    post("$PATH/logout") {

    }

}