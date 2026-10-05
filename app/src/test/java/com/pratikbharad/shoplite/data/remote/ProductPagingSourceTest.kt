package com.pratikbharad.shoplite.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.dataError
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.testing.FakeProductApi
import com.pratikbharad.shoplite.testing.testProductDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.UnknownHostException

class ProductPagingSourceTest {

    private val config = PagingConfig(pageSize = 2, initialLoadSize = 2, enablePlaceholders = false)
    private val api = FakeProductApi((1..5).map { testProductDto(it) })

    @Test
    fun `loads consecutive pages until the total is reached`() = runTest {
        val pager = TestPager(config, ProductPagingSource(api, query = ""))

        val first = pager.refresh() as LoadResult.Page
        val second = pager.append() as LoadResult.Page
        val third = pager.append() as LoadResult.Page

        assertEquals(listOf(1, 2), first.data.ids())
        assertEquals(2, first.nextKey)
        assertEquals(listOf(3, 4), second.data.ids())
        assertEquals(listOf(5), third.data.ids())
        assertNull(third.nextKey)
    }

    @Test
    fun `uses the search endpoint for a non-blank query`() = runTest {
        val api = FakeProductApi(listOf(testProductDto(1, "iPhone"), testProductDto(2, "Lipstick")))
        val pager = TestPager(config, ProductPagingSource(api, query = "phone"))

        val page = pager.refresh() as LoadResult.Page

        assertEquals(listOf("phone"), api.searchQueries)
        assertEquals(listOf(1), page.data.ids())
        assertNull(page.nextKey)
    }

    @Test
    fun `returns a typed error when the request fails`() = runTest {
        api.failure = UnknownHostException()
        val pager = TestPager(config, ProductPagingSource(api, query = ""))

        val result = pager.refresh() as LoadResult.Error

        assertEquals(DataError.NO_CONNECTION, result.throwable.dataError)
    }

    private fun List<Product>.ids() = map { it.id }
}
