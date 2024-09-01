package com.glitch.floweryapi.data.model.auth

data class AuthNewUserIncomingModel(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val verificationCode: String
)
