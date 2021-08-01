package ru.netology.inmedia.error

import java.io.IOException
import java.sql.SQLException

sealed class AppError(var code: String): RuntimeException() {
    companion object{
        fun from(e : Throwable): AppError = when (e) {
            is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            else -> UnknownError
        }
    }
}
class ApiError(val status: Int, code: String): AppError(code)
object NetworkError : AppError("error_network")
object UnknownError: AppError("error_unknown")
object DbError : AppError("error_db")
