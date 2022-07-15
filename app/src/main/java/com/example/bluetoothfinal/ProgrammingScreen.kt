package com.example.bluetoothfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.bluetoothfinal.databinding.ActivityProgrammingScreenBinding
import java.util.*


class ProgrammingScreen : AppCompatActivity() {

    lateinit var binding: ActivityProgrammingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READ -> {
                        val arduinoMsg: String = msg.obj.toString() // Read message from Arduino
                        when (arduinoMsg.lowercase(Locale.getDefault())) {
                            "led is turned on" -> {
                                imageView.setBackgroundColor(
                                    ContextCompat.getColor(this@MainActivity,
                                        R.color.purple_200
                                    ))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                            "led is turned off" -> {
                                imageView.setBackgroundColor(
                                    ContextCompat.getColor(this@MainActivity,
                                        R.color.teal_700
                                    ))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                            else -> {
                                imageView.setBackgroundColor(
                                    ContextCompat.getColor(this@MainActivity,
                                        R.color.teal_200
                                    ))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                        }
                    }
                }
            }
        }


        binding.loadMotorFwdButton.setOnTouchListener(object : View.OnTouchListener {
            private var mHandler: Handler? = null
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (mHandler != null) return true
                        mHandler = Handler()
                        binding.loadMotorFwdButton.performClick()
                        binding.commandSentTextView.text = "Moving load motor - 11"
                        mHandler!!.postDelayed(mAction, 500)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mHandler == null) return true
                        mHandler!!.removeCallbacks(mAction)
                        mHandler = null
                        binding.commandSentTextView.text = "Load motor stopped - 12"
                    }
                }
                return false
            }

            var mAction: Runnable = object : Runnable {
                override fun run() {
                    println("Performing action...")
                    mHandler!!.postDelayed(this, 500)
                }
            }
        })


    }
}
