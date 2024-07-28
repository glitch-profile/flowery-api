package com.glitch.floweryapi.data.model.addresses

import kotlinx.serialization.Serializable

@Serializable
data class AddressModel(
    val city: String?,
    val street: String?,
    val houseNumber: String?,
    val lat: Float, // latitude
    val lon: Float // longitude
)
