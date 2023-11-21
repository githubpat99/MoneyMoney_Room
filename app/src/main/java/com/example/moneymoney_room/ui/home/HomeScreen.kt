package com.example.moneymoney_room.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToBudgetForm: (Int) -> Unit,
    navigateToRegistration: () -> Unit,
    navigateToMonthly: () -> Unit,
    navigateToBudget: () -> Unit,
    navigateToGooglePicker: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val coroutineScope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    var switch: Boolean = false
    var patPrivate: Boolean = false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary_background)),
        contentAlignment = Alignment.TopCenter,
    )
    {
        Image(
            painterResource(id = R.drawable.budgetimage), "null",
            modifier = Modifier
                .fillMaxSize(),
            alignment = Alignment.TopCenter
        )

        Column(
            modifier = Modifier
                .padding(24.dp, 160.dp, 0.dp, 24.dp)
        ) {
            LoginCard(
                homeUiState = viewModel.homeUiState,
                onValueChange = viewModel::chgUserInfo,
                modifier = Modifier

            )

            if (viewModel.homeUiState.password.isNotBlank()) {
                switch = true
                if (viewModel.homeUiState.password == "Passwort ") {
                    patPrivate = true
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionButton(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 8.dp),
                    active = switch,
                    navigateToMonthly,
                    text = "Mein Budget"
                )
//                ActionButton(
//                    modifier = Modifier
//                        .padding(top = 16.dp),
//                    active = switch,
//                    navigateToBudget,
//                    text = "New Budget"
//                )
            }

            Divider(
                modifier = Modifier
                    .padding(top = 200.dp),
                color = Color.Gray, thickness = 1.dp
            )

//            Row() {
//                LoaderButton(
//                    modifier = Modifier
//                        .padding(8.dp),
//                    active = switch,
//                    onClick = {
//                        val active = switch
//                        if (active) {
//                            coroutineScope.launch {
//                                viewModel.insertItems("data.csv", appContext)
//                            }
//                        }
//                    },
//                    text = "Import Test"
//                )
//                ActionButton(
//                    modifier = Modifier
//                        .padding(top = 8.dp),
//                    active = switch,
//                    navigateToMonthly,
//                    text = "Monatsübersicht"
//                )
//            }


            Row() {
                DeleteAll(
                    modifier = Modifier
                        .padding(8.dp),
                    active = switch,
                    onClick = {
                        val active = switch
                        if (active) {
                            coroutineScope.launch {
                                viewModel.deleteItems()
                            }
                        }
                    },
                    text = "Budget löschen"
                )
                ActionButton(
                    modifier = Modifier
                        .padding(8.dp),
                    active = patPrivate,
                    navigateToGooglePicker,
                    text = "GDrive pick"
                )
            }

            Row() {
                ActionButton(
                    modifier = Modifier
                        .padding(8.dp),
                    active = switch,
                    navigateToRegistration,
                    text = "Registration"
                )
                ActionButton(
                    modifier = Modifier
                        .padding(8.dp),
                    active = switch,
                    { navigateToBudgetForm(0) },
                    text = "Budgetformular"
                )
            }


        }
    }
}


@Composable
fun ActionButton(
    modifier: Modifier,
    active: Boolean,
    navigateToList: () -> Unit,
    text: String,
) {
    Button(
        modifier = modifier,
        onClick = navigateToList,
        enabled = active,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.LightGray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text = text)

    }
}

@Composable
fun LoaderButton(
    modifier: Modifier,
    active: Boolean,
    onClick: () -> Unit,
    text: String,
) {
    Button(
        modifier = modifier,
        onClick = {
            if (active) {
                onClick()
            }
        },
        enabled = active,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.LightGray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text = text)

    }
}

@Composable
fun DeleteAll(
    modifier: Modifier,
    active: Boolean,
    onClick: () -> Unit,
    text: String,
) {
    Button(
        modifier = modifier,
        onClick = {
            if (active) {
                onClick()
            }
        },
        enabled = active,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.LightGray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text = text)

    }
}

@Composable
fun LoginCard(
    homeUiState: HomeUiState,
    onValueChange: (HomeUiState) -> Unit,
    modifier: Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column {
            // userId
            OutlinedTextField(
                value = homeUiState.userId,
                onValueChange = { onValueChange(homeUiState.copy(userId = it)) },
                label = { Text("User") },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = {}),
                maxLines = 1
            )
            // userId
            OutlinedTextField(
                value = homeUiState.password,
                onValueChange = { onValueChange(homeUiState.copy(password = it)) },
                label = { Text(text = "Passwort") },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = {}),
                maxLines = 1
            )
        }

    }
}

@Composable
fun UrlInputDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUrlEntered: (String) -> Unit,

    ) {

    val urlState = remember { mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user taps outside of it
                onDismiss()
            },
            title = {
                Text(text = "Enter URL")
            },
            text = {
                OutlinedTextField(
                    value = urlState.value,
                    onValueChange = { text ->
                        // Update the text value as the user types
                        urlState.value = text
                    },
                    label = { Text("URL") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Call the callback function with the entered URL
                        onUrlEntered(urlState.value)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Dismiss the dialog without taking any action
                        onDismiss()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


