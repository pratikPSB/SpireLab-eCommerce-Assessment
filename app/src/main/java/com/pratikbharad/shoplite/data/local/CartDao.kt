package com.pratikbharad.shoplite.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartDao {

    @Query("SELECT * FROM cart_items ORDER BY addedAt ASC, productId ASC")
    abstract fun observeItems(): Flow<List<CartItemEntity>>

    @Query("SELECT quantity FROM cart_items WHERE productId = :productId")
    abstract fun observeQuantity(productId: Int): Flow<Int?>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    abstract suspend fun find(productId: Int): CartItemEntity?

    @Upsert
    abstract suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE productId = :productId AND quantity < stock")
    abstract suspend fun increment(productId: Int)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :productId AND quantity > 1")
    abstract suspend fun decrement(productId: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    abstract suspend fun delete(productId: Int)

    @Transaction
    open suspend fun decrementOrDelete(productId: Int) {
        val existing = find(productId) ?: return
        if (existing.quantity > 1) decrement(productId) else delete(productId)
    }

    @Transaction
    open suspend fun addOne(snapshot: CartItemEntity) {
        if (snapshot.stock <= 0) return
        val existing = find(snapshot.productId)
        val quantity = ((existing?.quantity ?: 0) + 1).coerceAtMost(snapshot.stock)
        upsert(
            snapshot.copy(
                quantity = quantity,
                addedAt = existing?.addedAt ?: snapshot.addedAt,
            ),
        )
    }
}
