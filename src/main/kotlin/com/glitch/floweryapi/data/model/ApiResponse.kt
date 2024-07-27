package com.glitch.floweryapi.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T,
    val status: Boolean,
    val message: String = ""
)
