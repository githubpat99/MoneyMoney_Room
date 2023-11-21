package com.example.moneymoney_room.data

import kotlinx.coroutines.flow.Flow

class ConfigurationRepository(private val configurationDao: ConfigurationDao) {
    fun getConfiguration(): Flow<Configuration?> {
        return configurationDao.getConfiguration()
    }
}
