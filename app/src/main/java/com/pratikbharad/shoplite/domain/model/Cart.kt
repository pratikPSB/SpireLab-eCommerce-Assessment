package com.pratikbharad.shoplite.domain.model

data class CartItem(
    val productId: Int,
    val title: String,
    val thumbnailUrl: String,
    val unitPriceCents: Long,
    val quantity: Int,
    val stock: Int,
    val addedAt: Long,
) {
    val totalPriceCents: Long get() = unitPriceCents * quantity
    val canIncrease: Boolean get() = quantity < stock
}

data class Cart(val items: List<CartItem> = emptyList()) {
    val totalQuantity: Int = items.sumOf { it.quantity }
    val totalPriceCents: Long = items.sumOf { it.totalPriceCents }
    val isEmpty: Boolean get() = items.isEmpty()
}
