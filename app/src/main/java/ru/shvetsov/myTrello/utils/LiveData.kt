package ru.shvetsov.myTrello.utils

import androidx.lifecycle.MutableLiveData

/**
 * Created by Alexander Shvetsov on 12.11.2019
 */

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()

    if (defaultValue != null) {
        data.value = defaultValue
    }
    return data
}