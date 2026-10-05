package com.pratikbharad.shoplite.domain.repository

import androidx.paging.PagingData
import com.pratikbharad.shoplite.domain.model.DataException
import com.pratikbharad.shoplite.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun products(query: String): Flow<PagingData<Product>>

    @Throws(DataException::class)
    suspend fun getProduct(id: Int): Product
}
