package com.example.broadcastreciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ChargingReceiver : BroadcastReceiver() {
    var onChargingChange: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> onChargingChange?.invoke(true)
            Intent.ACTION_POWER_DISCONNECTED -> onChargingChange?.invoke(false)
        }
    }
}

