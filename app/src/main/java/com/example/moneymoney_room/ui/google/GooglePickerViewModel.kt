package com.example.moneymoney_room.ui.google

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.ui.home.ItemListGenerator
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


/**
 * ViewModel to retrieve all items in the Room database.
 */
class GooglePickerViewModel(
    private val itemsRepository: ItemsRepository,
) : ViewModel() {

    lateinit var drive: Drive
    var accountName = ""

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

    fun createGDriveFolder(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Define a Folder
            val gFolder = com.google.api.services.drive.model.File()
            // Set file name and MIME
            gFolder.name = folderName
            gFolder.mimeType = "application/vnd.google-apps.folder"

            // You can also specify where to create the new Google folder
            // passing a parent Folder Id
            val parents: MutableList<String> = ArrayList(1)
            parents.add("0B3OXozJR2eIJOUdJU25JaTh1SnM")
            gFolder.parents = parents
            drive.Files().create(gFolder).setFields("id").execute()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun downloadCsvFile(context: Context, fileId: String): String? {
        val drive = getDriveService(context)

        return withContext(Dispatchers.IO) {
            try {
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

                val csvDataString = String(outputStream.toByteArray(), Charsets.UTF_8)
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

                // Convert the downloaded bytes to a String assuming UTF-8 encoding
                return@withContext csvDataString

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

}