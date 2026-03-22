package com.lucio.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucio.expensetracker.ui.add_expense.AddExpenseScreen
import com.lucio.expensetracker.ui.add_expense.AddExpenseViewModel
import com.lucio.expensetracker.ui.add_expense.AddExpenseViewModelFactory
import com.lucio.expensetracker.ui.dashboard.DashboardScreen
import com.lucio.expensetracker.ui.dashboard.DashboardViewModel
import com.lucio.expensetracker.ui.dashboard.DashboardViewModelFactory
import com.lucio.expensetracker.ui.missions.MissionsScreen
import com.lucio.expensetracker.ui.missions.MissionsViewModel
import com.lucio.expensetracker.ui.missions.MissionsViewModelFactory
import com.lucio.expensetracker.ui.navigation.Screen
import com.lucio.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as ExpenseTrackerApp
        val expenseRepository = app.expenseRepository
        val missionRepository = app.missionRepository

        setContent {
            ExpenseTrackerTheme {
                val navController = rememberNavController()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Dashboard.route) {
                            val viewModel: DashboardViewModel = viewModel(
                                factory = DashboardViewModelFactory(expenseRepository, missionRepository)
                            )
                            DashboardScreen(
                                viewModel = viewModel,
                                onAddExpenseClick = {
                                    navController.navigate(Screen.AddExpense.route)
                                },
                                onMissionsClick = {
                                    navController.navigate(Screen.Missions.route)
                                }
                            )
                        }
                        composable(Screen.AddExpense.route) {
                            val viewModel: AddExpenseViewModel = viewModel(
                                factory = AddExpenseViewModelFactory(expenseRepository, missionRepository)
                            )
                            AddExpenseScreen(
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(Screen.Missions.route) {
                            val viewModel: MissionsViewModel = viewModel(
                                factory = MissionsViewModelFactory(missionRepository)
                            )
                            MissionsScreen(
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
