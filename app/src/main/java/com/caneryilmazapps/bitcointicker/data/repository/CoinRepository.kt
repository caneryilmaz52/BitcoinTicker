package com.caneryilmazapps.bitcointicker.data.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.caneryilmazapps.bitcointicker.data.local.CoinDao
import com.caneryilmazapps.bitcointicker.data.models.response.CoinResponse
import com.caneryilmazapps.bitcointicker.data.remote.CoinApi
import com.caneryilmazapps.bitcointicker.data.remote.FirebaseHelper
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val coinApi: CoinApi,
    private val firebaseHelper: FirebaseHelper,
    private val coinDao: CoinDao
) {

    suspend fun signInFirebase() = firebaseHelper.signInFirebase()

    suspend fun checkApiStatus() = coinApi.checkApiStatus()

    suspend fun getCoinList() = coinApi.getCoinList()

    suspend fun insertCoinListLocalDatabase(coinList: List<CoinResponse>) =
        coinDao.insertCoinList(coinList)

    suspend fun searchCoinByNameOrSymbol(query: SupportSQLiteQuery) = coinDao.searchCoin(query)

    suspend fun getCoinDetail(id: String) = coinApi.getCoinDetail(id)

    suspend fun saveFavoriteCoin() = firebaseHelper.favoriteCoinsCollection()

    suspend fun deleteFavoriteCoin() = firebaseHelper.favoriteCoinsCollection()

    suspend fun getFavoriteCoins() = firebaseHelper.favoriteCoinsCollection()
}