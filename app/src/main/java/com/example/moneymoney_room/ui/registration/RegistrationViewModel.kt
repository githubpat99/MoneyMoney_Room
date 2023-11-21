package com.example.moneymoney_room.ui.registration

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

class RegistrationViewModel(application: MoneyMoneyApplication) : ViewModel() {

    val context = application.applicationContext
    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()

    // Initialize registrationUiState with values from configuration
    var registrationUiState: MutableState<RegistrationUiState> = mutableStateOf(
        RegistrationUiState()
    )

    init {
        viewModelScope.launch {
            val configurationValue = configuration.first() // Wait for the first value
            registrationUiState.value = RegistrationUiState(
                startSaldo = configurationValue?.startSaldo ?: 0.0,
                userName = configurationValue?.userName ?: "",
                password = configurationValue?.password ?: "",
                email = configurationValue?.email ?: ""
            )
        }
    }

    fun updateUiState(registrationUiState: RegistrationUiState) {
        this.registrationUiState.value = registrationUiState
    }


    fun updateConfiguration(registrationUiState: RegistrationUiState) {
        val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(context)
        val updConfig = Configuration(
            ts = 1672531200,
            startSaldo = registrationUiState.startSaldo,
            userName = registrationUiState.userName,
            password = registrationUiState.password,
            email = registrationUiState.email,
            approxStartSaldo = 0.0,
            approxEndSaldo = 0.0
        )
        viewModelScope.launch {

            moneyMoneyDatabase.configurationDao().insert(updConfig)
        }
    }

}

data class RegistrationUiState(
    var userName: String = "",
    var password: String = "",
    var email: String = "",
    var startSaldo: Double = 0.0,
)
