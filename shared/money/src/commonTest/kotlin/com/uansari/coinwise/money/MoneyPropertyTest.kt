package com.uansari.coinwise.money

import com.uansari.coinwise.common.AppResult
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.fail

class MoneyPropertyTest {

    private companion object {
        const val ITERATIONS = 1000
        const val SEED = 20260827
    }

    private val random = Random(SEED)
    private val gbp = AssetCode.GBP

    private fun money(text: String, asset: AssetCode = gbp): Money =
        when (val result = Money.parse(text, asset)) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> fail("Could not parse '$text': ${result.error}")
        }

    /**
     * Plain decimal string for mantissa × 10^exponent.
     * Never scientific notation — that is one of the things under test, so the
     * generator must not be capable of producing it.
     *
     *   (431, -9)    -> "0.000000431"
     *   (104203, -2) -> "1042.03"
     *   (5, 3)       -> "5000"
     */
    private fun buildAmountString(mantissa: Int, exponent: Int): String {
        val digits = mantissa.toString()
        return when {
            exponent >= 0 -> digits + "0".repeat(exponent)
            -exponent < digits.length -> {
                val split = digits.length + exponent
                digits.substring(0, split) + "." + digits.substring(split)
            }

            else -> "0." + "0".repeat(-exponent - digits.length) + digits
        }
    }

    /** Spans ~1e-12 to ~1e12, signed. Negatives matter: P/L is the main consumer. */
    private fun randomMoney(asset: AssetCode = gbp): Money {
        val text = buildAmountString(
            mantissa = random.nextInt(1, 1_000_000),
            exponent = random.nextInt(-12, 7),
        )
        return money(if (random.nextBoolean()) "-$text" else text, asset)
    }

    @Test
    fun `addition round-trips`() = repeat(ITERATIONS) {
        val a = randomMoney()
        val b = randomMoney()
        assertEquals(a, (a + b) - b, "a=$a b=$b")
    }

    @Test
    fun `addition is associative`() = repeat(ITERATIONS) {
        val a = randomMoney()
        val b = randomMoney()
        val c = randomMoney()
        assertEquals((a + b) + c, a + (b + c), "a=$a b=$b c=$c")
    }

    @Test
    fun `zero is the additive identity`() = repeat(ITERATIONS) {
        val a = randomMoney()
        assertEquals(a, a + Money.zero(gbp), "a=$a")
    }

    @Test
    fun `negation is an involution`() = repeat(ITERATIONS) {
        val a = randomMoney()
        assertEquals(a, -(-a), "a=$a")
    }

    @Test
    fun `parse round-trips through amountString`() = repeat(ITERATIONS) {
        val a = randomMoney()
        assertEquals(a, money(a.amountString), "a=$a amountString=${a.amountString}")
    }

    @Test
    fun `amountString never uses scientific notation`() = repeat(ITERATIONS) {
        val a = randomMoney()
        assertFalse(a.amountString.contains('e', ignoreCase = true), "a=${a.amountString}")
    }

    @Test
    fun `comparison is consistent with equality`() = repeat(ITERATIONS) {
        val a = randomMoney()
        val b = randomMoney()
        assertEquals(a == b, a.compareTo(b) == 0, "a=$a b=$b")
    }

    @Test
    fun `mixing assets throws`() {
        assertFailsWith<IllegalArgumentException> {
            Money.zero(AssetCode.GBP) + Money.zero(AssetCode.of("btc"))
        }
    }

    @Test
    fun `normalised asset codes are equal`() {
        assertEquals(AssetCode.of("btc"), AssetCode.of("BTC"))
    }
}