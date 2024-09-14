package com.glitch.floweryapi.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageModel(
    val imageUrl: String,
    val previewImageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
)
