package com.glitch.floweryapi.data.model.orders

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingCartItem(
    val itemId: String,
    val quantity: Int,
    val additionalMessage: String? = null
)
