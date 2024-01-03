package com.nickpatrick.swissmoneysaver.ui.list

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.MoneyMoneyApplication
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.data.MoneyMoneyDatabase
import com.nickpatrick.swissmoneysaver.ui.home.ItemListGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * ViewModel to retrieve all items in the Room database.
 */

class ListViewModel(itemsRepository: ItemsRepository, application: MoneyMoneyApplication) :
    ViewModel() {


    var isImporting by mutableStateOf(false)
    var isRunning by mutableStateOf(false)
    var spreadSheetId = ""
    val sheetName = "CSV_Data"

    // Verify the Source Spreadsheet Info in AppsScript, too
    // var sourceSpreadsheetId = "1helfxjLHrQDdFg2EBIH7d3rFjvtzKFF3FiAXZJ4mzXw"; // Replace with the ID of the source spreadsheet
    val googleAppsScriptUrl = "https://script.google.com/macros/s/AKfycbz9QMVRtfb19TbzF2QGyEcs58amvl3aAg8W_0X_FRY25Y-mwYv8rxLskB4wVmNJGdKjZA/exec"

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()
    val itemsRepository = itemsRepository
    val context: Context? = application.applicationContext

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

    suspend fun processCsvData(csvData: String): String {

        var message = "something went wrong - too bad..."

        if (csvData.isNotEmpty()) {
            // Parse the JSON data
            val jsonObject = org.json.JSONObject(csvData)
            val valuesArray = jsonObject.getJSONArray("values")

            // Process the values array
            val firstRowArray = valuesArray.getJSONArray(0)

            println("ListViewModel - firstRowArray = $firstRowArray")
//            println("ListViewModel - jsonObject = ${jsonObject}")

            // Process the title row differently
            val budgetYear = 2023
            val saldoDouble = firstRowArray.getString(3)
                .replace(".", "")
                .replace(",", ".")
                .toDouble()
            val password = ""
            val email = ""
            val approxStartSaldo = 0.0
            val approxEndSaldo = 0.0
            val ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

            updateConfiguration(ts, saldoDouble, 0.0, budgetYear, password, email, approxStartSaldo, approxEndSaldo)

            // and now the rest
            val itemList: List<Item> = ItemListGenerator().AccountFromJson(valuesArray)
            val it = itemList.iterator()

            message = "my Budget updated successfully..."

            // Transform ItemDetail to Item and add it to the Db - directly
            while (it.hasNext() == true) {
                itemsRepository.insertItem(it.next())
            }

        }

        return message
    }

    private fun updateConfiguration(
        ts: Long,
        startSaldo: Double,
        endSaldo: Double,
        budgetYear: Int,
        password: String,
        email: String,
        approxStartSaldo: Double,
        approxEndSaldo: Double

        ) {

        val moneyMoneyDatabase = context?.let { MoneyMoneyDatabase.getDatabase(it) }

        val updConfig = Configuration(
            ts = ts,
            startSaldo = startSaldo,
            endSaldo = endSaldo,
            budgetYear = budgetYear,
            password = password,
            email = email,
            approxStartSaldo = approxStartSaldo,
            approxEndSaldo = approxEndSaldo
        )
        viewModelScope.launch {

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao().insert(updConfig)
            }
        }
    }

    fun saveSpreadsheetId(url: String) {
        val startIdx = url.indexOf("/d/") + 3
        val endIdx = url.indexOf("/edit")

        spreadSheetId = url.substring(startIdx, endIdx)

    }

    fun deleteGoogleSpreadsheet() {
        val webAppUrl = googleAppsScriptUrl
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(webAppUrl)
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network failures
                println("ListViewModel - network failures")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Successfully triggered deletion
                    println("ListViewModel - Successfully triggered deletion")
                } else {
                    // Handle response errors
                    println("ListViewModel - response error = $response")
                }
            }
        })
    }
}

data class ListUiState(
    val list: List<Item> = listOf(),
)