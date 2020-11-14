package com.caneryilmazapps.bitcointicker.di

import android.content.Context
import com.caneryilmazapps.bitcointicker.adapters.CoinListAdapter
import com.caneryilmazapps.bitcointicker.data.local.CoinDao
import com.caneryilmazapps.bitcointicker.data.local.CoinDatabase
import com.caneryilmazapps.bitcointicker.data.remote.CoinApi
import com.caneryilmazapps.bitcointicker.data.remote.FirebaseHelper
import com.caneryilmazapps.bitcointicker.data.remote.RetrofitInstance
import com.caneryilmazapps.bitcointicker.data.repository.CoinRepository
import com.caneryilmazapps.bitcointicker.ui.main.MainViewModel
import com.caneryilmazapps.bitcointicker.ui.splash.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MainAppModule {

    @Singleton
    @Provides
    fun provideFirebaseHelper(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): FirebaseHelper = FirebaseHelper(firebaseAuth, firebaseFirestore)

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideRetrofitApi(): CoinApi = RetrofitInstance.api

    @Provides
    fun provideSplashViewModel(coinRepository: CoinRepository): SplashViewModel =
        SplashViewModel(coinRepository)

    @Provides
    fun provideMainViewModel(coinRepository: CoinRepository): MainViewModel =
        MainViewModel(coinRepository)

    @Provides
    fun provideCoinListAdapter(): CoinListAdapter = CoinListAdapter()

    @Singleton
    @Provides
    fun provideCoinDatabase(@ApplicationContext appContext: Context) =
        CoinDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideCoinDao(coinDatabase: CoinDatabase) = coinDatabase.coinDao()

    @Singleton
    @Provides
    fun provideCoinRepository(coinApi: CoinApi, firebaseHelper: FirebaseHelper, coinDao: CoinDao) =
        CoinRepository(coinApi, firebaseHelper, coinDao)
}