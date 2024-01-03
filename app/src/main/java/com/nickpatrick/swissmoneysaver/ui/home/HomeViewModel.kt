package com.nickpatrick.swissmoneysaver.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.MoneyMoneyApplication
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.data.MoneyMoneyDatabase
import com.nickpatrick.swissmoneysaver.ui.overview.OverviewUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(
    val itemsRepository: ItemsRepository,
    application: MoneyMoneyApplication,
) : ViewModel() {

    val context = application.applicationContext

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    val configItems: Flow<List<Configuration?>> =
        moneyMoneyDatabase.configurationDao().getConfigurations()

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()
    var homeUiState by mutableStateOf(HomeUiState())
        private set

    // my new Approach
    private val _configs = MutableStateFlow<List<Configuration?>>(emptyList())

    // Initialize overviewUiState with values from configuration
    var overviewUiState: MutableState<OverviewUiState> = mutableStateOf(
        OverviewUiState()
    )

    val year = LocalDateTime.now().year
    val ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

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
            moneyMoneyDatabase.configurationDao().getConfigurations().collect {
                _configs.value = it
            }
        }
    }


    fun chgUserInfo(paramHomeUiState: HomeUiState) {
        homeUiState =
            HomeUiState(userId = paramHomeUiState.userId, password = paramHomeUiState.password)
    }

    fun initializeConfigForYear() {
        // initialize Configuration for actual year

        val configuration = Configuration(
            budgetYear = year,
            ts = ts,
            status = 0,
            startSaldo = 0.0,
            endSaldo = 0.0,
            approxStartSaldo = 0.0,
            approxEndSaldo = 0.0
        )

        viewModelScope.launch {
            moneyMoneyDatabase.configurationDao().insert(configuration)
        }
    }

    fun updateStatusToArchived(config: Configuration) {

        val configuration = config.copy()
        configuration.status = 2

        viewModelScope.launch {
            moneyMoneyDatabase.configurationDao().updateConfiguration(configuration)
        }
    }

    fun getVisibleIdx(configItems: List<Configuration?>): Int {
        var idx = 0
        val currentConfigs = configItems

        for (config in currentConfigs) {

            if (config != null) {
                if (config.budgetYear == year) {
                    break
                }
            }
            idx++
        }
        return idx
    }
}

data class HomeUiState(
    val userId: String = "MoneyMoney - Demo",
    val password: String = "Viel Spass...",
)

