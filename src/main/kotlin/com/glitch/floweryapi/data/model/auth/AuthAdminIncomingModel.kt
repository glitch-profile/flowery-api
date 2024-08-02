package com.glitch.floweryapi.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
class AuthAdminIncomingModel(
    val username: String,
    val password: String
)