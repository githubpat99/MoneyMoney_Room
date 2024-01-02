package com.example.moneymoney_room.ui.registration

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.Calendar

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
            viewModel,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenBody(
    viewModel: RegistrationViewModel,
    navigateBack: () -> Unit,
    onValueChange: (Configuration) -> Unit,
    onSaveClick: (Configuration) -> Unit,
) {

    var showInitDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val year = LocalDateTime.now().year
    val configItemsState = viewModel.configItems.collectAsState(initial = emptyList())
    val configList = mutableListOf<Configuration>()

    configItemsState.value.forEach { item ->
        if (item != null) {
            val config = Configuration(
                ts = item.ts,
                status = item.status,
                budgetYear = item.budgetYear,
                password = item.password,
                email = item.email,
                startSaldo = item.startSaldo,
                endSaldo = item.endSaldo,
                approxStartSaldo = item.approxStartSaldo,
                approxEndSaldo = item.approxEndSaldo
            )
            configList.add(config)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primary_background))
    ) {

        Column(
            modifier = Modifier
                .padding(top = 72.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "Aktives Jahr - $year",
                style = TextStyle(
                    color = colorResource(id = R.color.white),
                    textAlign = TextAlign.Left,
                    fontSize = 24.sp
                )
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 124.dp)
                .fillMaxWidth()
                .height(180.dp)
                .background(colorResource(id = R.color.gray))
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "Budgets",
                style = TextStyle(
                    color = colorResource(id = R.color.white),
                    textAlign = TextAlign.Left,
                    fontSize = 18.sp
                )
            )
            MyLazyBudgetList(configList, year, viewModel)
        }

        Column(
            modifier = Modifier
                .padding(top = 309.dp)
                .fillMaxWidth()
                .height(180.dp)
                .background(colorResource(id = R.color.semi_gray))
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = "Live Data",
                style = TextStyle(
                    color = colorResource(id = R.color.white),
                    textAlign = TextAlign.Left,
                    fontSize = 18.sp
                )
            )
            MyLazyLiveDataList(configList, year)
        }

        Column(
            modifier = Modifier
                .padding(top = 600.dp, start = 4.dp)
                .fillMaxWidth()
        ) {
            Row() {
                Button(
                    modifier = Modifier
                        .padding(4.dp),
                    onClick = {
                        showInitDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        disabledContentColor = colorResource(id = R.color.light_gray)
                    )
                ) {
                    Text(
                        text = "Initialisieren",
                        style = TextStyle(color = colorResource(id = R.color.white))
                    )

                }
                Button(
                    modifier = Modifier
                        .padding(4.dp),
                    onClick = {
                        val url = "https://moneymoney.it-pin.ch" // Replace with your URL
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        disabledContentColor = colorResource(id = R.color.light_gray)
                    )
                ) {
                    Text(
                        text = "Money-Money Homepage",
                        style = TextStyle(color = colorResource(id = R.color.white))
                    )

                }
            }
        }
    }

    if (showInitDialog == true) {
        showInitDialog = false
        val builder = AlertDialog.Builder(context)
        builder.setTitle("ACHTUNG")
        builder.setMessage("Bist Du sicher, dass Du sämtliche Daten löschen willst?")

        builder.setPositiveButton("OK") { _, _ ->

            // All Data will be initialized (Items, BudgetItems and Configuration)
            viewModel.initializeAllData()
        }

        builder.setNegativeButton("Cancel") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}

@Composable
fun MyLazyBudgetList(configList: List<Configuration>, year: Int, viewModel: RegistrationViewModel) {
    LazyColumn {
        items(configList) { config ->
            // Composable item that displays each Configuration
            ConfigBudgetItem(config, year, viewModel)
        }
    }
}

