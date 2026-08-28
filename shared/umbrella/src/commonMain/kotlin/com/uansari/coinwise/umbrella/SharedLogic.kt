package com.uansari.coinwise.umbrella

import com.uansari.coinwise.common.AppResult
import com.uansari.coinwise.domain.Coin
import com.uansari.coinwise.money.AssetCode
import com.uansari.coinwise.money.Money

object SharedLogic {

    fun sampleCoin(): Coin = Coin(
        id = "bitcoin",
        symbol = "BTC",
        price = when (val price = Money.parse("104203.11", AssetCode.GBP)) {
            is AppResult.Success -> price.value
            is AppResult.Failure -> Money.zero(AssetCode.GBP)
        },
        change24h = 2.4,
    )
}