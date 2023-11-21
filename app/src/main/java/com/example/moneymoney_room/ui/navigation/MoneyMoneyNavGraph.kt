package com.example.moneymoney_room.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneymoney_room.ui.MonthlyDetails.MonthlyDetailDestination
import com.example.moneymoney_room.ui.MonthlyDetails.MonthlyDetailsScreen
import com.example.moneymoney_room.ui.budget.BudgetDestination
import com.example.moneymoney_room.ui.budget.BudgetScreen
import com.example.moneymoney_room.ui.budgetDetails.BudgetDetailsDestination
import com.example.moneymoney_room.ui.budgetDetails.BudgetDetailsScreen
import com.example.moneymoney_room.ui.budgetForm.BudgetFormDestination
import com.example.moneymoney_room.ui.budgetForm.BudgetFormScreen

import com.example.moneymoney_room.ui.details.DetailsDestination
import com.example.moneymoney_room.ui.details.DetailsScreen
import com.example.moneymoney_room.ui.entry.EntryDestination
import com.example.moneymoney_room.ui.entry.EntryScreen
import com.example.moneymoney_room.ui.google.GoogleDestination
import com.example.moneymoney_room.ui.google.GooglePickerScreen
import com.example.moneymoney_room.ui.home.HomeDestination
import com.example.moneymoney_room.ui.home.HomeScreen
import com.example.moneymoney_room.ui.list.ListDestination
import com.example.moneymoney_room.ui.list.ListScreen
import com.example.moneymoney_room.ui.monthly.MonthlyDestination
import com.example.moneymoney_room.ui.monthly.MonthlyScreen
import com.example.moneymoney_room.ui.registration.RegistrationDestination
import com.example.moneymoney_room.ui.registration.RegistrationScreen

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
                navigateToBudgetForm = {
                    val route = "${BudgetFormDestination.route}/${it}"
                    navController.navigate(route)
                },
                navigateToRegistration = { navController.navigate(RegistrationDestination.route) },
                navigateToMonthly = { navController.navigate(MonthlyDestination.route) },
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
            route = MonthlyDestination.route
        ) {

            MonthlyScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
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
                navigateToBudgetForm = {
                    val route = "${BudgetFormDestination.route}/${it}"
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
        composable(
            route = BudgetFormDestination.routeWithArgs,
            arguments = listOf(
                navArgument(BudgetFormDestination.itemIdArg) {
                    type = NavType.IntType
                })
        ) {
            BudgetFormScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigate(HomeDestination.route) },
                navigateToBudgetDetails = {
                    val route = "${BudgetDetailsDestination.route}/${it}"
                    navController.navigate(route)
                },
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
