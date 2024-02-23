package edu.uw.ischool.qnho.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask


const val ALARM_ACTION = "edu.uw.ischool.qnho.ALARM"

class MainActivity : AppCompatActivity() {
    lateinit var msg : EditText
    lateinit var num : EditText
    lateinit var delay : EditText
    lateinit var btn : Button

    var receiver : BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msg = findViewById<EditText>(R.id.message)
        num = findViewById<EditText>(R.id.num)
        delay = findViewById<EditText>(R.id.delay)
        btn = findViewById<Button>(R.id.btn)

        msg.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn.isEnabled = msg.text.isNotEmpty() && num.text.isNotEmpty() && (delay.text.isNotEmpty() && delay.text.toString().toInt() > 0)
            }
        })

        num.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn.isEnabled = msg.text.isNotEmpty() && num.text.isNotEmpty() && (delay.text.isNotEmpty() && delay.text.toString().toInt() > 0)
            }
        })

        delay.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn.isEnabled = msg.text.isNotEmpty() && num.text.isNotEmpty() && (delay.text.isNotEmpty() && delay.text.toString().toInt() > 0)
            }
        })

        btn.setOnClickListener {
            if(delay.text.toString().toInt() < 0) {
                Toast.makeText(this, "Invalid delay time. Please put a positive integer that is larger than 0.", Toast.LENGTH_SHORT).show()
            } else {
                if(btn.text == "Start"){
                    btn.text = "Stop"
                    startNagging()
                }else{
                    btn.text = "Start"
                    unregisterReceiver(receiver)
                    receiver = null
                }
            }

        }
    }

    private fun checkForSmsPermission() {
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission not granted!")
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 1)
            btn.isEnabled = false
        } else {
            btn.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (permissions[0].equals(android.Manifest.permission.SEND_SMS, ignoreCase = true)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                } else {
                    // Permission denied.
                    Log.d("MainActivity", "Failed to obtain permission")
                    Toast.makeText(
                        this,
                        "Failed to obtain permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Disable the message button.
                    btn.isEnabled = false
                }
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        // Check if the SEND_SMS permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the SEND_SMS permission
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 1)
        } else {
            val smsManager = SmsManager.getDefault()
            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }

    fun startNagging() {
        val activity = this
        val delayTime = delay.text.toString().toInt()

        Log.i("TEST", delayTime.toString())

        if(receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    Log.i("WHY", "calling")
                    sendSms(num.text.toString(), msg.text.toString())
                }
            }

            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)

            val intent = Intent(ALARM_ACTION)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (delayTime * 60000).toLong(), pendingIntent)
        }
    }
}