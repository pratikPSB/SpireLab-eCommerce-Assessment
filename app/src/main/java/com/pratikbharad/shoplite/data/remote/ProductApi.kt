package com.pratikbharad.shoplite.data.remote

import com.pratikbharad.shoplite.data.remote.dto.ProductDto
import com.pratikbharad.shoplite.data.remote.dto.ProductPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): ProductPageDto

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
    ): ProductPageDto

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductDto
}
