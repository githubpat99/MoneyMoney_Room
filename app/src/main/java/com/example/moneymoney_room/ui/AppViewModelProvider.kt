package com.example.moneymoney_room.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.ui.MonthlyDetails.MonthlyDetailsViewModel
import com.example.moneymoney_room.ui.budget.BudgetViewModel
import com.example.moneymoney_room.ui.budgetDetails.BudgetDetailsViewModel
import com.example.moneymoney_room.ui.budgetForm.BudgetFormViewModel
import com.example.moneymoney_room.ui.details.DetailsViewModel
import com.example.moneymoney_room.ui.entry.EntryViewModel
import com.example.moneymoney_room.ui.google.GooglePickerViewModel
import com.example.moneymoney_room.ui.home.HomeViewModel
import com.example.moneymoney_room.ui.list.ListViewModel
import com.example.moneymoney_room.ui.monthly.MonthlyViewModel
import com.example.moneymoney_room.ui.overview.OverviewViewModel
import com.example.moneymoney_room.ui.registration.RegistrationViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for ItemEntryViewModel
        initializer {
            EntryViewModel(moneymoneyApplication().container.itemsRepository
            )
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            DetailsViewModel(
                this.createSavedStateHandle(),
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication().container.configurationRepository
            )
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            BudgetDetailsViewModel(
                this.createSavedStateHandle(),
                moneymoneyApplication().container.budgetItemsRepository
            )
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            ListViewModel(
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication()
            )
        }

        // Initializer for MonthlyViewModel
        initializer {
            MonthlyViewModel(
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication().container.configurationRepository,
                moneymoneyApplication().container.budgetItemsRepository,
                moneymoneyApplication(),
                this.createSavedStateHandle()
            )
        }

        // Initializer for MonthlyDetailsViewModel
        initializer {
            MonthlyDetailsViewModel(
                this.createSavedStateHandle(),
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication().container.configurationRepository,
                moneymoneyApplication()
            )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication()
            )
        }

        // Initializer for RegistrationViewModel
        initializer {
            RegistrationViewModel(
                moneymoneyApplication().container.budgetItemsRepository,
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication())
        }

        // Initializer for OverviewViewModel
        initializer {
            OverviewViewModel(moneymoneyApplication())
        }

        // Initializer for BudgetFormViewModel
        initializer {
            BudgetFormViewModel(
                moneymoneyApplication().container.configurationRepository,
                moneymoneyApplication().container.budgetItemsRepository,
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication(),
                this.createSavedStateHandle()
            )
        }

        // Initializer for BudgetViewModel
        initializer {
            BudgetViewModel(
                moneymoneyApplication().container.itemsRepository
            )
        }

        // Initializer for GooglePickerViewModel
        initializer {
            GooglePickerViewModel(
                moneymoneyApplication().container.itemsRepository,
                moneymoneyApplication()
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.moneymoneyApplication(): MoneyMoneyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoneyMoneyApplication)
//    (this[ViewModelProvider.AndroidViewModelFactory.getInstance(application))
