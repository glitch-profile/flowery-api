package com.glitch.floweryapi.data.datasource

import com.glitch.floweryapi.data.model.ImageModel
import com.glitch.floweryapi.data.model.products.ProductModel
import com.glitch.floweryapi.data.model.products.ProductsSortOrder

interface ProductsDataSource {

    suspend fun getProductById(productId: String): ProductModel

    suspend fun getProducts(
        name: String?,
        categoryId: String? = null,
        sortOrder: ProductsSortOrder = ProductsSortOrder.NAME_ASCENDING,
        onlyShowAvailable: Boolean = true
    ): List<ProductModel>

    suspend fun getProductsList(
        name: String?,
        categoryId: String? = null,
        startIndex: Int,
        pageLimit: Int = 20,
        sortOrder: ProductsSortOrder = ProductsSortOrder.NAME_ASCENDING,
        onlyShowAvailable: Boolean = true
    ): List<ProductModel>

    suspend fun updateProductAvailability(productId: String, isAvailable: Boolean): Boolean

    suspend fun createNewProduct(
        name: String,
        description: String,
        categoryId: String,
        price: Float,
        images: List<ImageModel>,
        specialPrice: Int? = null,
        isAvailable: Boolean = true
    ): ProductModel

    suspend fun deleteProductById(productId: String): Boolean

    suspend fun updateProductModel(productModel: ProductModel): Boolean

}