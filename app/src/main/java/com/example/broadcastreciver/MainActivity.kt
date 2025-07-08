package com.example.broadcastreciver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var txtInternet: TextView
    private lateinit var txtChanging: TextView
    private lateinit var txtVolume: TextView
    private lateinit var txtBrightness: TextView
    private lateinit var progressVolumee: ProgressBar
    private lateinit var progressBrightnesss: ProgressBar

    private val networkRecever = NetworkReceiver()
    private val chargingReceiver = ChargingReceiver()
    private lateinit var volumeReceiver: VolumeReceiver


    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }


        txtInternet = findViewById(R.id.txtInternet)
        txtChanging = findViewById(R.id.txtCharging)
        txtVolume = findViewById(R.id.txtVolume)
        txtBrightness = findViewById(R.id.txtBrightness)
        progressVolumee = findViewById(R.id.progressVolume)
        progressBrightnesss = findViewById(R.id.progressBrightness)

        volumeReceiver = VolumeReceiver { newVolume ->
            txtVolume.text = "Volume:$newVolume"
            progressVolumee.progress = newVolume

        }
        findViewById<Button>(R.id.btnCheckBrightness).setOnClickListener {
            val brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1)
            txtBrightness.text = "Brightness: $brightness"
            progressBrightnesss.max = 255
            progressBrightnesss.progress = brightness

        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        progressVolumee.progress = vol
        txtVolume.text = "Volume:$vol"

        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        txtChanging.text = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging: Yes 🔌"
            else -> "Charging: No ❌"
        }

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkRecever, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        registerReceiver(chargingReceiver, IntentFilter(Intent.ACTION_POWER_CONNECTED))
        registerReceiver(chargingReceiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED))
        registerReceiver(volumeReceiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
        networkRecever.onStatusChange = { connected ->
            txtInternet.text =
                if (connected) "Internet: Connected ✅" else "Internet: Not Connected ❌"
        }

        chargingReceiver.onChargingChange = { isCharging ->
            txtChanging.text = "Charging: ${if (isCharging) "Yes 🔌" else "No ❌"}"
        }
    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkRecever)
        unregisterReceiver(chargingReceiver)
        unregisterReceiver(volumeReceiver)
    }

}