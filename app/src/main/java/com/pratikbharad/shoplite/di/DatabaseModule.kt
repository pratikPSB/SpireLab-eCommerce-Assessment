package com.pratikbharad.shoplite.di

import android.content.Context
import androidx.room.Room
import com.pratikbharad.shoplite.data.local.CartDao
import com.pratikbharad.shoplite.data.local.ShopLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShopLiteDatabase =
        Room.databaseBuilder(context, ShopLiteDatabase::class.java, "shoplite.db").build()

    @Provides
    fun provideCartDao(database: ShopLiteDatabase): CartDao = database.cartDao()
}
