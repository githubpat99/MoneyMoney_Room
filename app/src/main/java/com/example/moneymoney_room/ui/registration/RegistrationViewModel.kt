package com.example.moneymoney_room.ui.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel() : ViewModel() {
    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(registrationUiState: RegistrationUiState) {
        this.registrationUiState =
            RegistrationUiState(userId = registrationUiState.userId,
                password = registrationUiState.password,
                email = registrationUiState.email)
    }

    fun saveItem() {
        TODO("Not yet implemented")
    }

    var registrationUiState by mutableStateOf(RegistrationUiState())
        private set

}

data class RegistrationUiState(
    val userId: String = "",
    val password: String = "",
    val email: String = ""
)
