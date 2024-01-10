package com.nickpatrick.swissmoneysaver.ui.budgetDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.data.BudgetItem
import com.nickpatrick.swissmoneysaver.data.BudgetItemsRepository
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.ui.entry.ItemDetails
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BudgetDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val budgetItemsRepository: BudgetItemsRepository,
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var budgetItemUiState by mutableStateOf(BudgetItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[BudgetDetailsDestination.itemIdArg])

    init {
        viewModelScope.launch {
            budgetItemUiState = budgetItemsRepository.getBudgetItemStream(itemId)
                .filterNotNull()
                .first()
                .toBudgetItemUiState(true)
        }
    }

    /**
     * Update the item in the [ItemsRepository]'s data source
     */
    suspend fun updateItem() {
        if (validateInput(budgetItemUiState.budgetItemDetails)) {
            budgetItemsRepository.updateBudgetItem(budgetItemUiState.budgetItemDetails.toBudgetItem())
        }
    }

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateBudgetUiState(budgetItemDetails: BudgetItemDetails) {
        budgetItemUiState =
            BudgetItemUiState(budgetItemDetails = budgetItemDetails, isEntryValid = validateInput(budgetItemDetails))

        println("BudgetDetailsViewModel - updateBudgetUiState - budgetItemDetails: ${budgetItemDetails.amount} / ${budgetItemDetails.debit}")
    }

    private fun validateInput(uiState: BudgetItemDetails = budgetItemUiState.budgetItemDetails): Boolean {

        return with(uiState) {
            name.isNotBlank()
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            budgetItemsRepository.insertBudgetItem(budgetItemUiState.budgetItemDetails.toBudgetItem())

            println("BudgetDetailsViewModel - saveItem: ${budgetItemUiState.budgetItemDetails}")
        }
    }

    suspend fun deleteItem() {
        budgetItemsRepository.deleteBudgetItem(budgetItemUiState.budgetItemDetails.toBudgetItem())

        println("BudgetDetailsViewModel - deleteItem: ${budgetItemUiState.budgetItemDetails}")
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

fun BudgetItem.toBudgetItemUiState(isEntryValid: Boolean = false): BudgetItemUiState = BudgetItemUiState(
    budgetItemDetails = this.toBudgetItemDetails(),
    isEntryValid = isEntryValid
)


/**
 * Extension function to convert [ItemDetails] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */

fun BudgetItem.toBudgetItemDetails(): BudgetItemDetails = BudgetItemDetails(
    id = id,
    timestamp = timestamp,
    name = name,
    description = description,
    type = type,
    amount = amount,
    balance = 0.0,
    debit = debit
)
fun BudgetItemDetails.toBudgetItem(): BudgetItem = BudgetItem(
    id = id,
    timestamp = timestamp,
    name = name,
    description = description,
    type = type,
    amount = amount,
    balance = 0.0,
    debit = debit
)

/**
 * UI state for ItemDetailsScreen
 */
data class BudgetItemUiState(
    val budgetItemDetails: BudgetItemDetails = BudgetItemDetails(),
    val isEntryValid: Boolean = false
)

data class BudgetItemDetails(
    val id: Int = 0,
    var timestamp: Long = 0,
    val name: String = "",
    val description: String = "",
    val type: Int = 0,
    val amount: Double = 0.00,
    val balance: Double = 0.00,
    val debit: Boolean = false)

