package com.pratikbharad.shoplite.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pratikbharad.shoplite.R
import com.pratikbharad.shoplite.domain.model.Cart
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.ui.components.LoadingState
import com.pratikbharad.shoplite.ui.components.MessageState
import com.pratikbharad.shoplite.ui.components.OfflineBanner
import com.pratikbharad.shoplite.ui.components.ProductImage
import com.pratikbharad.shoplite.ui.components.QuantityStepper
import com.pratikbharad.shoplite.ui.formatPrice
import com.pratikbharad.shoplite.ui.theme.ShopLiteTheme

@Composable
fun CartScreen(
    isOffline: Boolean,
    onBack: () -> Unit,
    onBrowseProducts: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val removedItem = uiState.recentlyRemoved
    if (removedItem != null) {
        val message = stringResource(R.string.cart_item_removed, removedItem.title)
        val undoLabel = stringResource(R.string.action_undo)
        LaunchedEffect(removedItem) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoRemove(removedItem)
            } else {
                viewModel.onRemovalMessageShown(removedItem)
            }
        }
    }

    CartContent(
        uiState = uiState,
        isOffline = isOffline,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onBrowseProducts = onBrowseProducts,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onRemove = viewModel::remove,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartContent(
    uiState: CartUiState,
    isOffline: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onBrowseProducts: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
) {
    val cart = uiState.cart
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.cart_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    },
                )
                OfflineBanner(isOffline = isOffline)
            }
        },
        bottomBar = {
            if (!cart.isEmpty) CartSummary(cart = cart)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(Modifier.padding(innerPadding))
            cart.isEmpty -> MessageState(
                iconRes = R.drawable.ic_remove_shopping_cart,
                title = stringResource(R.string.cart_empty_title),
                message = stringResource(R.string.cart_empty_message),
                actionLabel = stringResource(R.string.action_browse_products),
                onAction = onBrowseProducts,
                modifier = Modifier.padding(innerPadding),
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = cart.items, key = { it.productId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { onIncrease(item) },
                        onDecrease = { onDecrease(item) },
                        onRemove = { onRemove(item) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            ProductImage(
                url = item.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.dp),
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.action_remove_item, item.title),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.cart_unit_price, formatPrice(item.unitPriceCents)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityStepper(
                        quantity = item.quantity,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease,
                        canIncrease = item.canIncrease,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = formatPrice(item.totalPriceCents),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummary(cart: Cart) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SummaryRow(
                label = stringResource(R.string.cart_total_items),
                value = cart.totalQuantity.toString(),
            )
            SummaryRow(
                label = stringResource(R.string.cart_total_price),
                value = formatPrice(cart.totalPriceCents),
                emphasized = true,
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasized: Boolean = false) {
    val style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
    val weight = if (emphasized) FontWeight.Bold else FontWeight.Normal
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = style, fontWeight = weight, modifier = Modifier.weight(1f))
        Text(text = value, style = style, fontWeight = weight)
    }
}

@Preview
@Composable
private fun CartContentPreview() {
    ShopLiteTheme {
        CartContent(
            uiState = CartUiState(
                isLoading = false,
                cart = Cart(
                    listOf(
                        CartItem(1, "Essence Mascara Lash Princess", "", 999, 2, 5, 0),
                        CartItem(2, "Eyeshadow Palette with Mirror", "", 1999, 1, 34, 1),
                    ),
                ),
            ),
            isOffline = true,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onBrowseProducts = {},
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
        )
    }
}
