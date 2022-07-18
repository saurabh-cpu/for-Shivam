package com.example.bluetoothfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.bluetoothfinal.databinding.ActivityMassageScreenBinding
import java.util.*


class MassageScreen : AppCompatActivity() {

    lateinit var binding: ActivityMassageScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MachineState.MESSAGE_READ -> {
                        val arduinoMsg: String = msg.obj.toString()                 // Read message from Arduino
                        when (arduinoMsg.lowercase(Locale.getDefault())) {
                            MachineState.BOTH_MOTORS_ORIGIN_COMPLETE.toString() -> {
//                                binding.statusReceivedTextView.text = "Homing: Both motors at origin. Machine ready for massage."
//                                val intent = Intent(this@ProgrammingScreen, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
                            }
                        }
                    }
                }
            }
        }







    }
}