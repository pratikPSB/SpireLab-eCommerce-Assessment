package com.pratikbharad.shoplite.testing

import com.pratikbharad.shoplite.data.remote.dto.ProductDto
import com.pratikbharad.shoplite.domain.model.CartItem
import com.pratikbharad.shoplite.domain.model.Product

fun testProduct(
    id: Int = 1,
    priceCents: Long = 999,
    stock: Int = 10,
) = Product(
    id = id,
    title = "Product $id",
    description = "Description $id",
    category = "beauty",
    brand = "Brand",
    priceCents = priceCents,
    rating = 4.5,
    stock = stock,
    thumbnailUrl = "https://example.com/$id.png",
    imageUrls = emptyList(),
)

fun testCartItem(
    productId: Int = 1,
    unitPriceCents: Long = 999,
    quantity: Int = 1,
    stock: Int = 10,
) = CartItem(
    productId = productId,
    title = "Product $productId",
    thumbnailUrl = "https://example.com/$productId.png",
    unitPriceCents = unitPriceCents,
    quantity = quantity,
    stock = stock,
    addedAt = productId.toLong(),
)

fun testProductDto(id: Int, title: String = "Product $id") = ProductDto(
    id = id,
    title = title,
    price = 9.99,
)
