package com.caneryilmazapps.bitcointicker.data.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.caneryilmazapps.bitcointicker.data.models.response.CoinResponse

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinList(coinList: List<CoinResponse>): List<Long>

    @RawQuery(observedEntities = [CoinResponse::class])
    suspend fun searchCoin(query: SupportSQLiteQuery): List<CoinResponse>
}