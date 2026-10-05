package com.pratikbharad.shoplite.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.pratikbharad.shoplite.navigation.Destination
import com.pratikbharad.shoplite.ui.cart.CartScreen
import com.pratikbharad.shoplite.ui.details.ProductDetailsScreen
import com.pratikbharad.shoplite.ui.details.ProductDetailsViewModel
import com.pratikbharad.shoplite.ui.products.ProductListScreen

@Composable
fun ShopLiteApp(viewModel: AppViewModel = hiltViewModel()) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(Destination.ProductList)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Destination.ProductList> {
                ProductListScreen(
                    isOffline = appState.isOffline,
                    cartItemCount = appState.cartItemCount,
                    onProductClick = { productId -> backStack.add(Destination.ProductDetails(productId)) },
                    onCartClick = { backStack.add(Destination.Cart) },
                )
            }
            entry<Destination.ProductDetails> { destination ->
                ProductDetailsScreen(
                    viewModel = hiltViewModel<ProductDetailsViewModel, ProductDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(destination.productId) },
                    ),
                    isOffline = appState.isOffline,
                    cartItemCount = appState.cartItemCount,
                    onBack = { backStack.removeLastOrNull() },
                    onCartClick = { backStack.add(Destination.Cart) },
                )
            }
            entry<Destination.Cart> {
                CartScreen(
                    isOffline = appState.isOffline,
                    onBack = { backStack.removeLastOrNull() },
                    onBrowseProducts = { backStack.popToRoot() },
                )
            }
        },
    )
}

private fun NavBackStack<NavKey>.popToRoot() {
    while (size > 1) removeAt(lastIndex)
}
