package com.pratikbharad.shoplite.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.pratikbharad.shoplite.R
import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.Product
import com.pratikbharad.shoplite.domain.model.dataError
import com.pratikbharad.shoplite.ui.components.CartIconButton
import com.pratikbharad.shoplite.ui.components.ErrorState
import com.pratikbharad.shoplite.ui.components.LoadingState
import com.pratikbharad.shoplite.ui.components.MessageState
import com.pratikbharad.shoplite.ui.components.OfflineBanner
import com.pratikbharad.shoplite.ui.components.ProductImage
import com.pratikbharad.shoplite.ui.components.RatingLabel
import com.pratikbharad.shoplite.ui.formatPrice
import com.pratikbharad.shoplite.ui.titleRes

@Composable
fun ProductListScreen(
    isOffline: Boolean,
    cartItemCount: Int,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val products = viewModel.products.collectAsLazyPagingItems()

    LaunchedEffect(isOffline) {
        val loadState = products.loadState
        if (!isOffline && (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error)) {
            products.retry()
        }
    }

    ProductListContent(
        searchFieldState = viewModel.searchFieldState,
        products = products,
        isOffline = isOffline,
        cartItemCount = cartItemCount,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListContent(
    searchFieldState: TextFieldState,
    products: LazyPagingItems<Product>,
    isOffline: Boolean,
    cartItemCount: Int,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = { CartIconButton(itemCount = cartItemCount, onClick = onCartClick) },
                )
                SearchField(
                    state = searchFieldState,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                )
                OfflineBanner(isOffline = isOffline)
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.ime),
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val refresh = products.loadState.refresh
        when {
            refresh is LoadState.Error -> ErrorState(
                error = refresh.error.dataError,
                onRetry = products::retry,
                modifier = contentModifier,
            )
            refresh is LoadState.Loading && products.itemCount == 0 -> LoadingState(contentModifier)
            products.itemCount == 0 -> EmptyProducts(
                query = searchFieldState.text.toString().trim(),
                modifier = contentModifier,
            )
            else -> ProductGrid(
                products = products,
                isRefreshing = refresh is LoadState.Loading,
                onProductClick = onProductClick,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun SearchField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        state = state,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
        trailingIcon = {
            if (state.text.isNotEmpty()) {
                IconButton(onClick = { state.clearText() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.action_clear_search),
                    )
                }
            }
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { keyboardController?.hide() },
    )
}

@Composable
private fun ProductGrid(
    products: LazyPagingItems<Product>,
    isRefreshing: Boolean,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) gridState.requestScrollToItem(0)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            state = gridState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = products.itemCount,
                key = products.itemKey { it.id },
                contentType = products.itemContentType { "product" },
            ) { index ->
                products[index]?.let { product ->
                    ProductCard(product = product, onClick = { onProductClick(product.id) })
                }
            }

            when (val append = products.loadState.append) {
                is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadMoreError(error = append.error.dataError, onRetry = products::retry)
                }
                is LoadState.NotLoading -> Unit
            }
        }

        if (isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier) {
        ProductImage(
            url = product.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleSmall,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatPrice(product.priceCents),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                RatingLabel(rating = product.rating)
            }
        }
    }
}

@Composable
private fun LoadMoreError(
    error: DataError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.load_more_error, stringResource(error.titleRes())),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun EmptyProducts(query: String, modifier: Modifier = Modifier) {
    if (query.isEmpty()) {
        MessageState(
            iconRes = R.drawable.ic_search_off,
            title = stringResource(R.string.products_empty_title),
            message = stringResource(R.string.products_empty_message),
            modifier = modifier,
        )
    } else {
        MessageState(
            iconRes = R.drawable.ic_search_off,
            title = stringResource(R.string.search_empty_title),
            message = stringResource(R.string.search_empty_message, query),
            modifier = modifier,
        )
    }
}
