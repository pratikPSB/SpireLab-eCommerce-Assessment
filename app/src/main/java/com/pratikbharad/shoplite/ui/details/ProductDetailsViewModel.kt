package com.pratikbharad.shoplite.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.DataException
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.CartRepository
import com.pratikbharad.shoplite.domain.repository.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState
    data class Error(val error: DataError) : ProductDetailsUiState
    data class Success(val product: Product, val quantityInCart: Int) : ProductDetailsUiState {
        val canAddMore: Boolean get() = quantityInCart < product.stock
    }
}

@HiltViewModel(assistedFactory = ProductDetailsViewModel.Factory::class)
class ProductDetailsViewModel @AssistedInject constructor(
    @Assisted private val productId: Int,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val loadState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    private var loadJob: Job? = null

    val uiState: StateFlow<ProductDetailsUiState> =
        combine(loadState, cartRepository.observeQuantity(productId)) { state, quantity ->
            if (state is ProductDetailsUiState.Success) state.copy(quantityInCart = quantity) else state
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductDetailsUiState.Loading,
        )

    init {
        loadProduct()
    }

    fun retry() = loadProduct()

    fun addToCart() {
        val product = (loadState.value as? ProductDetailsUiState.Success)?.product ?: return
        viewModelScope.launch { cartRepository.add(product) }
    }

    fun decreaseQuantity() {
        viewModelScope.launch { cartRepository.decrease(productId) }
    }

    private fun loadProduct() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            loadState.value = ProductDetailsUiState.Loading
            loadState.value = try {
                ProductDetailsUiState.Success(productRepository.getProduct(productId), quantityInCart = 0)
            } catch (e: DataException) {
                ProductDetailsUiState.Error(e.error)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(productId: Int): ProductDetailsViewModel
    }
}
