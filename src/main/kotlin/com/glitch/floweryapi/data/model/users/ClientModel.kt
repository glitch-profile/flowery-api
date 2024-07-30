package com.glitch.floweryapi.data.model.users

import com.glitch.floweryapi.data.model.addresses.AddressModel
import com.glitch.floweryapi.data.model.orders.ShoppingCartItem
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class ClientModel(
    @BsonId
    val id: String = ObjectId().toString(),
    val personId: String,
    val phoneNumber: String,
    val accountCreationDate: Long = OffsetDateTime.now(ZoneId.systemDefault()).toEpochSecond(),
    val favouritesItems: List<String> = emptyList(), // items, marked as favourites
    val shoppingCartItems: List<ShoppingCartItem> = emptyList(), // list of current items in user's shopping cart
    val savedAddresses: List<AddressModel> = emptyList() // saved delivery addresses
)
