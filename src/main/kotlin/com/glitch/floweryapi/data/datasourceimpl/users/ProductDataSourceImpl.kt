package com.glitch.floweryapi.data.datasourceimpl.users

import com.glitch.floweryapi.data.datasource.ProductsDataSource
import com.glitch.floweryapi.data.exceptions.products.ProductNotFoundException
import com.glitch.floweryapi.data.model.ImageModel
import com.glitch.floweryapi.data.model.products.ProductModel
import com.glitch.floweryapi.data.model.products.ProductsSortOrder
import com.glitch.floweryapi.domain.utils.filemanager.FileManager
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.bson.conversions.Bson

class ProductDataSourceImpl(
    database: MongoDatabase,
    private val fileManager: FileManager
): ProductsDataSource {

    private val products = database.getCollection<ProductModel>("Products")

    override suspend fun getProductById(productId: String): ProductModel {
        val filter = Filters.eq("_id", productId)
        val result = products.find(filter).singleOrNull() ?: throw ProductNotFoundException()
        return result
    }

    override suspend fun getProducts(
        name: String?,
        categoryId: String?,
        sortOrder: ProductsSortOrder,
        onlyShowAvailable: Boolean
    ): List<ProductModel> {
        val filtersList = buildList<Bson> {
            if (name != null) Filters.regex(ProductModel::name.name, "^$name.*\$", "i") // i - case insensitive
            if (categoryId != null) Filters.eq(ProductModel::categoryId.name, categoryId)
            if (onlyShowAvailable) Filters.eq(ProductModel::isAvailable.name, true)
        }
        val filters = Filters.and(filtersList)
        val result = products.find(filters).toList()
        val sortedResults = when (sortOrder) {
            ProductsSortOrder.NAME_ASCENDING -> result.sortedBy { it.name }
            ProductsSortOrder.NAME_DESCENDING -> result.sortedByDescending { it.name }
            ProductsSortOrder.PRICE_ASCENDING -> result.sortedBy { it.specialPrice ?: it.price }
            ProductsSortOrder.PRICE_DESCENDING -> result.sortedByDescending { it.specialPrice ?: it.price }
            ProductsSortOrder.RATING_ASCENDING -> result.sortedBy { it.ratingsSum.toFloat() / it.ratingsCount }
            ProductsSortOrder.RATING_DESCENDING -> result.sortedByDescending { it.ratingsSum.toFloat() / it.ratingsCount }
        }
        return sortedResults
    }

    override suspend fun getProductsList(
        name: String?,
        categoryId: String?,
        startIndex: Int,
        pageLimit: Int,
        sortOrder: ProductsSortOrder,
        onlyShowAvailable: Boolean
    ): List<ProductModel> {
        val filtersList = buildList<Bson> {
            if (name != null) Filters.regex(ProductModel::name.name, "^$name.*\$", "i") // i - case insensitive
            if (categoryId != null) Filters.eq(ProductModel::categoryId.name, categoryId)
            if (onlyShowAvailable) Filters.eq(ProductModel::isAvailable.name, true)
        }
        val filters = Filters.and(filtersList)
        val result = products.find(filters).toList()
        val sortedResults = when (sortOrder) {
            ProductsSortOrder.NAME_ASCENDING -> result.sortedBy { it.name }
            ProductsSortOrder.NAME_DESCENDING -> result.sortedByDescending { it.name }
            ProductsSortOrder.PRICE_ASCENDING -> result.sortedBy { it.specialPrice ?: it.price }
            ProductsSortOrder.PRICE_DESCENDING -> result.sortedByDescending { it.specialPrice ?: it.price }
            ProductsSortOrder.RATING_ASCENDING -> result.sortedBy { it.ratingsSum.toFloat() / it.ratingsCount }
            ProductsSortOrder.RATING_DESCENDING -> result.sortedByDescending { it.ratingsSum.toFloat() / it.ratingsCount }
        }
        return sortedResults.subList(startIndex, startIndex + pageLimit)
    }

    override suspend fun updateProductAvailability(productId: String, isAvailable: Boolean): Boolean {
        val filter = Filters.eq("_id", productId)
        val update = Updates.set(ProductModel::isAvailable.name, isAvailable)
        val result = products.updateOne(filter, update)
        if (result.matchedCount != 0L) return true
        else throw ProductNotFoundException()
    }

    override suspend fun createNewProduct(
        name: String,
        description: String,
        categoryId: String,
        price: Float,
        images: List<ImageModel>,
        specialPrice: Float?,
        isAvailable: Boolean
    ): ProductModel {
        val product = ProductModel(
            name = name,
            description = description,
            categoryId = categoryId,
            price = price,
            images = images,
            specialPrice = specialPrice,
            isAvailable = isAvailable
        )
        val result = products.insertOne(product)
        return product
    }

    override suspend fun deleteProductById(productId: String): Boolean {
        try {
            val product = getProductById(productId)
            val cleanerScope = CoroutineScope(Dispatchers.Default)
            cleanerScope.launch {
                try {
                    val imagesUrl = product.images.map { it.imageUrl }
                    val imageFilter = Filters.`in`("${ProductModel::images.name}.${ImageModel::imageUrl.name}", imagesUrl)
                    val productsWithSameImages = products.find(imageFilter).toList()
                    if (productsWithSameImages.isNotEmpty()) {
                        val imagesUrlFromLinkedProducts = productsWithSameImages
                            .flatMap { it.images.map { images -> images.imageUrl } }
                            .distinct()
                        val notUsedImagesUrl = imagesUrl.filterNot { imagesUrlFromLinkedProducts.contains(it) }
                        notUsedImagesUrl.forEach { fileManager.deleteFile(it) }
                        val imagesToDelete = product.images.filter { notUsedImagesUrl.contains(it.imageUrl) }
                        imagesToDelete.forEach {
                            fileManager.deleteFile(it.imageUrl)
                            fileManager.deleteFile(it.previewImageUrl)
                        }
                    } else {
                        product.images.forEach {
                            fileManager.deleteFile(it.imageUrl)
                            fileManager.deleteFile(it.previewImageUrl)
                        }
                    }
                } catch (e: SecurityException) {
                    println("PRODUCTS DATA SOURCE - Unable to delete file. Security violations.")
                }
            }
            val deleteFilter = Filters.eq("_id", productId)
            val result = products.deleteOne(deleteFilter)
            return result.deletedCount != 0L
        } catch (e: ProductNotFoundException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // TODO:refactor in the future
    override suspend fun updateProductModel(productModel: ProductModel): Boolean {
        val result = products.insertOne(productModel)
        return result.insertedId != null
    }
}