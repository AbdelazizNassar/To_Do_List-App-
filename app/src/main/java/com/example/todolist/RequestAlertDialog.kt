package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object RequestAlertDialog {
    fun showAlertDialog(
        context: Context,
        message: String,
        action: String,
        onSwitchChecked: (Boolean) -> Unit // Lambda to handle switch state changes
    ) {
        AlertDialog.Builder(context)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Allow") { dialog, _ ->
                when {
                    action == Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM -> {
                        val intent = Intent(action)
                        context.startActivity(intent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        val intent = Intent(action).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            val uri = Uri.fromParts("package", context.packageName, null)
                            data = uri
                        }
                        context.startActivity(intent)
                    }
                }
                onSwitchChecked(false) // Set switch state to false on positive action
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                onSwitchChecked(false) // Set switch state to false on cancel
                dialog.dismiss()
            }
            .create()
            .show()
    }
}