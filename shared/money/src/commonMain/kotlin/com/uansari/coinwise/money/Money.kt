package com.uansari.coinwise.money

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.uansari.coinwise.common.AppError
import com.uansari.coinwise.common.AppResult

class Money private constructor(
    internal val amount: BigDecimal,
    val asset: AssetCode,
) : Comparable<Money> {

    /** Full precision, never scientific notation. This is the storage form. */
    val amountString: String get() = amount.toStringExpanded()

    val isZero: Boolean get() = amount.isZero()
    val isNegative: Boolean get() = amount.signum() < 0

    operator fun plus(other: Money): Money {
        requireSameAsset(other)
        return Money(amount + other.amount, asset)
    }

    operator fun minus(other: Money): Money {
        requireSameAsset(other)
        return Money(amount - other.amount, asset)
    }

    operator fun times(quantity: Quantity): Money =
        Money(amount.multiply(quantity.value, MONEY_MODE), asset)

    operator fun div(quantity: Quantity): Money =
        Money(amount.divide(quantity.value, MONEY_MODE), asset)

    operator fun unaryMinus(): Money = Money(amount.negate(), asset)

    override fun compareTo(other: Money): Int {
        requireSameAsset(other)
        return amount.compareTo(other.amount)
    }

    /** Proportional change from [base]. Percent.ZERO means unchanged. */
    fun percentChangeFrom(base: Money): Percent {
        requireSameAsset(base)
        require(!base.amount.isZero()) { "Percent change from zero is undefined" }
        return Percent((amount - base.amount).divide(base.amount, MONEY_MODE))
    }

    private fun requireSameAsset(other: Money) {
        require(asset == other.asset) {
            "Cannot combine ${asset.raw} with ${other.asset.raw}"
        }
    }

    override fun equals(other: Any?) =
        other is Money && asset == other.asset && amount == other.amount

    override fun hashCode() = 31 * amount.hashCode() + asset.hashCode()

    override fun toString() = "$amountString ${asset.raw}"

    companion object {
        fun zero(asset: AssetCode) = Money(BigDecimal.ZERO, asset)

        /** The only construction path from untrusted input. */
        fun parse(text: String, asset: AssetCode): AppResult<Money> = try {
            AppResult.Success(Money(BigDecimal.parseString(text.trim()), asset))
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unexpected("Bad amount: $text"))
        }
    }
}