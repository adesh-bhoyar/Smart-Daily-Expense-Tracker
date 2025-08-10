package com.example.smartdailyexpensetracker

import ExpenseRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.smartdailyexpensetracker.ui.screens.ExpenseEntryScreen
import com.example.smartdailyexpensetracker.ui.screens.ExpenseListScreen
import com.example.smartdailyexpensetracker.ui.screens.ExpenseReportScreen
import com.example.smartdailyexpensetracker.ui.theme.SmartDailyExpenseTrackerTheme
import com.example.smartdailyexpensetracker.viewmodel.ExpenseViewModel
import com.example.smartdailyexpensetracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {

    lateinit var viewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()

        viewModel = ExpenseViewModelFactory(
            ExpenseRepository(db.expenseDao())
        ).create(ExpenseViewModel::class.java)


        setContent {
            SmartDailyExpenseTrackerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ExpenseNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ExpenseNavGraph(viewModel: ExpenseViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            ExpenseListScreen(
                viewModel = viewModel,
                navToEntry = { navController.navigate("entry") },
                navToReport = { navController.navigate("report") },
            )
        }
        composable("entry") {
            ExpenseEntryScreen(
                viewModel = viewModel,
                navBack = { navController.popBackStack() }
            )
        }
        composable("report") {
            ExpenseReportScreen(
                viewModel = viewModel,
                navBack = { navController.popBackStack() }
            )
        }
    }
}
