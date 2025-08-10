package com.example.smartdailyexpensetracker.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.smartdailyexpensetracker.viewmodel.ExpenseViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseViewModel,
    navBack: () -> Unit
) {
    val context = LocalContext.current
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Report") },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    DropdownMenuSample(context, dailyTotals, categoryTotals)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Last 7 Days - Line Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Last 7 Days Totals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        SimpleBarChart(dailyTotals)
                    }
                }
            }

            // Category Totals List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Category Totals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        categoryTotals.forEach { (category, total) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(category)
                                Text("₹$total", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(data: List<Pair<String, Double>>) {
    if (data.isEmpty()) {
        Text("No data available", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val maxVal = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)
    val barColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val labelPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 30f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(8.dp)
    ) {
        val barWidth = size.width / (data.size * 2)
        val heightScale = size.height / maxVal.toFloat()
        val spacingX = size.width / (data.size * 2f) // space between bars

        // Draw grid lines
        for (i in 0..5) {
            val y = size.height * (i / 5f)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // --- Draw bars ---
        data.forEachIndexed { index, entry ->
            val x = index * spacingX * 2 + spacingX / 2
            val barHeight = (entry.second.toFloat() * heightScale).coerceAtMost(size.height)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // X-axis label
            drawContext.canvas.nativeCanvas.drawText(
                entry.first,
                x + barWidth / 2,
                size.height + 30f,
                labelPaint
            )
        }
    }
}


@Composable
fun DropdownMenuSample(
    context: Context,
    dailyTotals: List<Pair<String, Double>>,
    categoryTotals: List<Pair<String, Double>>
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("Export")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Export as PDF") },
                onClick = {
                    expanded = false
                    exportReportAsPDF(context, dailyTotals, categoryTotals)
                }
            )
            DropdownMenuItem(
                text = { Text("Export as CSV") },
                onClick = {
                    expanded = false
                    exportReportAsCSV(context, dailyTotals, categoryTotals)
                }
            )
        }
    }
}

fun exportReportAsCSV(
    context: Context,
    dailyTotals: List<Pair<String, Double>>,
    categoryTotals: List<Pair<String, Double>>
) {
    val builder = StringBuilder()
    builder.append("Daily Totals\nDate,Total\n")
    dailyTotals.forEach { (date, total) ->
        builder.append("$date,$total\n")
    }

    builder.append("\nCategory Totals\nCategory,Total\n")
    categoryTotals.forEach { (cat, total) ->
        builder.append("$cat,$total\n")
    }

    val file = File(context.cacheDir, "ExpenseReport.csv")
    FileOutputStream(file).use {
        it.write(builder.toString().toByteArray())
    }

    shareFile(context, file, "text/csv")
}

fun exportReportAsPDF(
    context: Context,
    dailyTotals: List<Pair<String, Double>>,
    categoryTotals: List<Pair<String, Double>>
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = android.graphics.Paint()

    var y = 25
    paint.textSize = 14f
    canvas.drawText("Expense Report", 80f, y.toFloat(), paint)
    y += 20

    paint.textSize = 10f
    canvas.drawText("Daily Totals:", 10f, y.toFloat(), paint)
    y += 15
    dailyTotals.forEach { (date, total) ->
        canvas.drawText("$date : ₹$total", 10f, y.toFloat(), paint)
        y += 15
    }

    y += 10
    canvas.drawText("Category Totals:", 10f, y.toFloat(), paint)
    y += 15
    categoryTotals.forEach { (cat, total) ->
        canvas.drawText("$cat : ₹$total", 10f, y.toFloat(), paint)
        y += 15
    }

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "ExpenseReport.pdf")
    FileOutputStream(file).use {
        pdfDocument.writeTo(it)
    }
    pdfDocument.close()

    shareFile(context, file, "application/pdf")
}

fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Report"))
}