package com.glitch.floweryapi.data.datasource

import com.glitch.floweryapi.data.model.addresses.AddressModel
import com.glitch.floweryapi.data.model.users.ClientModel

interface ClientsDataSource {

    abstract fun getClientById(clientId: String): ClientModel

    abstract fun getClientsByIds(clientIds: List<String>): List<ClientModel>

    abstract fun getClientByPhoneNumber(phoneString: String): ClientModel

    abstract fun addItemToFavourites(clientId: String, itemId: String): Boolean

    abstract fun removeItemFromFavourites(clientId: String, itemId: String): Boolean

    abstract fun addItemToShoppingCart(clientId: String, itemId: String): Boolean

    abstract fun removeItemFromShoppingCart(clientId: String, itemId: String): Boolean

    abstract fun updateShoppingCartItemQuantity(clientId: String, itemId: String, newQuantity: Int): Boolean

    abstract fun updateShoppingCartItemMessage(clientId: String, itemId: String, newMessage: String?): Boolean

    abstract fun addDeliveryAddress(clientId: String,
                                    city: String?,
                                    street: String,
                                    houseNumber: String,
                                    lat: Float,
                                    lon: Float
    ): AddressModel

    abstract fun removeDeliveryAddress(clientId: String, addressId: String)

    abstract fun editDeliveryAddress(clientId: String,
                                     addressId: String,
                                     city: String?,
                                     street: String,
                                     houseNumber: String,
                                     lat: Float,
                                     lon: Float
    )

}