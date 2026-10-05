package com.pratikbharad.shoplite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = true)
abstract class ShopLiteDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
