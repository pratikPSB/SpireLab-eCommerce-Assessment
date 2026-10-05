package com.pratikbharad.shoplite.testing

import androidx.paging.PagingData
import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.DataException
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProductRepository(private val products: List<Product> = emptyList()) : ProductRepository {

    var error: DataError? = null
    var getProductCalls = 0
        private set

    override fun products(query: String): Flow<PagingData<Product>> =
        flowOf(PagingData.from(products.filter { it.title.contains(query, ignoreCase = true) }))

    override suspend fun getProduct(id: Int): Product {
        getProductCalls++
        error?.let { throw DataException(it) }
        return products.find { it.id == id } ?: throw DataException(DataError.NOT_FOUND)
    }
}
