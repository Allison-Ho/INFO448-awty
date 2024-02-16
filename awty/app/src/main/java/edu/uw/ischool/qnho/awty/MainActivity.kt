package edu.uw.ischool.qnho.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.getSystemService

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

    fun startNagging() {
        val activity = this
        val delayTime = delay.text.toString().toInt()

        Log.i("TEST", delayTime.toString())

        if(receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    Log.i("WHY", "calling")
                    Toast.makeText(activity, "(425) 555-1212: Are we there yet?", Toast.LENGTH_SHORT).show()
                    Toast.makeText(activity, "Texting ${num.text} : ${msg.text}", Toast.LENGTH_SHORT).show()
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