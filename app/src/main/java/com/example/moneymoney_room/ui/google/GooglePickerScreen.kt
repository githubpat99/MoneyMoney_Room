package com.example.moneymoney_room.ui.google

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.entry.ItemDetails
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


object GoogleDestination : NavigationDestination {
    override val route = "google"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GooglePickerScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToList: () -> Unit,
    navigateToBudget: () -> Unit,
    viewModel: GooglePickerViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.googlePickerScreen),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) {

        GooglePickerScreenBody(
            navigateBack,
            onValueChange = { },
            onSaveClick = {
                coroutineScope.launch {
//                    viewModel.saveItem()
                    navigateToList()
                }
            },
            context = context,
            viewModel = viewModel
        )
    }


}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GooglePickerScreenBody(
    navigateBack: () -> Unit,
    onValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    context: Context,
    viewModel: GooglePickerViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    // Here's the HttpClient declaration now
    val client = remember { HttpClient() }

    var folderName by remember { mutableStateOf("Private") }
    var fileName by remember { mutableStateOf("test.csv") }
    var fileContent by remember { mutableStateOf<String?>(null) }
    var displayName by remember { mutableStateOf(viewModel.accountName) }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)

                    /**
                     * handle [task] result
                     */
                    displayName = task.result.displayName ?: ""

                    println("GooglePickerScreen - startForResult - task = $displayName")
                    viewModel.accountName = displayName

                } else {
                    Toast.makeText(context, "Google Login Error!", Toast.LENGTH_LONG).show()
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
    ) {

        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            label = { Text("Google Account") },
            value = displayName,
            onValueChange = {},
            readOnly = true
        )
        Row() {
            Button(
                enabled = (displayName == "" || displayName == "logged off"),
                onClick = {
                    startForResult.launch(getGoogleSignInClient(context).signInIntent)
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Sign in ...")
            }
            Button(
                enabled = (displayName != "logged off" && displayName != ""),
                onClick = {
                    viewModel.googleSignOut(context)
                    displayName = "logged off"
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Sign out ...")
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            label = { Text("Google Folder") },
            value = folderName,
            onValueChange = { folderName = it },
            readOnly = false
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            label = { Text("File Name") },
            value = fileName,
            onValueChange = { fileName = it },
            readOnly = false
        )
        Row() {
            Button(
                modifier = Modifier
                    .padding(16.dp),
                enabled = folderName.isNotBlank() && fileName.isNotBlank() &&
                        (displayName != "logged off" && displayName != ""),
                onClick = {

                    // Progress Indicator on
                    viewModel.isImporting = true
                    // Launch a coroutine to download the CSV file
                    coroutineScope.launch {
                        val downloadedContent =
                            viewModel.downloadCsvFile(context, folderName, fileName)

                        // Progress Indicator off
                        viewModel.isImporting = false

                        if (downloadedContent != null) {
                            // Update the state with the downloaded content
                            fileContent = downloadedContent
                            Toast.makeText(
                                context,
                                "Daten wurden erfolgreich importiert",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Handle the case where download failed
                            Toast.makeText(
                                context,
                                "Daten konnten leider nicht importiert werden",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            ) {
                Text("Download CSV")
            }

            val webAppUrl =
                "https://script.google.com/macros/s/AKfycbyT4p6C_YvUSKLnnDhGcR7Bq-yQB8XIZjnEwawURRCVp5_q-SFFcoiEfS1FYOrENKPq/exec"

            val myUrl =
                "$webAppUrl?action=exportCSV"

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val response = client.get(myUrl)
                            if (response.status.isSuccess()) {
                                val responseBody =
                                    response.bodyAsText() // Read the response as text

//                                println("CSV Data: $responseBody")

                                // Now responseBody contains your CSV data as a string
                                // You can parse and process it as needed

                                // For example, you can split the CSV data into lines
                                val message = viewModel.processCsvData(responseBody)
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()


                            }
                        } catch (e: Exception) {
                            // Handle any errors
                            println("error = ${e.localizedMessage}")
                            Toast.makeText(
                                context,
                                "Error calling Apps Script: ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Call Apps Script")
            }
        }


        if (viewModel.isImporting) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }

    }
}

@Composable
fun callAppsScriptWebApp(client: HttpClient, coroutineScope: CoroutineScope) {

    val context = LocalContext.current

    val scriptUrl =
        "https://script.google.com/macros/s/your_web_app_id/exec" // Replace with your Web App URL

    Button(
        onClick = {
            coroutineScope.launch {
                try {
                    val response: HttpResponse = client.get("$scriptUrl/exec")

                    if (response.status.isSuccess()) {
                        val responseBody = response.bodyAsText()
                        // Handle the response as needed
                        println("Response from Apps Script: $responseBody")
                    } else {
                        // Handle non-successful response
                        Toast.makeText(
                            context,
                            "Error calling Apps Script: ${response.status.description}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    // Handle any errors
                    Toast.makeText(
                        context,
                        "Error calling Apps Script: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    ) {
        Text("Execute Apps Script")
    }
}






