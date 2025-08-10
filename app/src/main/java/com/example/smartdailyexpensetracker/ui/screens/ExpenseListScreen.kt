package com.example.smartdailyexpensetracker.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.smartdailyexpensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    navToEntry: () -> Unit,
    navToReport: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(Date()) }
    var groupByCategory by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Date Picker
    val datePicker = remember {
        Calendar.getInstance().apply { time = selectedDate }
    }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            datePicker.set(year, month, day)
            selectedDate = datePicker.time
            viewModel.loadExpensesForDate(selectedDate.time)
        },
        datePicker.get(Calendar.YEAR),
        datePicker.get(Calendar.MONTH),
        datePicker.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense List") },
                actions = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Select Date")
                    }
                    IconButton(onClick = { groupByCategory = !groupByCategory }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Toggle Group")
                    }
                    IconButton(onClick = navToReport) {
                        Icon(Icons.Default.AccountBox, contentDescription = "Report")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navToEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date and totals
            Text(
                "Date: ${dateFormat.format(selectedDate)}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Total: ₹${ui.totalToday} • Count: ${ui.todayList.size}",
                style = MaterialTheme.typography.titleSmall
            )

            if (ui.todayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No expenses found for this date.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ui.todayList) { expense ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(expense.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "₹${expense.amount}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "Category: ${expense.category}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (!expense.notes.isNullOrBlank()) {
                                    Text(
                                        "Notes: ${expense.notes}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
