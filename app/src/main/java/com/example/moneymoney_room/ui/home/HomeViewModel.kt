package com.example.moneymoney_room.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import com.example.moneymoney_room.ui.overview.OverviewUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(
    private val itemsRepository: ItemsRepository,
    application: MoneyMoneyApplication
) : ViewModel() {

    val context = application.applicationContext

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    val configItems: Flow<List<Configuration?>> =
        moneyMoneyDatabase.configurationDao().getConfigurations()

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()
    var homeUiState by mutableStateOf(HomeUiState())
        private set

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


    fun chgUserInfo(paramHomeUiState: HomeUiState) {
        homeUiState =
            HomeUiState(userId = paramHomeUiState.userId, password = paramHomeUiState.password)
    }

    suspend fun deleteItems() {
        itemsRepository.deleteAllItems()
    }
}

data class HomeUiState(
    val userId: String = "Demoversion",
    val password: String = "Passwort",
)

