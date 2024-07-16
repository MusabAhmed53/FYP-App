package com.example.prototype

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.prototype.R

class LoadingScreen(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun startAlertDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.logout_alert, null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog?.show()
    }

    fun closeAlertDialog() {
        dialog?.dismiss()
    }
}