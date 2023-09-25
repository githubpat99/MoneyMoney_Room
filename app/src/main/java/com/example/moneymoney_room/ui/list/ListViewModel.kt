package com.example.moneymoney_room.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */

class ListViewModel(itemsRepository: ItemsRepository) : ViewModel() {

//    val currentDateTime: java.util.Date = java.util.Date()
//    val currentTimestamp: Long = currentDateTime.time
//    val itemsToAdd = listOf(
//        Item(1, currentTimestamp, "Haushalt", "Einkauf", 2, -250.00, 0.00, false),
//        Item(2, currentTimestamp, "Miete", "Monatskosten", 2, -250.00, 0.00, false),
//        Item(3, currentTimestamp, "Krankenkasse", "Prämie", 2, -250.00, 0.00, false),
//    )
//
//    val listUiState = ListUiState(itemsToAdd.toMutableList())


    val listUiState: StateFlow<ListUiState> =
        itemsRepository.getAllItemsStream().map { ListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ListUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

    }

}

data class ListUiState(
    val list: List<Item> = listOf()
)