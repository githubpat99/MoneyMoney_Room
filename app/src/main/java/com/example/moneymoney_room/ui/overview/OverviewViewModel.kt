package com.example.moneymoney_room.ui.overview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class OverviewViewModel(application: MoneyMoneyApplication) : ViewModel() {

    val context = application.applicationContext
    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()

    // Initialize overviewUiState with values from configuration
    var overviewUiState: MutableState<OverviewUiState> = mutableStateOf(
        OverviewUiState()
    )

    init {
        viewModelScope.launch {
            val configurationValue = configuration.first() // Wait for the first value
            overviewUiState.value = OverviewUiState(
                status = configurationValue?.status ?: 0,
                ts = configurationValue?.ts ?: LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                startSaldo = configurationValue?.startSaldo ?: 0.0,
                budgetYear = configurationValue?.budgetYear ?: 0,
                password = configurationValue?.password ?: "",
                email = configurationValue?.email ?: "",
                approxStartSaldo = configurationValue?.approxStartSaldo ?: 0.0,
                approxEndSaldo = configurationValue?.approxEndSaldo ?: 0.0
            )
        }
    }

    fun updateUiState(overviewUiState: OverviewUiState) {
        this.overviewUiState.value = overviewUiState
    }


    fun updateConfiguration(overviewUiState: OverviewUiState) {
        val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(context)
        val updConfig = Configuration(
            ts = 1672531200,
            startSaldo = overviewUiState.startSaldo,
            endSaldo = overviewUiState.endSaldo,
            budgetYear = overviewUiState.budgetYear,
            password = overviewUiState.password,
            email = overviewUiState.email,
            approxStartSaldo = 0.0,
            approxEndSaldo = 0.0
        )
        viewModelScope.launch {

            moneyMoneyDatabase.configurationDao().insert(updConfig)
        }
    }

}

data class OverviewUiState(
    var ts: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    var status: Int = 0,
    var budgetYear: Int = 0,
    var password: String = "",
    var email: String = "",
    var startSaldo: Double = 0.0,
    var endSaldo: Double = 0.0,
    var approxStartSaldo: Double = 0.0,
    var approxEndSaldo: Double = 0.0
)
