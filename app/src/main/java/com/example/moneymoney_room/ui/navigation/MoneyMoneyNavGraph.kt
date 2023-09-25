package com.example.moneymoney_room.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneymoney_room.ui.budget.BudgetDestination
import com.example.moneymoney_room.ui.budget.BudgetScreen
import com.example.moneymoney_room.ui.details.DetailsDestination
import com.example.moneymoney_room.ui.details.DetailsScreen
import com.example.moneymoney_room.ui.entry.EntryDestination
import com.example.moneymoney_room.ui.entry.EntryScreen
import com.example.moneymoney_room.ui.home.HomeDestination
import com.example.moneymoney_room.ui.home.HomeScreen
import com.example.moneymoney_room.ui.list.ListDestination
import com.example.moneymoney_room.ui.list.ListScreen
import com.example.moneymoney_room.ui.registration.RegistrationDestination
import com.example.moneymoney_room.ui.registration.RegistrationScreen

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
                navigateToList = { navController.navigate(ListDestination.route) },
                navigateToRegistration = { navController.navigate(RegistrationDestination.route)},
                navigateToBudget = { navController.navigate(BudgetDestination.route)}
            )
        }
        composable(route = ListDestination.route) {
            ListScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToDetail = {
                    navController.navigate("${DetailsDestination.route}/${it}")
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
                navigateToEntry = { navController.navigate(EntryDestination.route)}
            )
        }
        composable(route = EntryDestination.route) {
            EntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToList = { navController.navigate( ListDestination.route)}
            )
        }
        composable(route = RegistrationDestination.route) {
            RegistrationScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = BudgetDestination.route) {
            BudgetScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToList = { navController.navigate( ListDestination.route)}
            )
        }
    }
}
