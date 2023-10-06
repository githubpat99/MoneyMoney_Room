package com.example.moneymoney_room

import android.app.Application
import com.example.moneymoney_room.data.AppContainer
import com.example.moneymoney_room.data.AppDataContainer

class MoneyMoneyApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}