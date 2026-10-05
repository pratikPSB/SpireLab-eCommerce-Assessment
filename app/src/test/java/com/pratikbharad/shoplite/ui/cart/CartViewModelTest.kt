package com.pratikbharad.shoplite.ui.cart

import com.pratikbharad.shoplite.testing.FakeCartRepository
import com.pratikbharad.shoplite.testing.MainDispatcherRule
import com.pratikbharad.shoplite.testing.testCartItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mascara = testCartItem(productId = 1, unitPriceCents = 999, quantity = 2, stock = 3)
    private val palette = testCartItem(productId = 2, unitPriceCents = 1999, quantity = 1)
    private val repository = FakeCartRepository(listOf(mascara, palette))

    @Test
    fun `exposes cart items and totals`() = runTest {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf(mascara, palette), state.cart.items)
        assertEquals(3, state.cart.totalQuantity)
        assertEquals(3997L, state.cart.totalPriceCents)
    }

    @Test
    fun `increase and decrease update quantity and totals`() = runTest {
        val viewModel = createViewModel()

        viewModel.increase(mascara)
        assertEquals(3, viewModel.uiState.value.cart.items.first().quantity)
        assertEquals(4996L, viewModel.uiState.value.cart.totalPriceCents)

        viewModel.increase(viewModel.uiState.value.cart.items.first())
        assertEquals(3, viewModel.uiState.value.cart.items.first().quantity)

        viewModel.decrease(viewModel.uiState.value.cart.items.first())
        assertEquals(2, viewModel.uiState.value.cart.items.first().quantity)
    }

    @Test
    fun `decreasing the last unit removes the item and offers undo`() = runTest {
        val viewModel = createViewModel()

        viewModel.decrease(palette)

        assertEquals(listOf(mascara), viewModel.uiState.value.cart.items)
        assertEquals(palette, viewModel.uiState.value.recentlyRemoved)
    }

    @Test
    fun `undo restores a removed item`() = runTest {
        val viewModel = createViewModel()
        viewModel.remove(mascara)
        assertEquals(listOf(palette), viewModel.uiState.value.cart.items)

        viewModel.undoRemove(mascara)

        assertEquals(listOf(mascara, palette), viewModel.uiState.value.cart.items)
        assertNull(viewModel.uiState.value.recentlyRemoved)
    }

    @Test
    fun `dismissing the removal message clears it`() = runTest {
        val viewModel = createViewModel()
        viewModel.remove(mascara)

        viewModel.onRemovalMessageShown(mascara)

        assertNull(viewModel.uiState.value.recentlyRemoved)
        assertEquals(listOf(palette), viewModel.uiState.value.cart.items)
    }

    private fun TestScope.createViewModel(): CartViewModel {
        val viewModel = CartViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }
}
