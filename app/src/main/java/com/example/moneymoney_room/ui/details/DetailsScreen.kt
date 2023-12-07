package com.example.moneymoney_room.ui.details

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.entry.ItemDetails
import com.example.moneymoney_room.ui.entry.ItemUiState
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

object DetailsDestination : NavigationDestination {
    override val route = "details"
    override val titleRes = R.string.details
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"   //todo PIN: itemIdArg always in {}
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailsScreen(
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateToEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val itemDetails2 = viewModel.itemUiState.itemDetails
    val oldA = itemDetails2.amount

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.details),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) {

        DetailScreenBody(
            itemUiState = viewModel.itemUiState,
            navigateBack,
            navigateToEntry,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()

                }
            },
            onDeleteClick = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateBack()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenBody(
    itemUiState: ItemUiState,
    navigateBack: () -> Unit,
    navigateToEntry: () -> Unit,
    onValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val context = LocalContext.current
    val itemDetails = itemUiState.itemDetails
    var myLocalDateTime = Utilities.getDateFromTimestamp(itemDetails.timestamp)

    val myYear = myLocalDateTime.year
    val myMonth = myLocalDateTime.monthValue - 1
    val myDay = myLocalDateTime.dayOfMonth

    val instant = Instant.ofEpochMilli(itemDetails.timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    var myDateString = localDateTime.format(formatter)
    var mDate by remember { mutableStateOf(myDateString) }

    val calendar = Calendar.getInstance()
    val minYear = myYear
    val minMonth = myMonth // Month numbering starts from 0 (January is 0)
    val minDay = 1

    val maxYear = myYear
    val maxMonth = myMonth // Month numbering starts from 0 (December is 11)
    var maxDay = 31
    when (myMonth) {
        1 -> maxDay = 28
        3 -> maxDay = 30
        5 -> maxDay = 30
        8 -> maxDay = 30
        10 -> maxDay = 30
    }

    val myDatePickerDialog = DatePickerDialog(
        context,
        R.style.DialogTheme,
        { _: DatePicker, myYear: Int, myMonth: Int, myDay: Int ->
            mDate = "$myDay.${myMonth + 1}.$myYear"
            itemDetails.timestamp = Utilities.getLongFromStringDate(mDate)
        }, myYear, myMonth, myDay
    )
    // Set minimum date
    calendar.set(minYear, minMonth, minDay)
    val minDateInMillis = calendar.timeInMillis
    myDatePickerDialog.datePicker.minDate = minDateInMillis

// Set maximum date
    calendar.set(maxYear, maxMonth, maxDay)
    val maxDateInMillis = calendar.timeInMillis
    myDatePickerDialog.datePicker.maxDate = maxDateInMillis

    Column {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Einnahmen & Ausgaben",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.size(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (myDateString.isNotBlank())
                                mDate = myDateString

                            Text(
                                text = mDate,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    myDateString = ""
                                    myDatePickerDialog.show()
                                },
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "Erstes Mal")

                            }
                        }

                        OutlinedTextField(
                            value = itemDetails.description,
                            onValueChange = {
                                onValueChange(itemDetails.copy(description = it))
                            },
                            label = { Text(text = "Bezeichnung") },
                            visualTransformation = VisualTransformation.None,
                            keyboardOptions = KeyboardOptions.Default,
                            keyboardActions = KeyboardActions(onDone = {}),
                            maxLines = 1
                        )
                        OutlinedTextField(
                            value = itemDetails.amount.toString(),
                            onValueChange = {
                                val parsedValue = it.toDoubleOrNull() ?: 0.0
                                val validatedValue = if (parsedValue > 999999.9) 999999.9 else parsedValue
                                onValueChange(
                                    itemDetails.copy(
                                        amount = validatedValue
                                    )
                                )
                            },
                            label = { Text(text = "Betrag") },
                            visualTransformation = VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = {}),
                            maxLines = 1
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Adjust padding as needed
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f),
                onClick = onSaveClick,
                enabled = itemUiState.isEntryValid
            ) {
                Text(text = "Save")
            }
            Button(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f),
                onClick = onDeleteClick,
                enabled = true
            ) {
                Text(text = "Del")
            }
        }
    }
}

