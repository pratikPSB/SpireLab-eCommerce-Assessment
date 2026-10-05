package com.pratikbharad.shoplite.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikbharad.shoplite.domain.model.Cart
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = true,
    val cart: Cart = Cart(),
    val recentlyRemoved: CartItem? = null,
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val recentlyRemoved = MutableStateFlow<CartItem?>(null)

    val uiState: StateFlow<CartUiState> =
        combine(cartRepository.observeCart(), recentlyRemoved) { cart, removed ->
            CartUiState(isLoading = false, cart = cart, recentlyRemoved = removed)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState(),
        )

    fun increase(item: CartItem) {
        viewModelScope.launch { cartRepository.increase(item.productId) }
    }

    fun decrease(item: CartItem) {
        if (item.quantity <= 1) {
            remove(item)
        } else {
            viewModelScope.launch { cartRepository.decrease(item.productId) }
        }
    }

    fun remove(item: CartItem) {
        viewModelScope.launch {
            cartRepository.remove(item.productId)
            recentlyRemoved.value = item
        }
    }

    fun undoRemove(item: CartItem) {
        viewModelScope.launch { cartRepository.restore(item) }
        onRemovalMessageShown(item)
    }

    fun onRemovalMessageShown(item: CartItem) {
        recentlyRemoved.compareAndSet(item, null)
    }
}
