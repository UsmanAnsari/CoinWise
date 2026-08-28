package com.uansari.coinwise.money

object MoneyFormatter {

    private val FIAT_SYMBOLS = mapOf("GBP" to "£", "USD" to "$", "EUR" to "€")

    private const val SIGNIFICANT_FIGURES_BELOW_ONE_CENT = 4

    fun format(money: Money): String {
        val symbol = FIAT_SYMBOLS[money.asset.raw]
        val body = formatAmount(money.amountString)
        return if (symbol != null) {
            if (body.startsWith("-")) "-$symbol${body.drop(1)}" else "$symbol$body"
        } else {
            "$body ${money.asset.raw}"
        }
    }

    /** "+2.40%" / "-1.05%". Always signed — direction is the point. */
    fun formatPercent(percent: Percent, decimals: Int = 2): String {
        val scaled = shiftDecimalPointRight(percent.asString, places = 2)
        val rounded = roundToDecimals(scaled, decimals)
        val sign = if (rounded.startsWith("-")) "" else "+"
        return "$sign$rounded%"
    }


    /**
     *   zero             -> 0.00
     *   >= 1.00          -> 2 decimals            104,203.11
     *   0.01 <= v < 1.00 -> 4 decimals            0.4312
     *   < 0.01           -> 4 significant figures 0.000004310
     */
    internal fun formatAmount(raw: String): String {
        val negative = raw.startsWith("-")
        val magnitude = raw.removePrefix("-")

        val formatted = when {
            isZeroString(magnitude) -> roundToDecimals("0", 2)
            isAtLeast(magnitude, "1") -> groupThousands(roundToDecimals(magnitude, 2))
            isAtLeast(magnitude, "0.01") -> roundToDecimals(magnitude, 4)
            else -> toSignificantFigures(magnitude, SIGNIFICANT_FIGURES_BELOW_ONE_CENT)
        }
        return if (negative && !isAllZeros(formatted)) "-$formatted" else formatted
    }

    // ── string arithmetic ─────────────────────────────────────────────────

    private fun isZeroString(value: String) = value.all { it == '0' || it == '.' }

    private fun isAtLeast(value: String, threshold: String): Boolean {
        val (vInt, vFrac) = split(value)
        val (tInt, tFrac) = split(threshold)
        val vTrimmed = vInt.trimStart('0').ifEmpty { "0" }
        val tTrimmed = tInt.trimStart('0').ifEmpty { "0" }
        if (vTrimmed.length != tTrimmed.length) return vTrimmed.length > tTrimmed.length
        if (vTrimmed != tTrimmed) return vTrimmed > tTrimmed
        val width = maxOf(vFrac.length, tFrac.length)
        return vFrac.padEnd(width, '0') >= tFrac.padEnd(width, '0')
    }

    private fun roundToDecimals(value: String, decimals: Int): String {
        if (value.startsWith("-")) {
            val magnitude = roundToDecimals(value.removePrefix("-"), decimals)
            return if (isAllZeros(magnitude)) magnitude else "-$magnitude"
        }
        val (intPart, fracPart) = split(value)
        if (fracPart.length <= decimals) return "$intPart.${fracPart.padEnd(decimals, '0')}"

        val kept = fracPart.take(decimals)
        val nextDigit = fracPart[decimals]
        val remainder = fracPart.drop(decimals + 1)
        val roundUp = nextDigit > '5' || (nextDigit == '5' && remainder.any { it != '0' }) ||
                // HALF_EVEN: exact .5 rounds toward the even neighbour, matching
                // MONEY_MODE so display and arithmetic rounding agree.
                (nextDigit == '5' && remainder.all { it == '0' } && lastDigitOf(
                    intPart, kept
                ).digitToInt() % 2 == 1)

        if (!roundUp) return "$intPart.$kept".trimEnd('.')

        val carried = incrementDecimalString(intPart + kept)
        val newInt = carried.dropLast(decimals).ifEmpty { "0" }
        val newFrac = carried.takeLast(decimals)
        return if (decimals == 0) newInt else "$newInt.$newFrac"
    }

    private fun toSignificantFigures(value: String, figures: Int): String {
        val (_, fracPart) = split(value)
        val leadingZeros = fracPart.takeWhile { it == '0' }.length
        return roundToDecimals(value, leadingZeros + figures)
    }

    private fun groupThousands(value: String): String {
        val (intPart, fracPart) = split(value)
        val grouped = intPart.reversed().chunked(3).joinToString(",").reversed()
        return if (fracPart.isEmpty()) grouped else "$grouped.$fracPart"
    }

    private fun shiftDecimalPointRight(value: String, places: Int): String {
        val negative = value.startsWith("-")
        val (intPart, fracPart) = split(value.removePrefix("-"))
        val digits = intPart + fracPart.padEnd(places, '0')
        val newPoint = intPart.length + places
        val whole = digits.take(newPoint).trimStart('0').ifEmpty { "0" }
        val rest = digits.drop(newPoint)
        val result = if (rest.isEmpty()) whole else "$whole.$rest"
        return if (negative) "-$result" else result
    }

    private fun incrementDecimalString(digits: String): String {
        val chars = digits.toCharArray()
        for (i in chars.indices.reversed()) {
            if (chars[i] != '9') {
                chars[i] = chars[i] + 1
                return chars.concatToString()
            }
            chars[i] = '0'
        }
        return "1" + chars.concatToString()
    }

    private fun split(value: String): Pair<String, String> {
        val index = value.indexOf('.')
        return if (index < 0) value to "" else value.take(index) to value.drop(index + 1)
    }

    private fun lastDigitOf(intPart: String, kept: String): Char =
        kept.lastOrNull() ?: intPart.lastOrNull() ?: '0'

    private fun isAllZeros(value: String) = value.all { it == '0' || it == '.' || it == ',' }
}