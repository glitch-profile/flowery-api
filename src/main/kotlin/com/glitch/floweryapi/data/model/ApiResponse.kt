package com.glitch.floweryapi.data.model

import com.glitch.floweryapi.domain.utils.ApiResponseMessageCode
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T,
    val status: Boolean,
    val messageCode: ApiResponseMessageCode,
    val message: String = ""
)
