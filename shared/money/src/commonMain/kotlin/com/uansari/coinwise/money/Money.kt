package com.uansari.coinwise.money

import kotlin.jvm.JvmInline

@JvmInline
value class AssetCode(val raw: String)

data class Money(
    val amountRaw: String,
    val asset: AssetCode,
)