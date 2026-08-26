package com.uansari.coinwise.common

sealed interface AppError {
    data object Network : AppError
    data object RateLimited : AppError
    data class Unknown(val message: String) : AppError
}