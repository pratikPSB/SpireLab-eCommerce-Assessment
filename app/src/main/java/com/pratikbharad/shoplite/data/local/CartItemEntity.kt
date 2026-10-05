package com.pratikbharad.shoplite.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pratikbharad.shoplite.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val thumbnailUrl: String,
    val unitPriceCents: Long,
    val quantity: Int,
    val stock: Int,
    val addedAt: Long,
)

fun CartItemEntity.toDomain(): CartItem = CartItem(
    productId = productId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    unitPriceCents = unitPriceCents,
    quantity = quantity,
    stock = stock,
    addedAt = addedAt,
)

fun CartItem.toEntity(): CartItemEntity = CartItemEntity(
    productId = productId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    unitPriceCents = unitPriceCents,
    quantity = quantity,
    stock = stock,
    addedAt = addedAt,
)
