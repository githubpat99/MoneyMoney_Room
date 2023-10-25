package com.example.moneymoney_room.ui.MonthlyDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.list.ItemCard
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import java.text.NumberFormat
import java.util.Locale

object MonthlyDetailDestination : NavigationDestination {
    override val route = "monthlyDetail"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"   //todo PIN: itemIdArg always in {}
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonthlyDetailsScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: MonthlyDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val configurationState = viewModel.configuration.collectAsState(initial = null)
    var startSaldo = 0.0

    if (configurationState.value != null) {
        startSaldo = configurationState.value!!.startSaldo
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = "Monatsdetail",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ListScreenBody(
            viewModel,
            onItemClick = navigateToDetail,
            navigateToEntry,
            startSaldo
        )
    }
}

@Composable
fun ListScreenBody(
    viewModel: MonthlyDetailsViewModel,
    onItemClick: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,

    ) {

    var saldoState = remember { mutableStateOf(startSaldo) }
    val monthlyUiState: MonthlyDetailsUiState by viewModel.monthlyUiState.collectAsState()
    val endSaldo = calculateEndSaldo(monthlyUiState.list, startSaldo)

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
            onItemClick = { onItemClick(it.id) },
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
    onItemClick: (Item) -> Unit,
    navigateToEntry: () -> Unit,
    onSaldoChange: (Double) -> Unit,

    ) {

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState
    ) {

        println("MonthlyDetailsScreen - ShowOnlyRelevantElements - items = $itemList")

        items(items = itemList) {

            if (it.amount != 0.00) {
                ItemCard(
                    item = it,
                    modifier = Modifier
                        .clickable { onItemClick(it) }
                )
            }
        }
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