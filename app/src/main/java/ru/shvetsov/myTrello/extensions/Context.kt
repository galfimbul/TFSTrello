package ru.shvetsov.myTrello.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Created by Alexander Shvetsov on 10.11.2019
 */

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.dpToPx(dp: Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}

fun Context.spToPx(sp: Int): Float {
    return sp.toFloat() * this.resources.displayMetrics.scaledDensity
}

fun Fragment.showError(errorStringId: Int) {
    activity?.showError(errorStringId)
}

fun Activity.showError(errorStringId: Int) {
    applicationContext.showError(errorStringId)
}

fun Context.showError(errorStringId: Int) {
    Toast.makeText(this, getString(errorStringId), Toast.LENGTH_SHORT).show()
}