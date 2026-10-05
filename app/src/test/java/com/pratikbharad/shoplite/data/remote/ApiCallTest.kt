package com.pratikbharad.shoplite.data.remote

import com.pratikbharad.shoplite.domain.model.DataError
import com.pratikbharad.shoplite.domain.model.DataException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiCallTest {

    @Test
    fun `maps network failures to data errors`() {
        assertEquals(DataError.NO_CONNECTION, UnknownHostException().toDataError())
        assertEquals(DataError.NO_CONNECTION, IOException("reset").toDataError())
        assertEquals(DataError.TIMEOUT, SocketTimeoutException().toDataError())
        assertEquals(DataError.NOT_FOUND, httpException(404).toDataError())
        assertEquals(DataError.SERVER, httpException(500).toDataError())
        assertEquals(DataError.UNKNOWN, SerializationException("bad json").toDataError())
    }

    @Test
    fun `apiCall wraps failures in DataException`() = runTest {
        val exception = runCatching { apiCall { throw UnknownHostException() } }.exceptionOrNull()

        assertEquals(DataError.NO_CONNECTION, (exception as DataException).error)
    }

    @Test
    fun `apiCall rethrows cancellation untouched`() {
        assertThrows(CancellationException::class.java) {
            runBlocking { apiCall { throw CancellationException() } }
        }
    }

    private fun httpException(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))
}
