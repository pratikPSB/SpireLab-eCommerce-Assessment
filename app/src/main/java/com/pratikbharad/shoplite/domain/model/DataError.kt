package com.pratikbharad.shoplite.domain.model

enum class DataError {
    NO_CONNECTION,
    TIMEOUT,
    NOT_FOUND,
    SERVER,
    UNKNOWN,
}

class DataException(val error: DataError, cause: Throwable? = null) : Exception(error.name, cause)

val Throwable.dataError: DataError
    get() = (this as? DataException)?.error ?: DataError.UNKNOWN
