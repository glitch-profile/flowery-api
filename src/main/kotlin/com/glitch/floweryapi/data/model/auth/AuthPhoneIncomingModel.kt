package com.glitch.floweryapi.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthPhoneIncomingModel(
    val phone: String,
    val code: String? = null
)
