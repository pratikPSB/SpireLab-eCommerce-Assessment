package com.pratikbharad.shoplite.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pratikbharad.shoplite.R
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.ui.components.CartIconButton
import com.pratikbharad.shoplite.ui.components.ErrorState
import com.pratikbharad.shoplite.ui.components.LoadingState
import com.pratikbharad.shoplite.ui.components.OfflineBanner
import com.pratikbharad.shoplite.ui.components.ProductImage
import com.pratikbharad.shoplite.ui.components.QuantityStepper
import com.pratikbharad.shoplite.ui.components.RatingLabel
import com.pratikbharad.shoplite.ui.formatCategory
import com.pratikbharad.shoplite.ui.formatPrice
import com.pratikbharad.shoplite.ui.theme.ShopLiteTheme

@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsViewModel,
    isOffline: Boolean,
    cartItemCount: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isOffline) {
        if (!isOffline && uiState is ProductDetailsUiState.Error) viewModel.retry()
    }

    ProductDetailsContent(
        uiState = uiState,
        isOffline = isOffline,
        cartItemCount = cartItemCount,
        onBack = onBack,
        onCartClick = onCartClick,
        onRetry = viewModel::retry,
        onAddToCart = viewModel::addToCart,
        onDecreaseQuantity = viewModel::decreaseQuantity,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsContent(
    uiState: ProductDetailsUiState,
    isOffline: Boolean,
    cartItemCount: Int,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    onRetry: () -> Unit,
    onAddToCart: () -> Unit,
    onDecreaseQuantity: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.product_details_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    },
                    actions = { CartIconButton(itemCount = cartItemCount, onClick = onCartClick) },
                )
                OfflineBanner(isOffline = isOffline)
            }
        },
        bottomBar = {
            if (uiState is ProductDetailsUiState.Success) {
                AddToCartBar(
                    state = uiState,
                    onAddToCart = onAddToCart,
                    onDecreaseQuantity = onDecreaseQuantity,
                    onCartClick = onCartClick,
                )
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (uiState) {
            ProductDetailsUiState.Loading -> LoadingState(contentModifier)
            is ProductDetailsUiState.Error -> ErrorState(
                error = uiState.error,
                onRetry = onRetry,
                modifier = contentModifier,
            )
            is ProductDetailsUiState.Success -> ProductDetailsBody(
                product = uiState.product,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun ProductDetailsBody(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ProductImage(
            url = product.imageUrls.firstOrNull() ?: product.thumbnailUrl,
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = product.title, style = MaterialTheme.typography.headlineSmall)
            RatingLabel(rating = product.rating, style = MaterialTheme.typography.titleSmall)
            Text(
                text = formatPrice(product.priceCents),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DetailRow(
                label = stringResource(R.string.details_category),
                value = formatCategory(product.category),
            )
            DetailRow(
                label = stringResource(R.string.details_brand),
                value = product.brand ?: stringResource(R.string.brand_unknown),
            )
            DetailRow(
                label = stringResource(R.string.details_stock),
                value = if (product.isInStock) {
                    pluralStringResource(R.plurals.stock_available, product.stock, product.stock)
                } else {
                    stringResource(R.string.product_out_of_stock)
                },
                valueColor = if (product.isInStock) Color.Unspecified else MaterialTheme.colorScheme.error,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = stringResource(R.string.details_description), style = MaterialTheme.typography.titleMedium)
            Text(text = product.description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AddToCartBar(
    state: ProductDetailsUiState.Success,
    onAddToCart: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onCartClick: () -> Unit,
) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                state.quantityInCart > 0 -> {
                    QuantityStepper(
                        quantity = state.quantityInCart,
                        onDecrease = onDecreaseQuantity,
                        onIncrease = onAddToCart,
                        canIncrease = state.canAddMore,
                    )
                    Button(onClick = onCartClick, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.action_go_to_cart))
                    }
                }
                state.product.isInStock -> Button(onClick = onAddToCart, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shopping_cart),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_add_to_cart))
                }
                else -> Button(onClick = {}, enabled = false, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.product_out_of_stock))
                }
            }
        }
    }
}

private val PreviewProduct = Product(
    id = 1,
    title = "Essence Mascara Lash Princess",
    description = "A popular mascara known for its volumizing and lengthening effects.",
    category = "beauty",
    brand = "Essence",
    priceCents = 999,
    rating = 4.56,
    stock = 5,
    thumbnailUrl = "",
    imageUrls = emptyList(),
)

@Preview
@Composable
private fun ProductDetailsInCartPreview() {
    ShopLiteTheme {
        ProductDetailsContent(
            uiState = ProductDetailsUiState.Success(PreviewProduct, quantityInCart = 2),
            isOffline = true,
            cartItemCount = 2,
            onBack = {},
            onCartClick = {},
            onRetry = {},
            onAddToCart = {},
            onDecreaseQuantity = {},
        )
    }
}
