package com.glitch.floweryapi.session

import io.ktor.server.auth.*

data class AuthSession(
    val personId: String
): Principal
