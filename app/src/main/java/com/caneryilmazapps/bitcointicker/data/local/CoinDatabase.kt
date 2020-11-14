package com.caneryilmazapps.bitcointicker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caneryilmazapps.bitcointicker.data.models.response.CoinResponse

@Database(entities = [CoinResponse::class], version = 1)
abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        @Volatile
        private var instance: CoinDatabase? = null

        fun getDatabase(context: Context): CoinDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, CoinDatabase::class.java, "CoinDatabase")
                .fallbackToDestructiveMigration()
                .build()
    }
}