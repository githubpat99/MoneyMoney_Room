package com.example.moneymoney_room.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.ui.budget.BudgetViewModel
import com.example.moneymoney_room.ui.details.DetailsViewModel
import com.example.moneymoney_room.ui.entry.EntryViewModel
import com.example.moneymoney_room.ui.google.GooglePickerViewModel
import com.example.moneymoney_room.ui.home.HomeViewModel
import com.example.moneymoney_room.ui.list.ListViewModel
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
                moneymoneyApplication().container.itemsRepository
            )
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            ListViewModel(
                moneymoneyApplication().container.itemsRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                moneymoneyApplication().container.itemsRepository
            )
        }

        // Initializer for RegistrationViewModel
        initializer {
            RegistrationViewModel()
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
                moneymoneyApplication().container.itemsRepository
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
