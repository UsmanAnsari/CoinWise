package com.uansari.coinwise.domain

import com.uansari.coinwise.money.Money

data class Coin(
    val id: String,
    val symbol: String,
    val price: Money,
    val change24h: Double,
)