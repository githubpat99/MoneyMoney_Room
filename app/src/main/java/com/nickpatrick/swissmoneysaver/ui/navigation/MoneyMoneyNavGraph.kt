package com.nickpatrick.swissmoneysaver.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nickpatrick.swissmoneysaver.ui.MonthlyDetails.MonthlyDetailDestination
import com.nickpatrick.swissmoneysaver.ui.MonthlyDetails.MonthlyDetailsScreen
import com.nickpatrick.swissmoneysaver.ui.budget.BudgetDestination
import com.nickpatrick.swissmoneysaver.ui.budget.BudgetScreen
import com.nickpatrick.swissmoneysaver.ui.budgetDetails.BudgetDetailsDestination
import com.nickpatrick.swissmoneysaver.ui.budgetDetails.BudgetDetailsScreen
import com.nickpatrick.swissmoneysaver.ui.budgetForm.BudgetFormDestination
import com.nickpatrick.swissmoneysaver.ui.budgetForm.BudgetFormScreen

import com.nickpatrick.swissmoneysaver.ui.details.DetailsDestination
import com.nickpatrick.swissmoneysaver.ui.details.DetailsScreen
import com.nickpatrick.swissmoneysaver.ui.entry.EntryDestination
import com.nickpatrick.swissmoneysaver.ui.entry.EntryScreen
import com.nickpatrick.swissmoneysaver.ui.google.GoogleDestination
import com.nickpatrick.swissmoneysaver.ui.google.GooglePickerScreen
import com.nickpatrick.swissmoneysaver.ui.home.HomeDestination
import com.nickpatrick.swissmoneysaver.ui.home.HomeScreen
import com.nickpatrick.swissmoneysaver.ui.list.ListDestination
import com.nickpatrick.swissmoneysaver.ui.list.ListScreen
import com.nickpatrick.swissmoneysaver.ui.monthly.MonthlyDestination
import com.nickpatrick.swissmoneysaver.ui.monthly.MonthlyDestination.year
import com.nickpatrick.swissmoneysaver.ui.monthly.MonthlyScreen
import com.nickpatrick.swissmoneysaver.ui.overview.OverviewDestination
import com.nickpatrick.swissmoneysaver.ui.overview.OverviewScreen
import com.nickpatrick.swissmoneysaver.ui.registration.RegistrationDestination
import com.nickpatrick.swissmoneysaver.ui.registration.RegistrationScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MoneyMoneyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToBudgetForm = { year, tab ->
                    val route = "${BudgetFormDestination.route}/$year/$tab"
                    navController.navigate(route)
                },
                navigateToRegistration = { navController.navigate(RegistrationDestination.route) },
                navigateToMonthly = { year ->
                    val route = "${MonthlyDestination.route}/$year"
                    navController.navigate(route)
                },
                navigateToBudget = { navController.navigate(BudgetDestination.route) },
                navigateToGooglePicker = { navController.navigate(GoogleDestination.route) }
            )
        }
        composable(route = ListDestination.route) {
            ListScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToDetail = {
                    val route = "${DetailsDestination.route}/${it}"
                    navController.navigate(route)
                },
                navigateToEntry = { navController.navigate(EntryDestination.route) }
            )
        }
        composable(
            route = MonthlyDestination.routeWithArgs,
            arguments = listOf(
                navArgument(BudgetFormDestination.year) {
                    type = NavType.StringType
                },
            )
        ) {

            MonthlyScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                year = year,
                navigateToMonthlyDetail = { month, year, endSaldo, monthlyTotal ->

                    val route =
                        "${MonthlyDetailDestination.route}/$month/$year/$endSaldo/$monthlyTotal"
                    println("MonthlyScreen - navigateToMonthlyDetail - route = $route")

                    navController.navigate(route)
                },
                navigateToEntry = { navController.navigate(EntryDestination.route) }
            )
        }
        composable(
            route = MonthlyDetailDestination.routeWithArgs,
            arguments = listOf(
                navArgument(MonthlyDetailDestination.Month) {
                    type = NavType.StringType
                },
                navArgument(MonthlyDetailDestination.Year) {
                    type = NavType.StringType
                },
                navArgument(MonthlyDetailDestination.EndSaldo) {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            val month = backStackEntry.arguments?.getString(MonthlyDetailDestination.Month) ?: ""
            val year = backStackEntry.arguments?.getString(MonthlyDetailDestination.Year) ?: ""
            var endSaldo =
                backStackEntry.arguments?.getString(MonthlyDetailDestination.EndSaldo) ?: 0.0
            var monthlyTotal =
                backStackEntry.arguments?.getString(MonthlyDetailDestination.MonthlyTotal) ?: 0.0

            endSaldo = endSaldo.toString().toDouble()
            monthlyTotal = monthlyTotal.toString().toDouble()

            MonthlyDetailsScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                month = month,
                year = year,
                endSaldo = endSaldo,
                monthlyTotal = monthlyTotal,
                navigateToDetail = { itemId ->
                    val route = "${DetailsDestination.route}/$itemId"
                    navController.navigate(route)
                },
                navigateToEntry = { navController.navigate(EntryDestination.route) }
            )
        }

        composable(
            route = DetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(DetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            DetailsScreen(
                navigateBack = { navController.navigateUp() },
                navigateToEntry = { navController.navigate(EntryDestination.route) }
            )
        }
        composable(
            route = BudgetDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(DetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            BudgetDetailsScreen(
                navigateToBudgetForm = { year, tab ->
                    val route = "${BudgetFormDestination.route}/$year/$tab"
                    navController.navigate(route)
                },
                navigateToEntry = { navController.navigate(EntryDestination.route) }
            )
        }
        composable(route = EntryDestination.route) {
            EntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToList = { navController.navigate(ListDestination.route) }
            )
        }
        composable(route = RegistrationDestination.route) {
            RegistrationScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = OverviewDestination.route) {
            OverviewScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = BudgetFormDestination.routeWithArgs,
            arguments = listOf(
                navArgument(BudgetFormDestination.year) {
                    type = NavType.StringType
                },
                navArgument(BudgetFormDestination.tab) {
                    type = androidx.navigation.NavType.StringType
                },
            )
        ) {
            BudgetFormScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigate(HomeDestination.route) },
                navigateToBudgetDetails = {
                    val route = "${BudgetDetailsDestination.route}/${it}"
                    navController.navigate(route)
                },
                navigateToOverview = { navController.navigate(OverviewDestination.route) },
            )
        }
        composable(route = BudgetDestination.route) {
            BudgetScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigate(HomeDestination.route) },
                navigateToList = { navController.navigate(ListDestination.route) },
                navigateToBudget = { navController.navigate(BudgetDestination.route) }
            )
        }
        composable(route = GoogleDestination.route) {
            GooglePickerScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigate(HomeDestination.route) },
                navigateToList = { navController.navigate(ListDestination.route) },
                navigateToBudget = { navController.navigate(BudgetDestination.route) }
            )
        }
    }
}
