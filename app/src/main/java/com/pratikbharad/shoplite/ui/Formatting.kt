package com.pratikbharad.shoplite.ui

import androidx.annotation.StringRes
import com.pratikbharad.shoplite.R
import com.pratikbharad.shoplite.domain.model.DataError
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatPrice(cents: Long, locale: Locale = Locale.getDefault()): String =
    NumberFormat.getCurrencyInstance(locale)
        .apply { currency = Currency.getInstance("USD") }
        .format(BigDecimal.valueOf(cents, 2))

fun formatCategory(slug: String, locale: Locale = Locale.getDefault()): String =
    slug.replace('-', ' ').replaceFirstChar { it.titlecase(locale) }

@StringRes
fun DataError.titleRes(): Int = when (this) {
    DataError.NO_CONNECTION -> R.string.error_no_connection_title
    DataError.TIMEOUT -> R.string.error_timeout_title
    DataError.NOT_FOUND -> R.string.error_not_found_title
    DataError.SERVER -> R.string.error_server_title
    DataError.UNKNOWN -> R.string.error_unknown_title
}

@StringRes
fun DataError.messageRes(): Int = when (this) {
    DataError.NO_CONNECTION -> R.string.error_no_connection
    DataError.TIMEOUT -> R.string.error_timeout
    DataError.NOT_FOUND -> R.string.error_not_found
    DataError.SERVER -> R.string.error_server
    DataError.UNKNOWN -> R.string.error_unknown
}
