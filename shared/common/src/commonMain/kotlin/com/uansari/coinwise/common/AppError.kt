package com.uansari.coinwise.common

sealed interface AppError {

    /** No usable connection. Show cached data; retry when connectivity returns. */
    data object Offline : AppError

    /** Timeout, 5xx, connection reset. Retrying now is reasonable. */
    data object Transient : AppError

    /** CoinGecko returned 429. Back off for the given interval, then retry. */
    data class RateLimited(val retryAfterSeconds: Long?) : AppError

    /** The resource does not exist. Retrying will not help. */
    data object NotFound : AppError

    /**
     * Unclassified. Carries a diagnostic string for logs, never for display.
     * Reaching this case in normal operation means the taxonomy has a gap.
     */
    data class Unexpected(val detail: String) : AppError
}