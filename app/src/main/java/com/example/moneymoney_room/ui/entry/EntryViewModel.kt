package com.example.moneymoney_room.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class EntryViewModel(
    private val itemsRepository: ItemsRepository,
) : ViewModel() {

    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            if (timestamp == 0L) {
                timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
            name.isNotBlank() && description.isNotBlank()
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }
}

/**
 * UI state for ItemDetailsScreen
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false,
    val entries: List<String> = listOf(
        "Haushalt",
        "Versicherung ",
        "Krankenkasse ",
        "Miete ",
        "Ausgang ",
        "Geschenke ",
        "Steuern ",
        "Nebenkosten ",
        "Diverses",
        "Einkommen ",
        "Boni"
    )
)

data class ItemDetails(
    val id: Int = 0,
    var timestamp: Long = 0,
    val name: String = "",
    val description: String = "",
    val type: Int = 0,
    val amount: Double = 0.00,
    val balance: Double = 0.00,
    val debit: Boolean = false
)

/**
 * Extension function to convert [ItemDetails] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    timestamp = timestamp,
    name = name,
    description = description,
    type = type,
    amount = amount,
    balance = 0.0,
    debit = false
)

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)


/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    timestamp = timestamp,
    name = name,
    description = description,
    type = type,
    amount = amount,
    balance = 0.0,
    debit = false
)