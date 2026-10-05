package com.pratikbharad.shoplite.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pratikbharad.shoplite.data.remote.dto.toDomain
import com.pratikbharad.shoplite.domain.model.DataException
import com.pratikbharad.shoplite.domain.model.Product

class ProductPagingSource(
    private val api: ProductApi,
    private val query: String,
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val skip = params.key ?: 0
        return try {
            val page = apiCall {
                if (query.isBlank()) {
                    api.getProducts(limit = params.loadSize, skip = skip)
                } else {
                    api.searchProducts(query = query, limit = params.loadSize, skip = skip)
                }
            }
            val nextSkip = skip + page.products.size
            LoadResult.Page(
                data = page.products.map { it.toDomain() },
                prevKey = null,
                nextKey = nextSkip.takeIf { page.products.isNotEmpty() && it < page.total },
            )
        } catch (e: DataException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? = null
}
