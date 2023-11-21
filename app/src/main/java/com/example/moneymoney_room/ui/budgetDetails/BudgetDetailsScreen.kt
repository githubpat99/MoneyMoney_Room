package com.example.moneymoney_room.ui.budgetDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object BudgetDetailsDestination : NavigationDestination {
    override val route = "details"
    override val titleRes = R.string.budgetDetails
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"   //todo PIN: itemIdArg always in {}
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BudgetDetailsScreen(
    navigateToBudgetForm: (Int) -> Unit,
    canNavigateBack: Boolean = true,
    navigateToEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.details),
                canNavigateBack = true,
                navigateUp = {
                    val navRoute = if (viewModel.budgetItemUiState.budgetItemDetails.debit) 0 else 1
                    navigateToBudgetForm(navRoute)}
            )
        }
    ) {

        BudgetDetailScreenBody(
            budgetItemUiState = viewModel.budgetItemUiState,
            navigateToEntry,
            onValueChange = viewModel::updateBudgetUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    val navRoute = if (viewModel.budgetItemUiState.budgetItemDetails.debit) 0 else 1
                    navigateToBudgetForm(navRoute)
                }
            },
            onDeleteClick = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    val navRoute = if (viewModel.budgetItemUiState.budgetItemDetails.debit) 0 else 1
                    navigateToBudgetForm(navRoute)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreenBody(
    budgetItemUiState: BudgetItemUiState,
    navigateToEntry: () -> Unit,
    onValueChange: (BudgetItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    val budgetItemDetails = budgetItemUiState.budgetItemDetails
    var myLocalDateTime = Utilities.getDateFromTimestamp(budgetItemDetails.timestamp * 1000)

    val myYear = myLocalDateTime.year
    val myMonth = myLocalDateTime.monthValue - 1
    val myDay = myLocalDateTime.dayOfMonth

    val instant = Instant.ofEpochMilli(budgetItemDetails.timestamp * 1000)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    var myDateString = localDateTime.format(formatter)
    var mDate by remember { mutableStateOf(myDateString) }

    val myDatePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, myYear: Int, myMonth: Int, myDay: Int ->
            mDate = "$myDay.${myMonth + 1}.$myYear"
            budgetItemDetails.timestamp = Utilities.getLongFromStringDate(mDate) / 1000
        }, myYear, myMonth, myDay
    )
    var myBackgroundColor = colorResource(id = R.color.light_red)
    var myTitel = "Ausgabe"
    if (budgetItemDetails.debit == true) {
        myBackgroundColor = colorResource(id = R.color.light_blue)
        myTitel = "Einnahme"
    }


    Column {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 72.dp)
                .background(myBackgroundColor),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = myTitel,
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold),
//                    modifier = Modifier.padding(bottom = 8.dp)
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
                                color = colorResource(id = R.color.white),
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

                        println("BudgetDetailsScreen - budgetItemDetails: ${budgetItemDetails.amount} / ${budgetItemDetails.debit}")

                        OutlinedTextField(
                            value = budgetItemDetails.name,
                            onValueChange = {
                                onValueChange(budgetItemDetails.copy(name = it))
                            },
                            label = { Text(text = "Name") },
                            visualTransformation = VisualTransformation.None,
                            keyboardOptions = KeyboardOptions.Default,
                            keyboardActions = KeyboardActions(onDone = {}),
                            maxLines = 1,
                            textStyle = TextStyle(
                                color = colorResource(id = R.color.white),
                                fontSize = 16.sp,
                            )
                        )
                        OutlinedTextField(
                            value = budgetItemDetails.description,
                            onValueChange = { onValueChange(budgetItemDetails.copy(description = it)) },
                            label = { Text(text = "Beschreibung") },
                            visualTransformation = VisualTransformation.None,
                            keyboardOptions = KeyboardOptions.Default,
                            keyboardActions = KeyboardActions(onDone = {}),
                            maxLines = 1,
                            textStyle = TextStyle(
                                color = colorResource(id = R.color.white),
                                fontSize = 16.sp,
                            )
                        )
                        OutlinedTextField(
                            value = budgetItemDetails.amount.toString(),
                            onValueChange = {
                                onValueChange(
                                    budgetItemDetails.copy(
                                        amount = it.toDoubleOrNull() ?: 0.0
                                    )
                                )
                            },
                            label = { Text(text = "Betrag") },
                            visualTransformation = VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            keyboardActions = KeyboardActions(onDone = {}),
                            maxLines = 1,
                            textStyle = TextStyle(
                                color = colorResource(id = R.color.white),
                                fontSize = 16.sp,
                            )
                        )

                    }
                }

                val typeIdxMap = mapOf(
                    0 to 12,
                    1 to 6,
                    2 to 4,
                    3 to 3,
                    4 to 2,
                    5 to 1
                )
                val reverseTypeIdxMap = typeIdxMap.entries.associate { (key, value) -> value to key }

                val storedTypeIdx = reverseTypeIdxMap[budgetItemDetails.type]
                var selectedIndex by remember { mutableStateOf(storedTypeIdx ?: 0) }
                
                // If storedTypeIdx is not null, update selectedIndex
                storedTypeIdx?.let {
                    selectedIndex = it
                }
                
                val buttonTextMap = mapOf(
                    0 to "monatlich",
                    1 to "2 monatlich",
                    2 to "pro Quartal",
                    3 to "pro Trimester",
                    4 to "halbjährlich",
                    5 to "jährlich"
                    // Add more buttons as needed
                )

                Column {
                    Row( modifier = Modifier
                        .padding(end = 48.dp)
                    ) {
                        for (i in 0 until 2) {
                            PressIconButton(
                                index = i,
                                isSelected = selectedIndex == i,
                                onValueChange = { newIndex ->
                                    selectedIndex = if (selectedIndex == newIndex) -1 else newIndex
                                    onValueChange(
                                        budgetItemDetails.copy(
                                            type = typeIdxMap[i] ?: 0
                                        )
                                    )
                                },
                                buttonTextMap = buttonTextMap,
                                containerColor = colorResource(id = R.color.light_gray),
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f)
                            )
                        }
                    }
                    Row( modifier = Modifier
                        .padding(end = 48.dp)
                    ) {
                        for (i in 2 until 4) {
                            PressIconButton(
                                index = i,
                                isSelected = selectedIndex == i,
                                onValueChange = { newIndex ->
                                    selectedIndex = if (selectedIndex == newIndex) -1 else newIndex
                                    onValueChange(
                                        budgetItemDetails.copy(
                                            type = typeIdxMap[i] ?: 0
                                        )
                                    )
                                },
                                buttonTextMap = buttonTextMap,
                                containerColor = colorResource(id = R.color.light_gray),
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f)
                            )
                        }
                    }
                    Row( modifier = Modifier
                        .padding(end = 48.dp)
                    ) {
                        for (i in 4 until 6) {
                            PressIconButton(
                                index = i,
                                isSelected = selectedIndex == i,
                                onValueChange = { newIndex ->
                                    selectedIndex = if (selectedIndex == newIndex) -1 else newIndex
                                    onValueChange(
                                        budgetItemDetails.copy(
                                            type = typeIdxMap[i] ?: 0
                                        )
                                    )
                                },
                                buttonTextMap = buttonTextMap,
                                containerColor = colorResource(id = R.color.light_gray),
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f)
                            )
                        }
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
                onClick = navigateToEntry
            ) {
                Text(text = "Neu")
            }

            Button(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f),
                onClick = onSaveClick,
                enabled = budgetItemUiState.isEntryValid
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
                Text(text = "Delete")
            }
        }
    }
}

@Composable
fun PressIconButton(
    index: Int,
    isSelected: Boolean,
    onValueChange: (Int) -> Unit,
    buttonTextMap: Map<Int, String>,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = if (isSelected) Color.White else Color.Black,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Gray
    )

    Button(
        onClick = { onValueChange(index) },
        colors = buttonColors,
        modifier = modifier

    ) {
        Text(modifier = Modifier
            .padding(0.dp),
            text = buttonTextMap[index] ?: "")
    }
}



