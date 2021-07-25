package com.musicbrainz.android.extensions

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.musicbrainz.android.network.Resource


fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}


fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackBar(message: String, action: (() -> Unit)? = null) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
    action?.let {
        snackBar.setAction("Retry") {
            it()
        }
    }
    snackBar.show()
}


fun AppCompatActivity.handleApiError(
    view: View,
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> view.snackBar(
            "Please check your internet connection",
            retry
        )
        failure.errorMessage != null -> {
            view.snackBar(failure.errorMessage, retry)

        }
        else -> {
            val error = failure.errorBody?.string().toString()
            view.snackBar(error, retry)
        }
    }
}

fun AppCompatActivity.showToast(message: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val toast = Toast(this)
        toast.setText(message)
        toast.show()
    } else {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


fun AppCompatActivity.hideKeyBoard(view: View) {

    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

}


