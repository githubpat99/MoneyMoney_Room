package com.example.moneymoney_room

import android.app.Application
import com.example.moneymoney_room.data.AppContainer
import com.example.moneymoney_room.data.AppDataContainer
import java.util.TimeZone

class MoneyMoneyApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    lateinit var appTimeZone: TimeZone
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        // Set the default time zone for the app
        appTimeZone = TimeZone.getDefault()
    }

    companion object {
        lateinit var instance: MoneyMoneyApplication
            private set
    }

    init {
        instance = this
    }
}