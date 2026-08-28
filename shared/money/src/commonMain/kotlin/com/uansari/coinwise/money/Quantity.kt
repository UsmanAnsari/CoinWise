package com.uansari.coinwise.money

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.uansari.coinwise.common.AppError
import com.uansari.coinwise.common.AppResult

class Quantity private constructor(
    internal val value: BigDecimal
) : Comparable<Quantity> {

    val asString: String get() = value.toStringExpanded()

    val isZero: Boolean get() = value.isZero()

    operator fun plus(other: Quantity) = Quantity(value + other.value)
    operator fun minus(other: Quantity) = Quantity(value - other.value)

    override fun compareTo(other: Quantity): Int = value.compareTo(other.value)

    override fun equals(other: Any?) = other is Quantity && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = asString

    companion object {
        val ZERO = Quantity(BigDecimal.ZERO)

        fun parse(text: String): AppResult<Quantity> = try {
            AppResult.Success(Quantity(BigDecimal.parseString(text.trim())))
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unexpected("Bad quantity: $text"))
        }
    }
}