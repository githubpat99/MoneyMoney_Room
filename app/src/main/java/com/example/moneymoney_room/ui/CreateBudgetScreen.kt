package com.example.moneymoney_room.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.list.ListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

@Composable
fun CreateBudgetScreen(
    viewModel: ListViewModel,
    context: Context,
    coroutineScope: CoroutineScope,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary_background))
            .padding(top = 64.dp),

        ) {
        Text(
            text = "Bitte zuerst ein Budget erstellen oder downloaden",
            color = Color.White,
            fontSize = 20.sp, // Customize the font size
            textAlign = TextAlign.Center, // Center-align the text
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Add padding around the text
        )

        Spacer(
            modifier =
            Modifier
                .padding(16.dp)
                .background(Color.Gray)
        )

        GoogleSheetsLink(
            modifier = Modifier
                .background(Color.Gray)
                .height(150.dp),
            "Erstelle Deine Budget-Tabelle",
            viewModel,
            context
        )

        // GET Request via Google Sheet API
        val spreadsheetId = viewModel.spreadSheetId
        val sheetName = viewModel.sheetName

        // todo PIN Test
        println("ListScreen - ListScreenBody - spreadsheetId = $spreadsheetId")

        val range = "$sheetName!A1:J313"
        val apiKey = "AIzaSyB1MSBkTf1EfQwq04krYq5iGG1uvRbkzns"
        val url =
            URL("https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$range?key=$apiKey")

        Button(
            onClick = {
                viewModel.isImporting = true

                coroutineScope.launch {
                    try {

                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"

                        val responseData = withContext(Dispatchers.IO) {
                            connection.connect()
                            connection.inputStream.bufferedReader().use { it.readText() }
                        }

                        if (connection.responseCode == 200) {
                            // Process the responseData as needed
                            viewModel.processCsvData(responseData)
                            // Delete the Created Google Spreadsheet - does not work
                            // Done by Trigger Event in Google Sheets
                            // viewModel.deleteGoogleSpreadsheet()
                        }
                    } catch (e: Exception) {
                        // Handle any errors
                        println("error = ${e.localizedMessage}")
                        Toast.makeText(
                            context,
                            "Error calling Apps Script: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally),
            enabled = !spreadsheetId.isEmpty()
        ) {
            Text("Budget jetzt importieren!")
        }

        if (viewModel.isImporting || viewModel.isRunning) {

            println("LinearProgressIndicator")
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GoogleSheetsLink(
    modifier: Modifier = Modifier,
    text: String,
    viewModel: ListViewModel,
    context: Context) {
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
    context: Context
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
