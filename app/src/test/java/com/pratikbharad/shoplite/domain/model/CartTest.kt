package com.pratikbharad.shoplite.domain.model

import com.pratikbharad.shoplite.testing.testCartItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartTest {

    @Test
    fun `totals sum quantities and line prices`() {
        val cart = Cart(
            listOf(
                testCartItem(productId = 1, unitPriceCents = 999, quantity = 3),
                testCartItem(productId = 2, unitPriceCents = 1999, quantity = 1),
            ),
        )

        assertEquals(4, cart.totalQuantity)
        assertEquals(4996L, cart.totalPriceCents)
    }

    @Test
    fun `empty cart has zero totals`() {
        val cart = Cart()

        assertTrue(cart.isEmpty)
        assertEquals(0, cart.totalQuantity)
        assertEquals(0L, cart.totalPriceCents)
    }

    @Test
    fun `item cannot be increased beyond stock`() {
        assertTrue(testCartItem(quantity = 4, stock = 5).canIncrease)
        assertFalse(testCartItem(quantity = 5, stock = 5).canIncrease)
    }
}
