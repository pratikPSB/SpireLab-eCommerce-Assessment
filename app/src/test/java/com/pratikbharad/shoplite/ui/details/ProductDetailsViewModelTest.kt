package com.pratikbharad.shoplite.ui.details

import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.testing.FakeCartRepository
import com.pratikbharad.shoplite.testing.FakeProductRepository
import com.pratikbharad.shoplite.testing.MainDispatcherRule
import com.pratikbharad.shoplite.testing.testProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val product = testProduct(id = 7, stock = 2)
    private val productRepository = FakeProductRepository(listOf(product))
    private val cartRepository = FakeCartRepository()

    @Test
    fun `shows the product with its quantity in the cart`() = runTest {
        val viewModel = createViewModel()

        assertEquals(ProductDetailsUiState.Success(product, quantityInCart = 0), viewModel.uiState.value)
    }

    @Test
    fun `shows an error and recovers on retry`() = runTest {
        productRepository.error = DataError.NO_CONNECTION
        val viewModel = createViewModel()

        assertEquals(ProductDetailsUiState.Error(DataError.NO_CONNECTION), viewModel.uiState.value)

        productRepository.error = null
        viewModel.retry()

        assertEquals(ProductDetailsUiState.Success(product, quantityInCart = 0), viewModel.uiState.value)
        assertEquals(2, productRepository.getProductCalls)
    }

    @Test
    fun `adding to cart is capped by stock`() = runTest {
        val viewModel = createViewModel()

        viewModel.addToCart()
        assertEquals(1, successState(viewModel).quantityInCart)
        assertTrue(successState(viewModel).canAddMore)

        viewModel.addToCart()
        viewModel.addToCart()
        assertEquals(2, successState(viewModel).quantityInCart)
        assertFalse(successState(viewModel).canAddMore)
    }

    @Test
    fun `decreasing the last unit removes the product from the cart`() = runTest {
        val viewModel = createViewModel()
        viewModel.addToCart()

        viewModel.decreaseQuantity()

        assertEquals(0, successState(viewModel).quantityInCart)
        assertTrue(cartRepository.currentItems.isEmpty())
    }

    private fun TestScope.createViewModel(): ProductDetailsViewModel {
        val viewModel = ProductDetailsViewModel(product.id, productRepository, cartRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private fun successState(viewModel: ProductDetailsViewModel) =
        viewModel.uiState.value as ProductDetailsUiState.Success
}
