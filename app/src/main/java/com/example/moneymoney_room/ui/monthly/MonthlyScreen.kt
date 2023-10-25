package com.example.moneymoney_room.ui.monthly

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.CreateBudgetScreen
import com.example.moneymoney_room.ui.list.ListViewModel
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object MonthlyDestination : NavigationDestination {
    override val route = "monthly"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonthlyScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToMonthlyDetail: (Int) -> Unit,        //todo PIN: Navigate to xxxList
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val configuationState = viewModel.configuration.collectAsState(initial = null)
    var startSaldo = 0.0

    if (configuationState.value != null) {
        startSaldo = configuationState.value!!.startSaldo
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = "Monatsübersicht",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ListScreenBody(
            viewModel,
            onItemClick = navigateToMonthlyDetail,
            navigateToEntry,
            startSaldo
        )
    }
}

@Composable
fun ListScreenBody(
    viewModel: MonthlyViewModel,
    onItemClick: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,

    ) {

    var saldoState = remember { mutableStateOf(startSaldo) }
    val monthlyUiState: MonthlyUiState by viewModel.monthlyUiState.collectAsState()
    val endSaldo = calculateEndSaldo(monthlyUiState.list, startSaldo)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val listViewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)

    if (monthlyUiState.list.isEmpty()) {

        // Create Budget first
        CreateBudgetScreen(
            viewModel = listViewModel,
            context = context,
            coroutineScope = coroutineScope
        )
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(top = 64.dp),

                ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedSaldo: String =
                        NumberFormat.getCurrencyInstance(Locale("de", "CH")).format(startSaldo)


                    CustomStyledText(
                        text = "Start 1.1.2023",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    CustomStyledText(
                        text = "$formattedSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal
                    )
                }

                var background = Color.Green
                if (endSaldo < startSaldo) {
                    background = Color.Red
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedSaldo: String =
                        NumberFormat.getCurrencyInstance(Locale("de", "CH")).format(endSaldo)


                    CustomStyledText(
                        text = "End   31.12.2023",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    CustomStyledText(
                        text = "$formattedSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formattedSaldo: String =
                        NumberFormat.getCurrencyInstance(Locale("de", "CH"))
                            .format(endSaldo - startSaldo)


                    CustomStyledText(
                        text = "Saldo",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    CustomStyledText(
                        text = "$formattedSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }

    // Body (Main content)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 180.dp, bottom = 24.dp)
    ) {

        ShowOnlyRelevantElements(
            monthlyUiState.list,
            startSaldo,
            onItemClick = { onItemClick(it.length) },
            navigateToEntry,
            onSaldoChange = { newSaldo ->
                saldoState.value = newSaldo
            }

        )
    }
}

fun calculateEndSaldo(list: List<Item>, startSaldo: Double): Double {
    var saldo = startSaldo
    for (item in list) {
        saldo += item.amount
    }
    return saldo
}


@Composable
fun ShowOnlyRelevantElements(
    itemList: List<Item>,
    startSaldo: Double,
    onItemClick: (String) -> Unit,
    navigateToEntry: () -> Unit,
    onSaldoChange: (Double) -> Unit,

    ) {

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState
    ) {
        val monthlyTotals = mutableMapOf<String, Double>()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val monthFormat = SimpleDateFormat("MM")

        for (item in itemList) {
            val date = dateFormat.parse(Utilities.getTimestampAsDate(item.timestamp))
            val month = monthFormat.format(date)
            monthlyTotals[month] = monthlyTotals.getOrDefault(month, 0.0) + item.amount
        }

        items(monthlyTotals.keys.toList()) { month ->
            val totalAmount = monthlyTotals[month] ?: 0.0
            println("$month: %.2f".format(totalAmount))

            MonthlyCard(
                month,
                totalAmount,
                modifier = Modifier.clickable { onItemClick(month) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCard(
    month: String, totalAmount: Double, modifier: Modifier = Modifier,
) {
    val myCardModifier = modifier
        .padding(4.dp)
        .background(color = colorResource(id = R.color.white))

    var itemColor = colorResource(id = R.color.dark_blue)
    if (totalAmount < 0)
        itemColor = colorResource(id = R.color.dark_red)

    val fontFamily = FontFamily.Default

    val textStyle = TextStyle(
        color = itemColor,
        fontFamily = fontFamily, // Set the appropriate font family here
        textAlign = TextAlign.End
    )
    val monthlyText = mutableMapOf<String, String>(
        "01" to "Januar",
        "02" to "Februar",
        "03" to "März",
        "04" to "April",
        "05" to "Mai",
        "06" to "Juni",
        "07" to "Juli",
        "08" to "August",
        "09" to "September",
        "10" to "Oktober",
        "11" to "November",
        "12" to "Dezember"
    )

    val df = DecimalFormat("#,###.00", DecimalFormatSymbols(Locale("de", "CH")))
    val formattedTotalAmount = df.format(totalAmount)

    Row(
        modifier = myCardModifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = myCardModifier
                .size(8.dp),
            painter = painterResource(id = R.drawable.baseline_24),
            contentDescription = "Money-Sign"
        )

        Text(
            modifier = myCardModifier,
            color = colorResource(id = R.color.gray),
            text = monthlyText[month].toString()
        )
        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            modifier = myCardModifier,
            color = itemColor,
            text = formattedTotalAmount
        )
    }
}


@Composable
fun CustomStyledText(
    text: String,
    textAlign: TextAlign,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    // Define a custom TextStyle with the provided textAlign
    val customTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        fontWeight = fontWeight,
        color = Color.Black,
        textAlign = textAlign,
        // Add any other desired style properties here
    )

    // Apply the custom TextStyle to the BasicText
    BasicText(
        text = text,
        style = customTextStyle,
        modifier = modifier.padding(8.dp)
    )
}