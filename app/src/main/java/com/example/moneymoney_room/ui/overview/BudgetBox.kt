package com.example.moneymoney_room.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymoney_room.R
import com.example.moneymoney_room.util.Utilities.Companion.getFormattedStartAndEndDatesForYear
import java.text.DecimalFormat

@Composable
fun BudgetBox (
    navigateToBudget: (String) -> Unit,
    budgetYear: String,
    approxStart: Double,
    approxEnd: Double,
    budgetDatum: String,
    budgetStatus: Int
) {


    val (startDate, endDate) = getFormattedStartAndEndDatesForYear(budgetYear)
    val decimalFormat = DecimalFormat("#,###.##")
    val approxStartText = decimalFormat.format(approxStart)
    val approxEndText = decimalFormat.format(approxEnd)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(colorResource(id = R.color.gray))
            .clickable { navigateToBudget(budgetYear) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = if (budgetStatus == 1) {
                        painterResource(id = R.drawable.baseline_lock_24)
                    } else {
                        painterResource(id = R.drawable.baseline_lock_open_24)
                    },
                    contentDescription = "Budget locked",
                    tint = colorResource(id = R.color.white)
                )
                Text(
                    text = " Budget",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$startDate - $approxStartText",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f), // Aligns text to the right
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$endDate - $approxEndText",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f), // Aligns text to the right
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "       Erstellt am $budgetDatum",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp
                )
            }
        }
    }
}