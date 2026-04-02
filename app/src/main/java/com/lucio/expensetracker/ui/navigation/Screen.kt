package com.lucio.expensetracker.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddExpense : Screen("add_expense")
    object Missions : Screen("missions")
    object MissionDetails : Screen("mission_details/{missionId}") {
        fun createRoute(missionId: Long) = "mission_details/$missionId"
    }
}
