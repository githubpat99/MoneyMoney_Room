package com.nickpatrick.swissmoneysaver.ui.budget

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nickpatrick.swissmoneysaver.MoneyMoneyTopAppBar
import com.nickpatrick.swissmoneysaver.ui.AppViewModelProvider
import com.nickpatrick.swissmoneysaver.ui.entry.ItemDetails
import com.nickpatrick.swissmoneysaver.ui.navigation.NavigationDestination
import com.nickpatrick.swissmoneysaver.util.Utilities
import com.nickpatrick.swissmoneysaver.R
import kotlinx.coroutines.launch

object BudgetDestination : NavigationDestination {
    override val route = "budget"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BudgetScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToList: () -> Unit,
    navigateToBudget: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.budgetScreen),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) {
        BudgetScreenBody(
            viewModel.budgetUiState,
            navigateBack,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {

                    // todo PIN: viewModel not correctly updated yet
                    println("BudgetScreen: itemDetails = ${viewModel.budgetUiState.itemDetails}")

                    viewModel.saveItem()
//                    navigateToList()

                }
            },
            navigateToList,
            navigateToBudget
        )
    }


}

@Composable
fun BudgetScreenBody(
    budgetUiState: BudgetUiState,
    navigateBack: () -> Unit,
    onValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    navigateToList: () -> Unit,
    navigateToBudget: () -> Unit
) {

    var isEditing by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(true) }
    var budgetInfo by remember { mutableStateOf("tbd") }
    var infoStatic = ""
    var infoDynamic = ""
    var jokerCard: Boolean by remember { mutableStateOf(false) }
    val todayString = Utilities.getCurrentDateTimeAsString()
    val itemDetails = budgetUiState.itemDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Add a custom composable with Box and TextField to the first column
                BoxExample(
                    color = colorResource(id = R.color.einnahme_Vorlage),
                    name = "Einkommen",
                    betrag = "6500.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"
                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.einnahme_Vorlage),
                    name = "Dividende",
                    betrag = "500.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Add a custom composable with Box and TextField to the second column
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Miete",
                    betrag = "-1500.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Krankenkasse",
                    betrag = "-450.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Handy",
                    betrag = "-50.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Nebenkosten",
                    betrag = "-350.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Add a custom composable with Box and TextField to the second column
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Haushalt",
                    betrag = "-850.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Ausgang",
                    betrag = "-700.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.ausgabe_Vorlage),
                    name = "Kleider",
                    betrag = "-800.00",
                    datum = todayString,
                    jokerCard = false,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }

                    )
                Spacer(modifier = Modifier.height(12.dp))
                BoxExample(
                    color = colorResource(id = R.color.purple_200),
                    name = "Joker",
                    betrag = "-750",
                    datum = todayString,
                    jokerCard = true,

                    onClick = { clickedName, clickedBetrag, clickedDatum, selButton ->
                        // Handle the click here with the provided data
                        val updItemDetails = itemDetails.copy(
                            name = clickedName,
                            amount = clickedBetrag.toDouble(),  //todo PIN: Regex...
                            //                        date = clickedDatum
                        )
                        isEditing = true
                        val info = updInfo(
                            clickedName,
                            clickedBetrag,
                            selButton
                        )
                        budgetInfo = "$info 25.9.2023"

                        onValueChange(updItemDetails)
                    }
                )
            }
        }

        Divider(
            modifier = Modifier
                .padding(top = 32.dp),
            color = Color.Gray, thickness = 1.dp
        )

        if (isEditing) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = budgetInfo,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }

            Divider(
                modifier = Modifier
                    .padding(top = 8.dp),
                color = Color.Gray, thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isEnabled = false
                        onSaveClick()
                        navigateToBudget()
                    },
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(text = "... im Budget speichern")
                }
            }

        }
    }
}


private fun updInfo(
    clickedName: String,
    clickedBetrag: String,
    selButton: String,
): String {

    var infoDynamic = ""

    var infoStatic = "$clickedName\nCHF: $clickedBetrag"
    when (selButton) {
        "1 Mt." -> infoDynamic = "monatlich\nStart am: "
        "keine" -> infoDynamic = "einmalig\nam: "
        else -> infoDynamic = "Wiederholung nach $selButton\nStart am: "
    }
    return infoStatic + "\n" + infoDynamic
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxExample(
    color: Color,
    name: String,
    betrag: String,
    datum: String,
    jokerCard: Boolean,
    onClick: (String, String, String, String) -> Unit,

    ) {
    // Define state variables to hold the new values and dialog visibility
    var newName by remember { mutableStateOf(name) }
    var newBetrag by remember { mutableStateOf(betrag) }
    var newDatum by remember { mutableStateOf(datum) }
    var isDialogVisible by remember { mutableStateOf(false) }

    // State to track the box color
    var boxColor by remember { mutableStateOf(color) }
    val clickedColor = colorResource(id = R.color.vorlage_used)

    // State to track which button is clicked
    var selectedButton by remember { mutableStateOf("") }

    // When the user clicks the box, show the dialog
    Box(
        modifier = Modifier
            .height(60.dp)
            .clickable {
                isDialogVisible = true
                boxColor = clickedColor
            }
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight()
                .width(100.dp)
                .background(
                    color = if (selectedButton == "mtl.") clickedColor else color,
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Text(
            text = newName,
            modifier = Modifier.align(Alignment.TopCenter),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = newBetrag,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(fontSize = 14.sp)
        )
        Text(
            text = newDatum,
            modifier = Modifier.align(Alignment.BottomCenter),
            style = TextStyle(fontSize = 14.sp)
        )
    }

    // Display the dialog when isDialogVisible is true
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user taps outside of it
                isDialogVisible = false
            },
            title = {
                Text(text = "Werte anpassen")
            },
            text = {
                Column {
                    // Inside the dialog, allow the user to edit values
                    // Name may only be changed when "Joker"
                    if (jokerCard) {
                        TextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = {
                                Text("Name",
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        )
                    } else {
                        Text(
                            text = newName,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    TextField(
                        value = newBetrag,
                        onValueChange = { newBetrag = it },
                        label = {
                            Text("Betrag",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                    TextField(
                        value = newDatum,
                        onValueChange = { newDatum = it },
                        label = {
                            Text("Datum",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "Wiederholung nach ...",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                selectedButton = "1 Mt."
                            },
                            enabled = selectedButton != "1 Mt."
                        ) {
                            Text(text = "1 Mt.")

                        }
                        Button(
                            onClick = {
                                selectedButton = "2 Mt."
                            },
                            enabled = selectedButton != "2 Mt."
                        ) {
                            Text(text = "2 Mt.")

                        }
                        Button(
                            onClick = {
                                selectedButton = "3 Mt."
                            },
                            enabled = selectedButton != "3 Mt."
                        ) {
                            Text(text = "3 Mt.")

                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                selectedButton = "4 Mt."
                            },
                            enabled = selectedButton != "4 Mt."
                        ) {
                            Text(text = "4 Mt.")

                        }
                        Button(
                            onClick = {
                                selectedButton = "6 Mt."
                            },
                            enabled = selectedButton != "6 Mt."
                        ) {
                            Text(text = "6 Mt.")

                        }
                        Button(
                            onClick = {
                                selectedButton = "keine"
                            },
                            enabled = selectedButton != "keine"
                        ) {
                            Text(text = "keine")

                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Save the new values and close the dialog
                        onClick(newName, newBetrag, newDatum, selectedButton)
                        isDialogVisible = false
                    }
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Close the dialog without saving changes
                        isDialogVisible = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
