package com.pratikbharad.shoplite.data.remote

import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.DataException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.HttpURLConnection

suspend fun <T> apiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        throw DataException(e.toDataError(), e)
    }

fun Throwable.toDataError(): DataError = when (this) {
    is DataException -> error
    is InterruptedIOException -> DataError.TIMEOUT
    is IOException -> DataError.NO_CONNECTION
    is HttpException -> when (code()) {
        HttpURLConnection.HTTP_NOT_FOUND -> DataError.NOT_FOUND
        else -> DataError.SERVER
    }
    else -> DataError.UNKNOWN
}
