package com.example.moneymoney_room.ui.monthly

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.moneymoney_room.R


@Composable
    fun MonthlyLineChart() {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawLine(
                start = Offset(x = canvasWidth, y = 0f),
                end = Offset(x = 0f, y = canvasHeight),
                color = Color.Blue
            )
        }
    }

@Composable
fun PerformanceChart(modifier: Modifier = Modifier, list: List<Float> =
    listOf(4500f, 4650f, 5300f, 5550f, 6100f, 3000f, 3700f, 3850f, 4500f, 4650f, 5200f, 5350f, 3200f)
) {
    val zipList: List<Pair<Float, Float>> = list.zipWithNext()

    Row(modifier = modifier) {
        val max = list.max()
        val min = list.min()

        val lineColor =
            if (list.last() > list.first())
                colorResource(id = R.color.green)
            else colorResource(id = R.color.dark_red) // <-- Line color is Green if its going up and Red otherwise

        for (pair in zipList) {

            val fromValuePercentage = getValuePercentageForRange(pair.first, max, min)
            val toValuePercentage = getValuePercentageForRange(pair.second, max, min)

            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onDraw = {
                    val fromPoint = Offset(x = 0f, y = size.height.times(1 - fromValuePercentage)) // <-- Use times so it works for any available space
                    val toPoint =
                        Offset(x = size.width, y = size.height.times(1 - toValuePercentage)) // <-- Also here!

                    drawLine(
                        color = lineColor,
                        start = fromPoint,
                        end = toPoint,
                        strokeWidth = 5f
                    )
                })
        }
    }
}

private fun getValuePercentageForRange(value: Float, max: Float, min: Float) =
    (value - min) / (max - min)

@Composable
fun StackedBar(modifier: Modifier, slices: List<Slice>) {

    val textMeasurer = rememberTextMeasurer()
    val textColor = colorResource(id = R.color.light_gray)

    val textLayoutResults = remember {
        mutableListOf<TextLayoutResult>().apply {
            slices.forEach {
                val textLayoutResult: TextLayoutResult =
                    textMeasurer.measure(
                        text = AnnotatedString(it.text),
                        style = TextStyle(
                            color = textColor,
                            textAlign = TextAlign.Left,
                            fontSize = 16.sp
                        )
                    )
                add(textLayoutResult)
            }
        }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height - 8  // reduces the full Row Size a bit

        var currentX = 0f
        slices.forEachIndexed { index: Int, slice: Slice ->
            val width = (slice.value) / 100f * canvasWidth

            // Draw Rectangles
            drawRect(
                color = slice.color, topLeft = Offset(currentX, 0f), size = Size(
                    width,
                    canvasHeight
                )
            )

            // Draw Text
            val textSize = textLayoutResults[index].size
            val style = textLayoutResults[index].layoutInput.style
            drawText(
                textMeasurer = textMeasurer,
                    text = slice.text,
                    topLeft = Offset(
                        x = currentX + 12,  // was 12
                        y = (canvasHeight - textSize.height) / 2
                    ),
                    style = style
            )

            // Update start position of next rectangle
            currentX += width
        }
    }
}
data class Slice(val value: Float, val color: Color, val text: String) {
}