@Composable
fun MyLazyLiveDataList(configList: List<Configuration>, year: Int) {
    LazyColumn {
        items(configList) { config ->
            // Composable item that displays each Configuration
            ConfigLiveDataItem(config, year)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigBudgetItem(config: Configuration, year: Int, viewModel: RegistrationViewModel) {

    val df = DecimalFormat("#,##0.00")
    val context = LocalContext.current
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showDialog by remember { mutableStateOf(false) }

    var showJsonDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val observer = Observer<String> { observedMessage ->
            if (observedMessage.isNotBlank()) {
                Toast.makeText(context, observedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.messageLiveData.observeForever(observer)
        onDispose {
            viewModel.messageLiveData.removeObserver(observer)
        }
    }

    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.gray))
    ) {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .weight(0.5f)
                .clickable(
                    enabled = true,
                    onClick = {
                        when (config.status) {
                            1 -> Toast
                                .makeText(
                                    context, "Dieses Budget muss erst wiedereröffnet werden!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                            2 -> Toast
                                .makeText(
                                    context, "Dieses Budget ist bereits archiviert!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                            else -> showJsonDialog = true
                        }

                    }
                ),
            painter = when (config.status) {
                2 -> painterResource(id = R.drawable.baseline_archive_24)
                1 -> painterResource(id = R.drawable.baseline_lock_24)
                else -> painterResource(id = R.drawable.baseline_lock_open_24)
            },
            contentDescription = "Budget locked",
            tint = colorResource(id = R.color.white)
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(0.5f),
            text = config.budgetYear.toString(),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Left,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            text = df.format(config.approxStartSaldo),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            text = df.format(config.approxEndSaldo),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
        )
        Icon(
            modifier = Modifier
                .background(colorResource(id = R.color.black))
                .padding(4.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        showDialog = true
                    },
                )
                .weight(0.5f),
            painter = painterResource(id = R.drawable.baseline_copy_all_24),
            contentDescription = "Budget locked",
            tint = colorResource(id = R.color.white)
        )
    }

    if (showDialog) {

        // actual Year + next two Years
        val years = arrayOf(year.toString(), (year + 1).toString(), (year + 2).toString())

        // Radio Button
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Kopiere Budget für das Jahr\nDaten werden überschrieben!")

// Create a view to hold the radio buttons
        val radioGroup = RadioGroup(context)
        years.forEachIndexed { index, year ->
            val radioButton = RadioButton(context)
            radioButton.text = year
            radioButton.id = index // Set unique IDs for each radio button
            radioGroup.addView(radioButton)
        }

// Set the radio group as the view in the dialog
        builder.setView(radioGroup)

        builder.setPositiveButton("OK") { dialog, _ ->
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                selectedYear = years[selectedRadioButtonId].toInt()

                // Handle the selected year here
                println("RegistrationScreen - showDialog - Selected Year: $selectedYear")

                if (config.budgetYear == selectedYear) {
                    Toast.makeText(
                        context,
                        "Quell- und Zieljahr sind identisch",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Copy source budget to target
                    coroutineScope.launch {
                        viewModel.copyBudgetFromTo(config.budgetYear, selectedYear)
                    }
                }
            }
            showDialog = false
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            showDialog = false
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    if (showJsonDialog == true) {
        showJsonDialog = false
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Löschen oder Musterbudget generieren?")
        builder.setMessage("Dein bestehendes Budget löschen oder ein neues generieren?")

        builder.setPositiveButton("Single") { _, _ ->
            // Handle save action here
            // Implement the logic for saving data
            viewModel.generateBudgetFromToJson(config.budgetYear, "single")
        }

        builder.setNegativeButton("Family") { _, _ ->
            // Handle restore action here
            // Implement the logic for restoring data
            viewModel.generateBudgetFromToJson(config.budgetYear, "family")
        }

        builder.setNeutralButton("Delete") { _, _ ->
            // Handle delete action here
            // Implement the logic for deleting data
            viewModel.deleteBudgetOfYear(config.budgetYear)
        }

        val dialog = builder.create()
        dialog.show()
    }


}

@Composable
fun ConfigLiveDataItem(config: Configuration, year: Int) {

    val df = DecimalFormat("#,##0.00")

    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.semi_gray))
    ) {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .weight(0.5f),
            painter = when (config.budgetYear == year) {
                true -> painterResource(id = R.drawable.baseline_power_24)
                else -> if (config.status > 1) {
                    painterResource(id = R.drawable.baseline_archive_24)
                } else painterResource(id = R.drawable.baseline_power_off_24)
            },
            contentDescription = "Live-Data active",
            tint = colorResource(id = R.color.white)
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(0.5f),
            text = config.budgetYear.toString(),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Left,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            text = df.format(config.startSaldo),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .weight(1f),
            text = df.format(config.endSaldo),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
        )
    }
}


