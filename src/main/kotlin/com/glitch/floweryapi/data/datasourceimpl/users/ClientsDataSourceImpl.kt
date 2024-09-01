package com.glitch.floweryapi.data.datasourceimpl.users

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.exceptions.*
import com.glitch.floweryapi.data.model.addresses.AddressModel
import com.glitch.floweryapi.data.model.orders.ShoppingCartItem
import com.glitch.floweryapi.data.model.users.ClientModel
import com.glitch.floweryapi.domain.utils.encryptor.AESEncryptor
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList

class ClientsDataSourceImpl(
    db: MongoDatabase
): ClientsDataSource {

    private val clients = db.getCollection<ClientModel>("Clients")

    override suspend fun addClient(personId: String, phoneString: String): ClientModel {
        val encryptedPhone = AESEncryptor.encrypt(phoneString)
        val clientModel = ClientModel(
            personId = personId,
            phoneNumber = encryptedPhone
        )
        clients.insertOne(clientModel)
        return clientModel
    }

    override suspend fun getClientById(clientId: String): ClientModel {
        val filter = Filters.eq("_id", clientId)
        return clients.find(filter).singleOrNull() ?: throw UserNotFoundException()
    }

    override suspend fun getClientsByIds(clientIds: List<String>): List<ClientModel> {
        val filter = Filters.`in`("_id", clientIds)
        return clients.find(filter).toList()
    }

    override suspend fun getClientByPhoneNumber(phoneString: String): ClientModel {
        val encryptedPhone = AESEncryptor.encrypt(phoneString)
        val filter = Filters.eq(ClientModel::phoneNumber.name, encryptedPhone)
        return clients.find(filter).singleOrNull() ?: throw UserNotFoundException()
    }

    override suspend fun addItemToFavourites(clientId: String, itemId: String): Boolean {
        val client = getClientById(clientId)
        if (client.favouritesItems.contains(itemId)) throw ItemAlreadyExistsException()
        val filter = Filters.eq("_id", clientId)
        val update = Updates.addToSet(ClientModel::favouritesItems.name, itemId)
        val result = clients.updateOne(filter, update)
        if (result.matchedCount == 0L) throw  UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun removeItemFromFavourites(clientId: String, itemId: String): Boolean {
        val filter = Filters.eq("_id", clientId)
        val update = Updates.pull(ClientModel::favouritesItems.name, itemId)
        val result = clients.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else if (result.modifiedCount == 0L) throw ItemNotFoundException()
        else return true
    }

    override suspend fun addItemToShoppingCart(clientId: String, itemId: String): Boolean {
        val client = getClientById(clientId)
        if (client.shoppingCartItems.any { it.itemId == itemId }) throw ItemAlreadyExistsException()
        val item = ShoppingCartItem(itemId = itemId)
        val filter = Filters.eq("_id", clientId)
        val update = Updates.addToSet(ClientModel::shoppingCartItems.name, item)
        val result = clients.updateOne(filter, update)
        if (result.matchedCount == 0L) throw  UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun removeItemFromShoppingCart(clientId: String, itemId: String): Boolean {
//        val clientFilter = Filters.eq("_id", clientId)
//        val shoppingCartFilter = Filters.eq("${ClientModel::shoppingCartItems.name}.${ShoppingCartItem::itemId.name}", itemId)
//        val update = Updates.pull("${ClientModel::shoppingCartItems.name}.${ShoppingCartItem::itemId.name}", itemId)
//        val options = UpdateOptions()
//            .arrayFilters(listOf(shoppingCartFilter))
//        val result = clients.updateOne(clientFilter, update, options)
//        if (result.matchedCount == 0L) throw UserNotFoundException()
//        else return result.modifiedCount != 0L

        val clientShoppingCart = getClientById(clientId).shoppingCartItems
        return if (clientShoppingCart.any { it.itemId == itemId }) {
            val newShoppingCartList = clientShoppingCart.toMutableList().apply {
                this.removeIf { it.itemId == itemId }
            }
            val filter = Filters.eq("_id", clientId)
            val update = Updates.set(ClientModel::shoppingCartItems.name, newShoppingCartList)
            val result = clients.updateOne(filter, update)
            if (result.matchedCount == 0L) throw UserNotFoundException()
            else result.modifiedCount != 0L
        } else throw ItemNotFoundException()
    }

    override suspend fun updateShoppingCartItemQuantity(clientId: String, itemId: String, newQuantity: Int): Boolean {
//        val clientFilter = Filters.eq("_id", clientId)
//        val shoppingCartFilter = Filters.eq("${ClientModel::shoppingCartItems.name}.${ShoppingCartItem::itemId.name}", itemId)
//        val update = Updates.set("${ClientModel::shoppingCartItems.name}.${ShoppingCartItem::quantity.name}", newQuantity)
//        val options = UpdateOptions()
//            .arrayFilters(listOf(shoppingCartFilter))
//        val result = clients.updateOne(clientFilter, update, options)
//        if (result.matchedCount == 0L) throw UserNotFoundException()
//        else return result.modifiedCount != 0L

        val clientShoppingCart = getClientById(clientId).shoppingCartItems
        if (clientShoppingCart.any { it.itemId == itemId }) {
            val newShoppingCartItem = ShoppingCartItem(itemId = itemId, quantity = newQuantity)
            val indexToReplace = clientShoppingCart.indexOfFirst { it.itemId == itemId }
            val newShoppingCartList = clientShoppingCart.toMutableList().apply {
                this[indexToReplace] = newShoppingCartItem
            }
            val filter = Filters.eq("_id", clientId)
            val update = Updates.set(ClientModel::shoppingCartItems.name, newShoppingCartList)
            val result = clients.updateOne(filter, update)
            if (result.matchedCount == 0L) throw UserNotFoundException()
            else return result.modifiedCount != 0L
        } else throw ItemNotFoundException()
    }

    override suspend fun addDeliveryAddress(
        clientId: String,
        city: String?,
        street: String,
        houseNumber: String,
        lat: Float,
        lon: Float
    ): AddressModel {
        val client = getClientById(clientId)
        if (client.savedAddresses.any { ( it.lat == lat ) && ( it.lon == lon ) }) throw AddressAlreadyExistException()
        val newAddress = AddressModel(
            city = city,
            street = street,
            houseNumber = houseNumber,
            lat = lat,
            lon = lon
        )
        val filter = Filters.eq("_id", clientId)
        val update = Updates.addToSet(ClientModel::shoppingCartItems.name, newAddress)
        val result = clients.updateOne(filter, update)
        if (result.matchedCount == 0L) throw  UserNotFoundException()
        else return newAddress
    }

    override suspend fun removeDeliveryAddress(clientId: String, addressId: String): Boolean {
        val savedAddresses = getClientById(clientId).savedAddresses
        return if (savedAddresses.any { it.id == addressId }) {
            val newAddressesList = savedAddresses.toMutableList().apply {
                this.removeIf { it.id == addressId }
            }
            val filter = Filters.eq("_id", clientId)
            val update = Updates.set(ClientModel::savedAddresses.name, newAddressesList)
            val result = clients.updateOne(filter, update)
            if (result.matchedCount == 0L) throw UserNotFoundException()
            else result.modifiedCount != 0L
        } else throw AddressNotFoundException()
    }

    override suspend fun editDeliveryAddress(
        clientId: String,
        addressId: String,
        city: String?,
        street: String,
        houseNumber: String,
        lat: Float,
        lon: Float
    ): Boolean {
        val savedAddresses = getClientById(clientId).savedAddresses
        return if (savedAddresses.any { it.id == addressId }) {
            val indexToReplace = savedAddresses.indexOfFirst { it.id == addressId }
            val newAddressesList = savedAddresses.toMutableList().apply {
                this.removeAt(indexToReplace)
            }
            val filter = Filters.eq("_id", clientId)
            val update = Updates.set(ClientModel::savedAddresses.name, newAddressesList)
            val result = clients.updateOne(filter, update)
            if (result.matchedCount == 0L) throw UserNotFoundException()
            else result.modifiedCount != 0L
        } else throw AddressNotFoundException()
    }
}