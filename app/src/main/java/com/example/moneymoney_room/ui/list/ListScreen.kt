package com.example.moneymoney_room.ui.list

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.CreateBudgetScreen
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val configuationState = viewModel.configuration.collectAsState(initial = null)
    var userName: String = "Bitte Budget erstellen"
    var startSaldo: Double = 0.0

    val configurationValue = configuationState.value


    if (configurationValue != null) {
        startSaldo = configurationValue.startSaldo
        userName = configurationValue.budgetYear.toString()
        // Use the configuration values in your Composable
    } else {
        // Handle the case where configuration is not set yet
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = userName,
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
    viewModel: ListViewModel,
    onItemClick: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,

    ) {

    var saldoState = remember { mutableStateOf(startSaldo) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val listUiState: ListUiState by viewModel.listUiState.collectAsState()

    if (listUiState.list.isEmpty()) {
        // Noch kein Budget vorhanden

        CreateBudgetScreen(viewModel, context, coroutineScope)
    } else {

        val endSaldo = calculateEndSaldo(listUiState.list, startSaldo)

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            val saldoTxt = Utilities.formatDoubleToString(saldoState.value)

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
                            .format(saldoState.value)


                    CustomStyledText(
                        text = "Now",
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
                listUiState.list,
                startSaldo,
                onItemClick = { onItemClick(it.id) },
                navigateToEntry,
                onSaldoChange = { newSaldo ->
                    saldoState.value = newSaldo
                }

            )
        }
    }
}


fun calculateEndSaldo(list: List<Item>, startSaldo: Double): Double {
    var saldo = startSaldo
    for (item in list) {

        println("Saldo = $saldo")

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

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val today = Utilities.getNowAsLong()

    var todayItemIndex = itemList.indexOfFirst { it.timestamp > today }

    if (todayItemIndex < 0)
        todayItemIndex = 0

    LazyColumn(
        state = lazyListState
    ) {

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

    LaunchedEffect(todayItemIndex) {
        // Scroll to the desired index when the effect is launched
        coroutineScope.launch {
            lazyListState.scrollToItem(todayItemIndex)
        }
    }

    println("ListScreen - LazyColumn - firstVisItemIndex = ${lazyListState.firstVisibleItemIndex}")
    val idx = lazyListState.firstVisibleItemIndex + 1
    var i = 0
    var newSaldo = startSaldo
    while (i < idx) {
        newSaldo = newSaldo + itemList[i].amount
        i++
    }

    onSaldoChange(newSaldo)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: Item, modifier: Modifier = Modifier,
) {
    val myCardModifier = modifier
        .padding(4.dp)
        .background(color = colorResource(id = R.color.white))

    val now = Utilities.getNowAsLong()
    val visualDate = Utilities.getTimestampAsDate(now)

    var itemColor = colorResource(id = R.color.dark_blue)
    if (item.amount < 0)
        itemColor = colorResource(id = R.color.dark_red)

    val fontFamily = FontFamily.Default

    val textStyle = TextStyle(
        color = itemColor,
        fontFamily = fontFamily, // Set the appropriate font family here
        textAlign = TextAlign.End
    )

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
        Column {
            Text(
                modifier = myCardModifier,
                color = colorResource(id = R.color.gray),
                text = Utilities.getTimestampAsDate(item.timestamp)
            )
            Text(
                modifier = myCardModifier,
                color = colorResource(id = R.color.gray),
                text = item.name
            )
        }

        BasicText(
            text = item.description,
            style = textStyle
        )

        Spacer(modifier = Modifier.weight(0.5f))
        BasicText(
            text = item.amount.toString(),
            style = textStyle
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

@Composable
fun GoogleSheetsLink(
    modifier: Modifier = Modifier,
    text: String,
    viewModel: ListViewModel,
    context: Context,
) {
    val coroutineScope = rememberCoroutineScope()
    val text = text

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.None)) {
                append(text)
            }
            appendLine()
            append("(Klicken, Bearbeiten, Importieren)") // Additional instructions
        },
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White, // Color for the link text
        textAlign = TextAlign.Center, // Center-align the text
        fontSize = 16.sp, // Adjust the font size as needed
        modifier = modifier
            .clickable {
                // Handle the link click action here
                /*
                Old Version with Browser Call

                val url =
                    "https://docs.google.com/spreadsheets/d/112hN7on-j8OzBzLya96zxl7wkS3GJ6C4bkIJ94Ja8R0/edit#gid=672646673"
                openUrlInBrowser(context, url)
                */

                // New Version with direct Call via - Square’s meticulous HTTP client for Java and Kotlin
                callGoogleAppsScriptFunction(coroutineScope, viewModel, context)
            }
            .fillMaxWidth()
    )
}

private fun openUrlInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    println("ListScreen - openUrlInBrowser - context: $context}")
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Handle the case where a suitable activity to open the URL is not found.
    }
}


fun callGoogleAppsScriptFunction(
    coroutineScope: CoroutineScope,
    viewModel: ListViewModel,
    context: Context,
) {

    val scriptUrl = viewModel.googleAppsScriptUrl
    val json = """
        {
            "function": "copySheetWithFunctionAndOpen",
            "parameters": ["CSV_Data", "New-Budget 2023"]
        }
    """

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val requestBody = json.toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
        .url(scriptUrl)
        .post(requestBody)
        .build()

    viewModel.isRunning = true
    println("ListScreen - isRunning: ${viewModel.isRunning}")
    var i = 1

    coroutineScope.launch {
        try {

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                // Handle error response
                println("ListScreen - Request failed with code: ${response.code}")
                // Handle the error state in your app
                return@launch
            }
            // Check if the response body is null
            if (response.body == null) {
                println("ListScreen - Response body is null")
                return@launch
            }

            val responseBody = response.body?.string()
            println("ListScreen - responseBody: $responseBody")

            val url = responseBody?.let { extractUrlFromGoogleResponse(it) }

            if (isValidUrl(url)) {
                if (url != null) {

                    println("ListScreen - responseBody: $url")
                    viewModel.saveSpreadsheetId(url)
                    openUrlInBrowser(context, url)
                }
            }
        } catch (e: Exception) {
            // Handle the exception
            println("ListScreen - Error: ${e.message}")
            // Handle the error state in your app
        } finally {
            viewModel.isRunning = false
            println("ListScreen - isRunning: ${viewModel.isRunning}")
        }
    }
}

fun isValidUrl(url: String?): Boolean {
    try {
        URL(url)
        return true
    } catch (e: MalformedURLException) {
        return false
    }
}

fun extractUrlFromGoogleResponse(googleResponse: String): String {
    // Parse the Google response JSON
    val json = JSONObject(googleResponse)

    // Extract the URL value associated with the "url" key
    return json.getString("url")
}
