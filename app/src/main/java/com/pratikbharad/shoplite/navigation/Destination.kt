package com.pratikbharad.shoplite.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {

    @Serializable
    data object ProductList : Destination

    @Serializable
    data class ProductDetails(val productId: Int) : Destination

    @Serializable
    data object Cart : Destination
}
