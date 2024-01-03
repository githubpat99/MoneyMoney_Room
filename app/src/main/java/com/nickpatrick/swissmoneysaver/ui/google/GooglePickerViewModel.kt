package com.nickpatrick.swissmoneysaver.ui.google

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.MoneyMoneyApplication
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.data.MoneyMoneyDatabase
import com.nickpatrick.swissmoneysaver.ui.home.ItemListGenerator
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * ViewModel to retrieve all items in the Room database.
 */
class GooglePickerViewModel(
    private val itemsRepository: ItemsRepository, application: MoneyMoneyApplication
) : ViewModel() {

    lateinit var drive: Drive
    var accountName: String = ""
    var isImporting by mutableStateOf(false)

    var saldoDouble: Double = 0.0
    var userName = ""
    var password = ""
    var email = ""
    val context: Context? = application.applicationContext


    fun googleSignOut(context: Context): String {
        var message = "Google sign Out - successful"
        val googleSignInClient =
            getGoogleSignInClient(context) // Use the same context where you signed in
        googleSignInClient.signOut()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-out was successful
                    // You can perform additional actions or UI updates here
                    accountName = "empty"
                } else {
                    // Handle sign-out failure
                    message = "Google sign Out - not successful"
                }
            }
        return message
    }

    fun getDriveService(context: Context): Drive {
        accountName = ""
        val driveInstance =
            GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->

                val credential = GoogleAccountCredential.usingOAuth2(
                    context, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account!!

                drive = Drive
                    .Builder(
                        AndroidHttp.newCompatibleTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential
                    )
                    .setApplicationName(context.getString(R.string.app_name))
                    .build()
                accountName = credential.selectedAccountName

            }

        return drive
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun downloadCsvFile(context: Context, folderName: String, fileName: String): String? {
        val drive = getDriveService(context)
        var message = "something went wrong - too bad..."

        return withContext(Dispatchers.IO) {
            try {
                // Search for the folder by name
                val folderQuery = drive.files().list()
                    .setQ("name = '$folderName' and mimeType = 'application/vnd.google-apps.folder'")
                val folderResult = folderQuery.execute()
                val folders = folderResult.files

                if (folders == null || folders.isEmpty()) {
                    // The folder with the specified name was not found
                    println("GooglePickerViewModel - downloadCsvFile - folderName = $folderName - not found")
                    return@withContext null
                }

                val folderId = folders[0].id

                // Now that you have the folder ID, you can search for the file by name
                val fileQuery = drive.files().list()
                    .setQ("name = '$fileName' and '$folderId' in parents")
                val fileResult = fileQuery.execute()
                val files = fileResult.files

                if (files == null || files.isEmpty()) {
                    // The file with the specified name was not found in the folder
                    return@withContext null
                }

                val fileId = files[0].id

                // Download the file content using the fileId
                val outputStream = ByteArrayOutputStream()
                val responseStream = drive.files().get(fileId)
                    .executeMediaAsInputStream()

                // Read from responseStream and write to outputStream
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (responseStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                responseStream.close() // Close the response stream

                // Convert the downloaded bytes to a String assuming UTF-8 encoding
                val csvDataString = String(outputStream.toByteArray(), Charsets.UTF_8)
                val csvReader = CsvReader()
                val csvData = csvReader.readAll(csvDataString)

                val parts = csvDataString.split(",")
                val saldoValue = parts[9]
                    .trim('"').replace("[^0-9.]".toRegex(), "")
                saldoDouble = saldoValue.toDoubleOrNull() ?: 0.0
                userName = parts[0].replace(Regex("[\n\r\\s]+"),"")

                val itemList: List<Item> = ItemListGenerator().AccountFromUrl(csvData)
                val it = itemList.iterator()

                message = "my Budget updated successfully..."

                // Transform ItemDetail to Item and add it to the Db - directly
                while (it.hasNext() == true) {
                    itemsRepository.insertItem(it.next())
                }

                // additionally update Configuration based on Csv Info

                val approxStartSaldo = 0.0
                val approxEndSaldo = 0.0
                val ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

                updateConfiguration(ts, saldoDouble, 0.0, 2023, "", email, approxStartSaldo, approxEndSaldo)

                return@withContext message
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    suspend fun processCsvData(csvData: String): String {
        val lines = csvData.split("\n")
        var message = "something went wrong - too bad..."

        if (lines.isNotEmpty()) {
            val firstLine = lines[0]

            println("GooglePickerViewModel - firstLine = $firstLine")

            val columns = firstLine.split(",")

            println("GooglePickerViewModel - columns = ${columns.size}")

            if (columns.size >= 10) {
                val title = columns[0].replace(Regex("[\n\r\\s]+"), "")
                val year = columns[1].replace(Regex("[\n\r\\s]+"), "")
                val userName = "$title - $year"
                val saldoValue = columns[9].trim('"').replace("[^0-9.]".toRegex(), "")
                val saldoDouble = saldoValue.toDoubleOrNull() ?: 0.0
                val approxStartSaldo = 0.0
                val approxEndSaldo = 0.0
                val ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

                updateConfiguration(ts, saldoDouble, 0.0, 2023, "", email, approxStartSaldo, approxEndSaldo)

                println("GooglePickerViewModel - updateConfiguration = $saldoDouble - $userName")
            }
        }

        val itemList: List<Item> = ItemListGenerator().AccountFromDataString(csvData)
        val it = itemList.iterator()

        println("GooglePickerViewModel - items = ${itemList.size}")

        message = "my Budget updated successfully..."

        // Transform ItemDetail to Item and add it to the Db - directly
        while (it.hasNext() == true) {
            itemsRepository.insertItem(it.next())
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
            ts = ts, startSaldo = startSaldo, endSaldo = endSaldo, budgetYear = budgetYear, password = password, email = email,
            approxStartSaldo = approxStartSaldo, approxEndSaldo = approxEndSaldo)
        viewModelScope.launch {

            println("GooglePickerViewModel - updateConfiguration = $saldoDouble, $userName, $password, $email")

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao().insert(updConfig)
            }

        }
    }

}