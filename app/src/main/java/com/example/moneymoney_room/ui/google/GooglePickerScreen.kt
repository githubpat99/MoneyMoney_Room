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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

    var fileId by remember { mutableStateOf("1sQxG5LIrdi2OeYIZSLQNaoz9JEWatO-M")}

    var fileContent by remember { mutableStateOf<String?>(null) }
    var inputValue by remember { mutableStateOf("") }

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
        Button(
            onClick = {
                startForResult.launch(getGoogleSignInClient(context).signInIntent)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(32.dp)
        ) {
            Text(text = "Sign in with Google")
        }
        TextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(64.dp),
            value = viewModel.accountName,
            onValueChange = {},
            readOnly = true
        )
        TextField(
            value = inputValue,
            onValueChange = {
                inputValue = it
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = { viewModel.createGDriveFolder(inputValue) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Create GDrive Folder")
        }
        TextField(
            value = fileId,
            onValueChange = { fileId = it }
        )

        Button(
            onClick = {
                // Launch a coroutine to download the CSV file
                coroutineScope.launch {
                    val downloadedContent = viewModel.downloadCsvFile(context, fileId)

                    if (downloadedContent != null) {
                        // Update the state with the downloaded content
                        fileContent = downloadedContent
                    } else {
                        // Handle the case where download failed
                    }
                }
            }
        ) {
            Text("Download CSV")
        }

        // Display the downloaded CSV content
        fileContent?.let {
            Text(it)
        }
        TextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(64.dp),
            value = "not set yet",
            onValueChange = {},
            readOnly = true
        )
    }
}



