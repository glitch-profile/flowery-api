package com.glitch.floweryapi.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseModel(
    val personId: String,
    val clientId: String?,
    val employeeId: String?,
    val employeeRoles: List<String>
)
