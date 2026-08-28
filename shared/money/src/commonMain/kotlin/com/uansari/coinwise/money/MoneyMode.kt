package com.uansari.coinwise.money

import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

internal val MONEY_MODE = DecimalMode(
    decimalPrecision = 34,
    roundingMode = RoundingMode.ROUND_HALF_TO_EVEN,
)