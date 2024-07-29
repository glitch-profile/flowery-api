package com.glitch.floweryapi.data.model.addresses

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class AddressModel(
    val id: String = ObjectId().toString(),
    val city: String?,
    val street: String?,
    val houseNumber: String?,
    val lat: Float, // latitude
    val lon: Float // longitude
)
