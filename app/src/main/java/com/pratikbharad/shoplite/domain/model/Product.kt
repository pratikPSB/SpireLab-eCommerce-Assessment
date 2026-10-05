package com.pratikbharad.shoplite.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val brand: String?,
    val priceCents: Long,
    val rating: Double,
    val stock: Int,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
) {
    val isInStock: Boolean get() = stock > 0
}
