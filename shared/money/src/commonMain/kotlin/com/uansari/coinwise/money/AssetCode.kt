package com.uansari.coinwise.money

import kotlin.jvm.JvmInline

@JvmInline
value class AssetCode(val raw: String) {

    init {
        require(raw.isNotBlank()) { "AssetCode cannot be blank" }
    }

    override fun toString(): String = raw

    companion object {
        fun of(raw: String): AssetCode = AssetCode(raw.trim().uppercase())

        val GBP = AssetCode("GBP")
        val USD = AssetCode("USD")
        val EUR = AssetCode("EUR")
    }
}