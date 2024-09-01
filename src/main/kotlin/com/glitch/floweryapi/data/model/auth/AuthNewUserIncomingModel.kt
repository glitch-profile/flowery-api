package com.glitch.floweryapi.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthNewUserIncomingModel(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val verificationCode: String
)
