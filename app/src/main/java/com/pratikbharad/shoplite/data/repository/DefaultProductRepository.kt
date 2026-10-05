package com.pratikbharad.shoplite.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pratikbharad.shoplite.data.remote.ProductApi
import com.pratikbharad.shoplite.data.remote.ProductPagingSource
import com.pratikbharad.shoplite.data.remote.apiCall
import com.pratikbharad.shoplite.data.remote.dto.toDomain
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultProductRepository @Inject constructor(
    private val api: ProductApi,
) : ProductRepository {

    override fun products(query: String): Flow<PagingData<Product>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { ProductPagingSource(api, query.trim()) },
        ).flow

    override suspend fun getProduct(id: Int): Product = apiCall { api.getProduct(id).toDomain() }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
