package com.nickpatrick.swissmoneysaver.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.ConfigurationRepository
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.ui.entry.ItemDetails
import com.nickpatrick.swissmoneysaver.ui.entry.ItemUiState
import com.nickpatrick.swissmoneysaver.ui.entry.toItem
import com.nickpatrick.swissmoneysaver.ui.entry.toItemUiState
import com.nickpatrick.swissmoneysaver.util.Utilities
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    val itemsRepository: ItemsRepository,
    private val configurationRepository: ConfigurationRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[DetailsDestination.itemIdArg])

    var configUiState by mutableStateOf(Configuration())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemStream(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(true)

            val itemTimestamp = itemUiState.itemDetails.timestamp
            val year = Utilities.getDateFromTimestamp(itemTimestamp).year
            configUiState = configurationRepository.getConfigurationForYear(year.toString())
                .filterNotNull()
                .first()
        }
    }

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
            description.isNotBlank()
        }
    }

    suspend fun saveItem() {

        println("DetailsScreen - saveItem()")

        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    suspend fun deleteItem() {
        itemsRepository.deleteItem(itemUiState.itemDetails.toItem())
    }

    suspend fun getConfigurationForYear(year: Int): Configuration {
        return configurationRepository.getConfigurationForYear(year.toString())
            .filterNotNull()
            .first()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for ItemDetailsScreen
 */
data class DetailsUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
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