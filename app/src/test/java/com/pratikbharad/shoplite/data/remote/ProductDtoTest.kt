package com.pratikbharad.shoplite.data.remote

import com.pratikbharad.shoplite.data.remote.dto.ProductDto
import com.pratikbharad.shoplite.data.remote.dto.toDomain
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `decodes product without brand and ignores unknown fields`() {
        val dto = json.decodeFromString<ProductDto>(
            """{"id":16,"title":"Apple","price":1.99,"rating":4.19,"stock":9,"category":"groceries","tags":["fruits"]}""",
        )

        val product = dto.toDomain()

        assertNull(product.brand)
        assertEquals(199L, product.priceCents)
        assertEquals("groceries", product.category)
    }

    @Test
    fun `converts prices to cents without floating point drift`() {
        val prices = listOf(9.99 to 999L, 19.99 to 1999L, 0.29 to 29L, 1899.99 to 189999L)

        prices.forEach { (price, cents) ->
            assertEquals(cents, ProductDto(id = 1, title = "t", price = price).toDomain().priceCents)
        }
    }

    @Test
    fun `blank brand is treated as missing`() {
        assertNull(ProductDto(id = 1, title = "t", price = 1.0, brand = " ").toDomain().brand)
    }
}
