package com.uansari.coinwise.money

import com.ionspin.kotlin.bignum.decimal.BigDecimal

class Percent internal constructor(
    internal val fraction: BigDecimal,
) : Comparable<Percent> {

    internal val asString: String get() = fraction.toStringExpanded()

    val isNegative: Boolean get() = fraction.signum() < 0
    val isZero: Boolean get() = fraction.isZero()

    override fun compareTo(other: Percent): Int = fraction.compareTo(other.fraction)

    override fun equals(other: Any?) = other is Percent && fraction == other.fraction
    override fun hashCode() = fraction.hashCode()
    override fun toString() = asString

    companion object {
        val ZERO = Percent(BigDecimal.ZERO)
    }
}