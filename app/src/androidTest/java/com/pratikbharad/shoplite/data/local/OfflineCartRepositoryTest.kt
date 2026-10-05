package com.pratikbharad.shoplite.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pratikbharad.shoplite.data.repository.OfflineCartRepository
import com.pratikbharad.shoplite.domain.model.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineCartRepositoryTest {

    private lateinit var database: ShopLiteDatabase
    private lateinit var repository: OfflineCartRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopLiteDatabase::class.java,
        ).build()
        repository = OfflineCartRepository(database.cartDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addingTheSameProductIncrementsQuantityUpToStock() = runTest {
        val product = product(id = 1, stock = 2)

        repeat(3) { repository.add(product) }

        val cart = repository.observeCart().first()
        assertEquals(1, cart.items.size)
        assertEquals(2, cart.items.single().quantity)
    }

    @Test
    fun outOfStockProductsAreNotAdded() = runTest {
        repository.add(product(id = 1, stock = 0))

        assertTrue(repository.observeCart().first().isEmpty)
    }

    @Test
    fun totalsReflectQuantitiesAndPrices() = runTest {
        repository.add(product(id = 1, priceCents = 999))
        repository.add(product(id = 1, priceCents = 999))
        repository.add(product(id = 2, priceCents = 1999))

        val cart = repository.observeCart().first()
        assertEquals(3, cart.totalQuantity)
        assertEquals(3997L, cart.totalPriceCents)
    }

    @Test
    fun increaseIsCappedAndDecreaseRemovesTheLastUnit() = runTest {
        repository.add(product(id = 1, stock = 2))

        repository.increase(1)
        repository.increase(1)
        assertEquals(2, repository.observeQuantity(1).first())

        repository.decrease(1)
        assertEquals(1, repository.observeQuantity(1).first())

        repository.decrease(1)
        assertEquals(0, repository.observeQuantity(1).first())
        assertTrue(repository.observeCart().first().isEmpty)
    }

    @Test
    fun removedItemCanBeRestoredInItsOriginalPosition() = runTest {
        repository.add(product(id = 1))
        repository.add(product(id = 2))
        val removed = repository.observeCart().first().items.first()

        repository.remove(removed.productId)
        assertEquals(listOf(2), repository.observeCart().first().items.map { it.productId })

        repository.restore(removed)
        assertEquals(listOf(1, 2), repository.observeCart().first().items.map { it.productId })
    }

    private fun product(id: Int, priceCents: Long = 999, stock: Int = 10) = Product(
        id = id,
        title = "Product $id",
        description = "",
        category = "beauty",
        brand = null,
        priceCents = priceCents,
        rating = 4.0,
        stock = stock,
        thumbnailUrl = "",
        imageUrls = emptyList(),
    )
}
