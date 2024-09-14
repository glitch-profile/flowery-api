package com.glitch.floweryapi.data.datasource.users

import com.glitch.floweryapi.data.model.addresses.AddressModel
import com.glitch.floweryapi.data.model.users.ClientModel

interface ClientsDataSource {

    suspend fun addClient(
        personId: String,
        phoneString: String
    ): ClientModel
    
    suspend fun getClientById(clientId: String): ClientModel

    suspend fun getClientsByIds(clientIds: List<String>): List<ClientModel>

    suspend fun getClientByPhoneNumber(phoneString: String): ClientModel

    suspend fun addItemToFavourites(clientId: String, itemId: String): Boolean

    suspend fun removeItemFromFavourites(clientId: String, itemId: String): Boolean

    suspend fun addItemToShoppingCart(clientId: String, itemId: String): Boolean

    suspend fun removeItemFromShoppingCart(clientId: String, itemId: String): Boolean

    suspend fun updateShoppingCartItemQuantity(clientId: String, itemId: String, newQuantity: Int): Boolean

    suspend fun addDeliveryAddress(
        clientId: String,
        city: String?,
        street: String,
        houseNumber: String,
        lat: Float,
        lon: Float
    ): AddressModel

    suspend fun removeDeliveryAddress(clientId: String, addressId: String): Boolean

    suspend fun editDeliveryAddress(
        clientId: String,
        addressId: String,
        city: String?,
        street: String,
        houseNumber: String,
        lat: Float,
        lon: Float
    ): Boolean

}