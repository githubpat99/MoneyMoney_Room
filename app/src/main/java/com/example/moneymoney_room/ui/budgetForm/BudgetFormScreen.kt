package com.example.moneymoney_room.ui.budgetForm

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.BudgetItem
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object BudgetFormDestination : NavigationDestination {
    override val route = "budgetForm"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"   //todo PIN: itemIdArg always in {}
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
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: BudgetFormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    var startSaldo: String by remember {
        mutableStateOf(viewModel.approxStartSaldo.toString())
    }

    // pin: Without LaunchedEffect the value remains initial - means "0.0"
    LaunchedEffect(viewModel.approxStartSaldo) {
        startSaldo = viewModel.approxStartSaldo.toString()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var tabState = remember { mutableStateOf(0) }
    if (viewModel.value == 1) {
        tabState = remember { mutableStateOf(1) }
    }

    val tabs = listOf("Einnahmen", "Ausgaben")
    val budgetItems by viewModel.budgetItems.collectAsState(initial = BudgetItems())
    val approxEndSaldo = viewModel.calculateApproxEndSaldo(viewModel.approxStartSaldo, budgetItems)

    val einnahmen = budgetItems.list.filter { it.debit == true }
    val ausgaben = budgetItems.list.filter { it.debit == false }

    val budgetConfig by viewModel.configuration.collectAsState(initial = null)
    var budgetStatus = 0

    if (budgetConfig != null) {
        budgetStatus = budgetConfig!!.status

        println("BudgetFormScreen - budgetStatus - in If: $budgetStatus")
    }

    println("BudgetFormScreen - budgetStatus - after If: $budgetStatus")

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
                .background(colorResource(id = R.color.light_gray))
        ) {

            val year = "2023"   //todo PIN: only for Visualization

            Row(
                modifier = Modifier
                    .padding(top = 72.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Budget",
                    style = TextStyle(
                        colorResource(id = R.color.gray),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.2f)
                )
                Text(
                    text = year,
                    style = TextStyle(
                        colorResource(id = R.color.gray),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.2f)
                )
                Text(
                    text = "1.1.$year",
                    style = TextStyle(
                        colorResource(id = R.color.gray),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.3f)
                )

                val decimalFormatter = DecimalFormatter()

                val modifierFlexible = if (budgetStatus == 1)
                    Modifier.background(colorResource(id = R.color.light_gray))
                else Modifier.background(colorResource(id = R.color.white))

                BasicTextField(
                    value = startSaldo,
                    enabled = budgetStatus == 0,
                    onValueChange = {

                        println("pin - it: $it")

                        startSaldo = decimalFormatter.cleanup(it)

                        println("pin - startSaldo: $startSaldo")

                        if (startSaldo.isNotBlank()) {
                            viewModel.updateApproxSaldi(startSaldo, budgetItems)
                        } else {
                            viewModel.updateApproxSaldi("0.0", budgetItems)
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),

                    // pin - Does not work properly
                    //visualTransformation = DecimalInputVisualTransformation(decimalFormatter),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.4f)
                        .then(modifierFlexible)
                )
            }
            // Create Tab Row
            TabRow(
                selectedTabIndex = tabState.value,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabState.value]),
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
                        selected = tabState.value == index,
                        onClick = { tabState.value = index },
                        text = {
                            Text(
                                text = title,
                                color = textColor,
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                }

            }

            Box(
                modifier = Modifier.weight(0.8f)
            ) {
                // Display the content based on the selected tab
                when (tabState.value) {
                    0 -> {
                        // "Ausgaben" tab

                        BudgetFormScreenBodyEinnahmen(
                            onAddClicked = {
                                coroutineScope.launch {
                                    viewModel.saveBudgetItem(it)
                                }

                            },
                            einnahmen,
                            navigateBack,
                            navigateToBudgetDetails,
                            budgetStatus
                        )
                    }

                    1 -> {
                        // "Einnahmen" tab

                        BudgetFormScreenBodyAusgaben(
                            onAddClicked = {
                                coroutineScope.launch {
                                    viewModel.saveBudgetItem(it)
                                }

                            },
                            ausgaben,
                            navigateBack,
                            navigateToBudgetDetails,
                            budgetStatus
                        )
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
                            colorResource(id = R.color.gray),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.2f)
                    )
                    Text(
                        text = year,
                        style = TextStyle(
                            colorResource(id = R.color.gray),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.2f)
                    )
                    Text(
                        text = "31.12.$year",
                        style = TextStyle(
                            colorResource(id = R.color.gray),
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

                    val formattedEndSaldo = decimalFormat.format(approxEndSaldo)

                    Text(
                        text = formattedEndSaldo,
                        style = TextStyle(
                            colorResource(id = R.color.gray),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(0.4f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .clickable {
                        // toggle BudgetStatus
                        viewModel.toggleBudgetStatus()
                    }
            ) {

                var budgetText = "Budget ready -> save & go..."
                if (budgetStatus == 1) {
                    budgetText = "Budget done -> goto Forecast..."
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
            }
        }
    }
}

@Composable
fun BudgetFormScreenBodyEinnahmen(
    onAddClicked: (BudgetItem) -> Unit,
    budgetItems: List<BudgetItem>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    budgetStatus: (Int),
) {
    BudgetFormTabContent(
        onAddClicked,
        budgetItems = budgetItems.toMutableList(),
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails,
        backgroundColor = colorResource(id = R.color.light_blue),
        debit = true,
        budgetStatus = budgetStatus
    )
}

@Composable
fun BudgetFormScreenBodyAusgaben(
    onAddClicked: (BudgetItem) -> Unit,
    budgetItems: List<BudgetItem>,
    navigateBack: () -> Unit,
    navigateToDetails: (Int) -> Unit,
    budgetStatus: (Int),
) {
    BudgetFormTabContent(
        onAddClicked,
        budgetItems = budgetItems.toMutableList(),
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails,
        backgroundColor = colorResource(id = R.color.light_red),
        debit = false,
        budgetStatus = budgetStatus
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
) {

    val context = LocalContext.current

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
                // Floating button at the bottom
                val bStroke = if (budgetStatus == 0)
                    BorderStroke(1.dp, colorResource(id = R.color.white))
                else
                    BorderStroke(0.dp, colorResource(id = R.color.black))
                Button(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.BottomStart),
                    onClick = {
                        val ts = Utilities.getCurrentTimeInMillis() / 1000
                        val budgetItem: BudgetItem =
                            BudgetItem(0, ts, "Neu", "tbd", 12, 0.0, 0.0, debit)
                        onAddClicked(budgetItem)
                    },
                    enabled = budgetStatus == 0,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorResource(id = R.color.white),
                        containerColor = backgroundColor
                    ),
                    border = bStroke
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
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        stickyHeader {
            // Header row

            val fontSize = 16.sp
            val fontWeight = FontWeight.SemiBold

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                Text(
                    text = " Name",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                )
                Text(
                    text = " Betrag",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(4.dp)
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
                        .padding(4.dp)
                        .weight(1f)
                )
                Text(
                    text = "Kadenz",
                    style = TextStyle(
                        colorResource(id = R.color.white),
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                )
            }
        }

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
            .background(backgroundColor)
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
                    .weight(1f)
            )

            Text(
                text = Utilities.getStringDateFromTimestamp(budgetItem.timestamp),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(1f)
            )
            Text(
                text = Utilities.getKadenz(budgetItem.type),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .padding(0.dp)
                    .weight(1f)
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
