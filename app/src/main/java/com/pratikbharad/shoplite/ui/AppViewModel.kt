package com.pratikbharad.shoplite.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikbharad.shoplite.data.network.NetworkMonitor
import com.pratikbharad.shoplite.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppUiState(
    val isOffline: Boolean = false,
    val cartItemCount: Int = 0,
)

@HiltViewModel
class AppViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    cartRepository: CartRepository,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> =
        combine(networkMonitor.isOnline, cartRepository.observeCart()) { isOnline, cart ->
            AppUiState(isOffline = !isOnline, cartItemCount = cart.totalQuantity)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppUiState(),
        )
}
