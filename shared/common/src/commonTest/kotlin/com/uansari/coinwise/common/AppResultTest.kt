package com.uansari.coinwise.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppResultTest {

    @Test
    fun `map transforms a success`() {
        val result: AppResult<Int> = AppResult.Success(2)
        assertEquals(AppResult.Success(4), result.map { it * 2 })
    }

    @Test
    fun `map leaves a failure untouched`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Offline)
        assertEquals(result, result.map { it * 2 })
    }

    @Test
    fun `map does not invoke the transform on failure`() {
        var invoked = false
        val result: AppResult<Int> = AppResult.Failure(AppError.Transient)
        result.map { invoked = true; it }
        assertTrue(!invoked)
    }

    @Test
    fun `flatMap chains successes`() {
        val result: AppResult<Int> = AppResult.Success(2)
        assertEquals(
            AppResult.Success("4"),
            result.flatMap { AppResult.Success((it * 2).toString()) },
        )
    }

    @Test
    fun `flatMap short-circuits on failure`() {
        val result: AppResult<Int> = AppResult.Failure(AppError.NotFound)
        assertEquals(result, result.flatMap { AppResult.Success(it) })
    }

    @Test
    fun `getOrNull returns null for a failure`() {
        assertNull(AppResult.Failure(AppError.Offline).getOrNull())
    }
}