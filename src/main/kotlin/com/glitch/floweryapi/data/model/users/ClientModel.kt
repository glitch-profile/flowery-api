package com.glitch.floweryapi.data.model.users

import com.glitch.floweryapi.data.model.addresses.AddressModel
import com.glitch.floweryapi.data.model.orders.ShoppingCartItem
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class ClientModel(
    @BsonId
    val id: String = ObjectId().toString(),
    val personId: String,
    val phoneNumber: String,
    val accountCreationDate: Long,
    val favouritesItems: List<String>, // items, marked as favourites
    val shoppingCartItems: List<ShoppingCartItem>, // list of current items in user's shopping cart
    val savedAddresses: List<AddressModel> // saved delivery addresses
)
