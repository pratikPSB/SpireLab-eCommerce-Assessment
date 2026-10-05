package com.pratikbharad.shoplite.testing

import com.pratikbharad.shoplite.data.remote.ProductApi
import com.pratikbharad.shoplite.data.remote.dto.ProductDto
import com.pratikbharad.shoplite.data.remote.dto.ProductPageDto

class FakeProductApi(private val products: List<ProductDto>) : ProductApi {

    var failure: Exception? = null
    val searchQueries = mutableListOf<String>()

    override suspend fun getProducts(limit: Int, skip: Int): ProductPageDto = page(products, limit, skip)

    override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductPageDto {
        searchQueries += query
        return page(products.filter { it.title.contains(query, ignoreCase = true) }, limit, skip)
    }

    override suspend fun getProduct(id: Int): ProductDto {
        failure?.let { throw it }
        return products.first { it.id == id }
    }

    private fun page(source: List<ProductDto>, limit: Int, skip: Int): ProductPageDto {
        failure?.let { throw it }
        return ProductPageDto(
            products = source.drop(skip).take(limit),
            total = source.size,
            skip = skip,
            limit = limit,
        )
    }
}
