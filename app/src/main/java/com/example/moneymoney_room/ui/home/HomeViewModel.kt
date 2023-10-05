package com.example.moneymoney_room.ui.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.ui.entry.ItemDetails
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(
    private val itemsRepository: ItemsRepository,
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
        private set

    fun chgUserInfo(paramHomeUiState: HomeUiState) {
        homeUiState =
            HomeUiState(userId = paramHomeUiState.userId, password = paramHomeUiState.password)
    }

    suspend fun deleteItems() {
        itemsRepository.deleteAllItems()
    }

    suspend fun insertItems(user: String, appContext: Context) {
        viewModelScope.launch {

            // Bulk Insert of Items for specific Account

            // Get Data from Csv-File as a List of Details
            val fileName = user

            val itemList: List<Item> = ItemListGenerator().generateItemDetailsList(fileName, appContext)
            val it = itemList.iterator()

            // Transform ItemDetail to Item and add it to the Db - directly
            while (it.hasNext() == true) {
                itemsRepository.insertItem(it.next())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun insertItemsFromUrl(csvUrl: String) {
        viewModelScope.launch(Dispatchers.IO) { // Run the coroutine on the IO dispatcher
            try {
                val url = URL(csvUrl)
                println("HomeViewModel - url = $url")

                val csvInputStream = url.openStream()
                val csvDataString = csvInputStream.bufferedReader().use { it.readText() } // Read the CSV data as a // string
                println("HomeViewModel - csvDataString = $csvDataString")

                val csvReader = CsvReader()
                val csvData = csvReader.readAll(csvDataString)
                println("HomeViewModel - csvData = $csvData") // Print csvData

                val itemList: List<Item> = ItemListGenerator().AccountFromUrl(csvData)
                val it = itemList.iterator()

                // Transform ItemDetail to Item and add it to the Db - directly
                while (it.hasNext() == true) {
                    itemsRepository.insertItem(it.next())
                }

            } catch (e: Exception) {
                // Handle exceptions here, e.g., log an error or show a message to the user
                e.printStackTrace()
            }
        }
    }
}

data class HomeUiState(
    val userId: String = "patrick",
    val password: String = "Password",
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
