package com.example.connect.ViewModal

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FireBaseViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FireBaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FireBaseViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}