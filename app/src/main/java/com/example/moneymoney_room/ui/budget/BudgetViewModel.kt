package com.example.moneymoney_room.ui.budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.ui.entry.ItemDetails
import com.example.moneymoney_room.ui.entry.toItem
import java.time.LocalDateTime
import java.time.ZoneOffset

class BudgetViewModel(
    private val itemsRepository: ItemsRepository,
) : ViewModel() {

    var budgetUiState by mutableStateOf(BudgetUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        budgetUiState =
            BudgetUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))

        println("BudgetViewModel - budgetUiState = $budgetUiState")
    }

    private fun validateInput(uiState: ItemDetails = budgetUiState.itemDetails): Boolean {
        return with(uiState) {
            if (timestamp == 0L) {
                timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
            name.isNotBlank()    // todo PIN: more to check later
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(budgetUiState.itemDetails.toItem())
        }
    }

}

data class BudgetUiState(
    val userId: String = "",
    val password: String = "",
    val actualDate: Long = 0,       // todo PIN: this will be the date the Budget was built
    val isEntryValid: Boolean = false,
    val itemDetails: ItemDetails = ItemDetails()
)

