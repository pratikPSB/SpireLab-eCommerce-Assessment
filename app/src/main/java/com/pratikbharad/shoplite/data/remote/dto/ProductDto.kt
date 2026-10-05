package com.pratikbharad.shoplite.data.remote.dto

import com.pratikbharad.shoplite.domain.model.Product
import kotlinx.serialization.Serializable
import kotlin.math.roundToLong

@Serializable
data class ProductPageDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String = "",
    val category: String = "",
    val brand: String? = null,
    val price: Double,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val thumbnail: String = "",
    val images: List<String> = emptyList(),
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    brand = brand?.takeIf { it.isNotBlank() },
    priceCents = (price * 100).roundToLong(),
    rating = rating,
    stock = stock.coerceAtLeast(0),
    thumbnailUrl = thumbnail,
    imageUrls = images,
)
