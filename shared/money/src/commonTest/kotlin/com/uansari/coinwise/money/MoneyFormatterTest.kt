package com.uansari.coinwise.money

import com.uansari.coinwise.common.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MoneyFormatterTest {

    private fun gbp(text: String): Money = when (val r = Money.parse(text, AssetCode.GBP)) {
        is AppResult.Success -> r.value
        is AppResult.Failure -> fail("Could not parse '$text'")
    }

    private fun btc(text: String): Money = when (val r = Money.parse(text, AssetCode.of("btc"))) {
        is AppResult.Success -> r.value
        is AppResult.Failure -> fail("Could not parse '$text'")
    }


    @Test
    fun `groups thousands`() = assertEquals("£104,203.11", MoneyFormatter.format(gbp("104203.11")))

    @Test
    fun `groups millions`() = assertEquals("£1,000,000.00", MoneyFormatter.format(gbp("1000000")))

    @Test
    fun `no separator below one thousand`() =
        assertEquals("£999.99", MoneyFormatter.format(gbp("999.99")))

    @Test
    fun `pads to two decimals`() = assertEquals("£5.00", MoneyFormatter.format(gbp("5")))

    @Test
    fun `exactly one enters band one`() = assertEquals("£1.00", MoneyFormatter.format(gbp("1")))

    @Test
    fun `rounds half to even at two decimals`() =
        assertEquals("£2.02", MoneyFormatter.format(gbp("2.025")))


    @Test
    fun `four decimals below one`() = assertEquals("£0.4312", MoneyFormatter.format(gbp("0.43124")))

    @Test
    fun `one cent stays in band two`() = assertEquals("£0.0100", MoneyFormatter.format(gbp("0.01")))

    /**
     * The band comes from the unrounded value, so this stays in band 2 rather
     * than rounding up into band 1. Deciding the band after rounding would be
     * circular.
     */
    @Test
    fun `just under one stays in band two`() =
        assertEquals("£0.9990", MoneyFormatter.format(gbp("0.999")))

    // ── band 3: below one cent, four significant figures ────────────────

    @Test
    fun `significant figures below one cent`() =
        assertEquals("£0.000004310", MoneyFormatter.format(gbp("0.00000431")))

    @Test
    fun `very small values keep four figures`() =
        assertEquals("£0.0000000001234", MoneyFormatter.format(gbp("0.00000000012344")))

    @Test
    fun `small negatives keep their sign and precision`() =
        assertEquals("-£0.000004310", MoneyFormatter.format(gbp("-0.00000431")))

    // ── sign and zero ───────────────────────────────────────────────────

    @Test
    fun `negative sign precedes the symbol`() =
        assertEquals("-£1,234.56", MoneyFormatter.format(gbp("-1234.56")))

    @Test
    fun `zero renders in band one`() = assertEquals("£0.00", MoneyFormatter.format(gbp("0")))

    @Test
    fun `trailing-zero decimals are still zero`() =
        assertEquals("£0.00", MoneyFormatter.format(gbp("0.000")))

    // ── non-fiat ────────────────────────────────────────────────────────

    @Test
    fun `crypto uses a code suffix`() = assertEquals("0.5000 BTC", MoneyFormatter.format(btc("0.5")))

    // ── percent ─────────────────────────────────────────────────────────

    @Test
    fun `positive percent is signed`() = assertEquals(
        "+2.40%", MoneyFormatter.formatPercent(gbp("102.4").percentChangeFrom(gbp("100")))
    )

    @Test
    fun `negative percent is signed`() = assertEquals(
        "-5.00%", MoneyFormatter.formatPercent(gbp("95").percentChangeFrom(gbp("100")))
    )

    @Test
    fun `zero change is signed positive`() = assertEquals(
        "+0.00%", MoneyFormatter.formatPercent(gbp("100").percentChangeFrom(gbp("100")))
    )

    /**
     * Regression: the sign guard in roundToDecimals. Without it,
     * incrementDecimalString does char arithmetic on '-' and returns garbage.
     */
    @Test
    fun `a percent that rounds away does not render as negative zero`() = assertEquals(
        "+0.00%", MoneyFormatter.formatPercent(gbp("99.999").percentChangeFrom(gbp("100")))
    )

}