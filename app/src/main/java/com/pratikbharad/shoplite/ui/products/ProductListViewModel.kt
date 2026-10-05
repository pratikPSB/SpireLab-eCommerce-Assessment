package com.pratikbharad.shoplite.ui.products

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    productRepository: ProductRepository,
) : ViewModel() {

    val searchFieldState = TextFieldState()

    val products: Flow<PagingData<Product>> = snapshotFlow { searchFieldState.text.toString().trim() }
        .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
        .distinctUntilChanged()
        .flatMapLatest { productRepository.products(it) }
        .cachedIn(viewModelScope)

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 400L
    }
}
