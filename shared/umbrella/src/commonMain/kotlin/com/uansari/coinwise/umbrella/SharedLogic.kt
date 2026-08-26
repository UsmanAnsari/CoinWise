package com.uansari.coinwise.umbrella

import com.uansari.coinwise.domain.Coin
import com.uansari.coinwise.money.AssetCode
import com.uansari.coinwise.money.Money

object SharedLogic {
    fun sampleCoin(): Coin = Coin(
        id = "bitcoin",
        symbol = "BTC",
        price = Money("104203.11", AssetCode("GBP")),
        change24h = 2.4,
    )
}