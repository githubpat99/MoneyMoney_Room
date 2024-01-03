package com.nickpatrick.swissmoneysaver

import android.app.Application
import com.nickpatrick.swissmoneysaver.data.AppContainer
import com.nickpatrick.swissmoneysaver.data.AppDataContainer
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