package com.glitch.floweryapi.data.model.goods

import kotlinx.serialization.Serializable

@Serializable
data class ImageModel(
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
)
