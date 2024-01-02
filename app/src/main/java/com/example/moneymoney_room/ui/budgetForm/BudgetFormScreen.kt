package com.example.moneymoney_room.ui.budgetForm

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.BudgetItem
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import com.example.moneymoney_room.util.YesNoDialog
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

object BudgetFormDestination : NavigationDestination {
    override val route = "budgetForm"
    override val titleRes = R.string.app_name
    const val year = "year"
    const val tab = "tab"
    val routeWithArgs = "$route/{$year}/{$tab}"   //todo PIN: itemIdArg always in {}
}

/**
 * Entry route for Home screen
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BudgetFormScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToBudgetDetails: (Int) -> Unit,
    navigateToOverview: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: BudgetFormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    var context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val paramYear = viewModel.year
    val paramTab = viewModel.tab
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Einnahmen", "Ausgaben")
    var tabState = remember { mutableStateOf("0") }
    if (paramTab == "1") {
        tabState = remember { mutableStateOf("1") }
    }
    var budgetStatus = 0
    var budgetDate = "open"
    val year = paramYear

    var configUiState = viewModel.configuration.collectAsState(initial = null)

    val appTimeZone = MoneyMoneyApplication.instance.appTimeZone


    val tzMillis = appTimeZone.rawOffset
    val timezoneLongSeconds: Long = tzMillis / 1000L

    val budgetItemState =
        viewModel.budgetItemsRepository.getAllBudgetItemsStreamForYearTZ(year, timezoneLongSeconds)
            .collectAsState(initial = BudgetItems().list)
    val budgetItems = budgetItemState.value

    var startSaldo = configUiState.value?.approxStartSaldo
    var saldoDouble by remember {
        mutableStateOf(configUiState.value?.approxStartSaldo)
    }
    var approxEndSaldo = configUiState.value?.approxEndSaldo
    // Your logic when both configUiState and budgetItems are available
    // This block will execute only when both variables are not null

    if (startSaldo != null) {
        viewModel.updateApproxSaldi(startSaldo, budgetItems, paramYear)
    }

    val einnahmen = budgetItems.filter { it.debit == true }
    val ausgaben = budgetItems.filter { it.debit == false }

    println("BudgetFormScreen - ausgaben: $ausgaben")

    budgetStatus = configUiState.value?.status ?: 0
    budgetDate =
        Utilities.getStringDateFromTimestamp(configUiState.value?.ts ?: 1672531200) // 1.1.2023


    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.budgetFormScreen),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.semi_gray))
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 72.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Budget",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.2f)
                )
                Text(
                    text = year.toString(),
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.2f)
                )
                Text(
                    text = "1.1.$year",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.3f)
                )

                val modifierFlexible = if (budgetStatus == 1)
                    Modifier.background(colorResource(id = R.color.light_gray))
                else Modifier.background(colorResource(id = R.color.white))

                val decimalFormat = DecimalFormat("#.##")
                var startSaldoText = "0.0"
                if (startSaldo != null) {
                    startSaldoText = decimalFormat.format(startSaldo) // from configUiState
                }

                BasicTextField(
                    value = startSaldoText,
                    enabled = budgetStatus == 0,
                    onValueChange = { newValue ->
                        // todo PIN - still not absolutely happy with the behaviour
                        // Matches up to seven digits before the decimal point and up to two decimal places
                        val pattern = Regex("""^\d{1,7}(\.\d{1,2})?$""")

                        var doubleValue = 0.0
                        if (newValue.isNotBlank()) {
                            doubleValue = newValue.toDoubleOrNull() ?: 0.0
                        }

                        if (doubleValue != null) {
                            startSaldo = doubleValue
                            println("BudgetformScreen - startSaldo 1: $startSaldo")
                        }

                        if (pattern.matches(doubleValue.toString())) {
                            val matchingValue = doubleValue.toString()

                            // The entered value matches the pattern
                            // Update your ViewModel field here
                            val new =
                                configUiState.value?.copy(approxStartSaldo = matchingValue.toDouble())
                            if (new != null) {
                                viewModel.updateConfigUiState(new)
                            }
                        } else {
                            // The entered value does not match the pattern
                            // Handle incorrect input format here
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    // pin
                    //visualTransformation = DecimalInputVisualTransformation(decimalFormatter),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.4f)
                        .then(modifierFlexible)
                )
            }
            // Create Tab Row

            TabRow(
                selectedTabIndex = tabState.value.toInt(),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabState.value.toInt()]),
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    val textColor = colorResource(id = R.color.white)

                    var backgroundColor = if (index == 0) {
                        colorResource(id = R.color.light_blue)
                    } else {
                        colorResource(id = R.color.light_red)
                    }

                    Tab(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor),
                        selected = tabState.value.toInt() == index,
                        onClick = { tabState.value = index.toString() },
                        text = {
                            Text(
                                text = title,
                                color = textColor,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    )
                }

            }

            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .background(color = Color.White)
            ) {
                // Display the content based on the selected tab
                when (tabState.value) {
                    "0" -> {
                        // "Ausgaben" tab

                        if (year != null) {
                            BudgetFormScreenBodyEinnahmen(
                                onAddClicked = {
                                    coroutineScope.launch {
                                        viewModel.saveBudgetItem(it)
                                    }

                                },
                                einnahmen,
                                navigateBack,
                                navigateToBudgetDetails,
                                budgetStatus,
                                year
                            )
                        }
                    }

                    "1" -> {
                        // "Einnahmen" tab

                        if (year != null) {
                            BudgetFormScreenBodyAusgaben(
                                onAddClicked = {
                                    coroutineScope.launch {
                                        viewModel.saveBudgetItem(it)
                                    }

                                },
                                ausgaben,
                                navigateBack,
                                navigateToBudgetDetails,
                                budgetStatus,
                                year
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            ) {

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Budget",
                        style = TextStyle(
                            colorResource(id = R.color.white),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.2f)
                    )
                    Text(
                        text = year.toString(),
                        style = TextStyle(
                            colorResource(id = R.color.white),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.2f)
                    )
                    Text(
                        text = "31.12.$year",
                        style = TextStyle(
                            colorResource(id = R.color.white),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.3f)
                    )

                    val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
                    decimalFormatSymbols.groupingSeparator = '\''
                    decimalFormatSymbols.decimalSeparator = '.'

                    val decimalFormat = DecimalFormat("#,##0.00", decimalFormatSymbols)
                    var formattedEndSaldo = "0.0"

                    if (approxEndSaldo != null) {
                        formattedEndSaldo = decimalFormat.format(approxEndSaldo)
                    }


                    Text(
                        text = formattedEndSaldo,
                        style = TextStyle(
                            colorResource(id = R.color.white),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.4f)
                    )
                }
            }

            if (budgetStatus < 2) {
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .clickable {
                        // toggle BudgetStatus and update Timestamp
                        if (year != null) {
                            showDialog = true
                        }
                    }
            ) {

                var budgetText = "Budget bereit - für Live Daten hier klicken"
                if (budgetStatus == 1) {
                    budgetText =
                        "Budget am $budgetDate geschlossen - für Wiedereröffnung hier klicken"
                }
                Text(
                    text = budgetText,
                    style = TextStyle(
                        colorResource(id = R.color.primary_background),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(2.dp)

                )
            }}
        }
    }

    // Display the Yes/No dialog based on showDialog state
    YesNoDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onYesClick = {

            viewModel.handleYesClick(year, budgetStatus, budgetItems)
            showDialog = false // Close the dialog

            if (budgetStatus == 0) {
                onNavigateUp()          // Navigate back to Home Screen
            }
        },

        onNoClick = {
            showDialog = false // Close the dialog if "No" is clicked
        }
    )
}


@Composable
fun BudgetFormScreenBodyEinnahmen(
    onAddClicked: (BudgetItem) -> Unit,
    budgetItems: List<BudgetItem>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    budgetStatus: (Int),
    year: String,
) {
    BudgetFormTabContent(
        onAddClicked,
        budgetItems = budgetItems.toMutableList(),
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails,
        backgroundColor = colorResource(id = R.color.light_blue),
        debit = true,
        budgetStatus = budgetStatus,
        year = year
    )
}

@Composable
fun BudgetFormScreenBodyAusgaben(
    onAddClicked: (BudgetItem) -> Unit,
    budgetItems: List<BudgetItem>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    budgetStatus: (Int),
    year: String,
) {
    BudgetFormTabContent(
        onAddClicked,
        budgetItems = budgetItems.sortedWith(compareBy({ it.timestamp }, { -it.type })).toMutableList(),
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails,
        backgroundColor = colorResource(id = R.color.light_red),
        debit = false,
        budgetStatus = budgetStatus,
        year = year
    )
}

@Composable
fun BudgetFormTabContent(
    onAddClicked: (BudgetItem) -> Unit,
    budgetItems: MutableList<BudgetItem>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    backgroundColor: Color,
    debit: Boolean,
    budgetStatus: Int,
    year: String,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Keep track of the selected tab index
            MyLazyList(
                budgetItems = budgetItems,
                navigateToDetails = navigateToDetails,
                backgroundColor = backgroundColor,
                budgetStatus = budgetStatus
            )
        }

        if (budgetStatus == 0) {
            Box(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                Button(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.BottomStart),
                    onClick = {
                        val yearInt = year.toInt()
                        val startOfYear = LocalDateTime.of(yearInt, Month.JANUARY, 1, 1, 0)
                        val appTimeZone = MoneyMoneyApplication.instance.appTimeZone
                        val zoneId = ZoneId.of(appTimeZone.id)
                        val zoneOffset = zoneId.rules.getOffset(java.time.Instant.now())
                        val ts = startOfYear.toEpochSecond(zoneOffset)
                        val budgetItem: BudgetItem =
                            BudgetItem(0, ts, "Neu", "", 12, 0.0, 0.0, debit)
                        onAddClicked(budgetItem)
                    },
                    enabled = budgetStatus == 0,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        containerColor = backgroundColor
                    ),
                    border = BorderStroke(1.dp, colorResource(id = R.color.white))
                ) {

                    Icon(
                        painterResource(id = R.drawable.add_24), // Use your custom "+" icon
                        contentDescription = null, // Provide a content description if needed
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyLazyList(
    budgetItems: List<BudgetItem>,
    navigateToDetails: (Int) -> Unit,
    backgroundColor: Color,
    budgetStatus: Int,
) {

    // Header row
    val fontSize = 16.sp
    val fontWeight = FontWeight.SemiBold

    Box() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .background(backgroundColor)
                .padding(top = 4.dp, bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "Name",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp, start = 4.dp)
                        .weight(1.6f)
                )
                Text(
                    text = "Betrag",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .weight(1f)
                )
                Text(
                    text = "Valuta",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .weight(0.8f)
                )
                Text(
                    text = "Kadenz",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .weight(1f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
//                .fillMaxWidth()
                .padding(top = 36.dp)
        ) {
            items(budgetItems) { budgetItem ->
                BudgetFormItemRow(
                    budgetItem,
                    navigateToDetails,
                    backgroundColor,
                    budgetStatus
                )
            }
        }
    }
}

@Composable
fun BudgetFormItemRow(
    budgetItem: BudgetItem,
    onItemClicked: (Int) -> Unit,
    backgroundColor: Color,
    budgetStatus: Int,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .background(backgroundColor)
            .padding(top = 2.dp, bottom = 2.dp)
    ) {

        // Row can be clicked only when BudgetStatus = 0 (open)
        val clickableModifier = if (budgetStatus == 0) {
            Modifier.clickable { onItemClicked(budgetItem.id) }
        } else {
            Modifier
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(colorResource(id = R.color.white))
                .padding(8.dp)
                .then(clickableModifier)
        ) {
            Text(
                text = budgetItem.name,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(1f)
            )
            Text(
                text = budgetItem.amount.toString(),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(0.5f)
            )

            Text(
                text = Utilities.getStringDateFromTimestamp(budgetItem.timestamp),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(0.6f)
            )
            Text(
                text = Utilities.getKadenz(budgetItem.type),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(0.5f)
            )
        }
    }
}

class DecimalFormatter(
    symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(),
) {

    private val thousandsSeparator = symbols.groupingSeparator
    private val decimalSeparator = symbols.decimalSeparator

    fun cleanup(input: String): String {

        if (input.matches("\\D".toRegex())) return ""
        if (input.matches("0+".toRegex())) return "0"

        val sb = StringBuilder()

        var hasDecimalSep = false

        for (char in input) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
            }
        }

        return sb.toString()
    }

    fun formatForVisual(input: String): String {

        val split = input.split(decimalSeparator)

        val intPart = split[0]
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()

        val fractionPart = split.getOrNull(1)

        return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
    }
}

class DecimalInputVisualTransformation(
    private val decimalFormatter: DecimalFormatter,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        println("pin - AnnotatedString text: $text")

        val inputText = text.text
        val formattedNumber = decimalFormatter.formatForVisual(inputText)

        val newText = AnnotatedString(
            text = formattedNumber,
            spanStyles = text.spanStyles,
            paragraphStyles = text.paragraphStyles
        )

        val offsetMapping = FixedCursorOffsetMapping(
            contentLength = inputText.length,
            formattedContentLength = formattedNumber.length
        )

        println("pin - inputText.length: ${inputText.length}")
        println("pin - formattedNumber.length: ${formattedNumber.length}")

        return TransformedText(newText, offsetMapping)
    }
}

private class FixedCursorOffsetMapping(
    private val contentLength: Int,
    private val formattedContentLength: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = formattedContentLength
    override fun transformedToOriginal(offset: Int): Int = contentLength
}
