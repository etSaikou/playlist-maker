package com.saikou.playlistmaker.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

open class BaseViewModel<T>(private val t: T) : ViewModel() {


    protected fun getFactory(t: T): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            //Продумать наследование и создать родительский интерактор
            BaseViewModel(t)

        }
    }

}