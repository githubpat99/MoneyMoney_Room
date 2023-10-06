package com.example.moneymoney_room.ui.registration

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RegistrationDestination : NavigationDestination {
    override val route = "register"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.registration),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) {
        RegistrationScreenBody(
            viewModel.registrationUiState.value,
            navigateBack,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateConfiguration(it)
                    navigateBack()
                }
            },
        )
    }
}

@Composable
fun RegistrationScreenBody(
    registrationUiState: RegistrationUiState,
    navigateBack: () -> Unit,
    onValueChange: (RegistrationUiState) -> Unit,
    onSaveClick: (RegistrationUiState) -> Unit
) {

    val registrationUiState = registrationUiState

    Column(
        modifier = Modifier
            .padding(top = 128.dp, start = 32.dp)
    ) {


        OutlinedTextField(
            value = registrationUiState.userName,
            onValueChange = {
                onValueChange(
                    registrationUiState.copy(
                        userName = it
                    )
                )
            },
            label = { Text(text = "Benutzer") },
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions(onDone = {}),
            maxLines = 1
        )
        OutlinedTextField(
            value = registrationUiState.password,
            onValueChange = {
                onValueChange(
                    registrationUiState.copy(
                        password = it
                    )
                )
            },
            label = { Text(text = "Passwort") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.None, true, KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {}),
            maxLines = 1
        )
        OutlinedTextField(
            value = registrationUiState.email,
            onValueChange = {
                onValueChange(
                    registrationUiState.copy(
                        email = it
                    )
                )
            },
            label = { Text(text = "E-Mail") },
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions(onDone = {}),
            maxLines = 1
        )
        OutlinedTextField(
            value = registrationUiState.startSaldo.toString(),
            onValueChange = {
                onValueChange(
                    registrationUiState.copy(
                        startSaldo = it.toDouble()
                    )
                )
            },
            label = { Text(text = "Start Saldo") },
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.None, true, KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {}),
            maxLines = 1
        )

        val enableRegisterButton = registrationUiState.userName.isNotEmpty() &&
                registrationUiState.password.isNotEmpty() &&
                registrationUiState.email.isNotEmpty()

        Button(
            modifier = Modifier
                .padding(top=16.dp),
            onClick = {
                onSaveClick(registrationUiState)
                      },
            enabled = enableRegisterButton
        ) {
            Text(text = "Register")
        }

    }
}