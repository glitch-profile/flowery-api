package com.glitch.floweryapi.data.model.products

import com.glitch.floweryapi.data.model.ImageModel
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class ProductModel(
    val id: String = ObjectId().toString(),
    val name: String,
    val description: String,
    val category: String,
    val isAvailable: Boolean = true,
    val price: Float,
    val specialPrice: Float? = null,
    val images: List<ImageModel>
)