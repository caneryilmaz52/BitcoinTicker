package com.caneryilmazapps.bitcointicker.data.remote

import com.caneryilmazapps.bitcointicker.data.models.response.ApiStatusResponse
import com.caneryilmazapps.bitcointicker.data.models.response.CoinDetailResponse
import com.caneryilmazapps.bitcointicker.data.models.response.CoinResponse
import com.caneryilmazapps.bitcointicker.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinApi {
    @GET(Constants.checkApiStatus)
    suspend fun checkApiStatus(): Response<ApiStatusResponse>

    @GET(Constants.getCoinList)
    suspend fun getCoinList(): Response<List<CoinResponse>>

    @GET("${Constants.getCoinDetail}/{id}")
    suspend fun getCoinDetail(@Path("id") id: String): Response<CoinDetailResponse>
}