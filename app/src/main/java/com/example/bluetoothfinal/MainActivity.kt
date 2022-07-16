package com.example.bluetoothfinal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.bluetoothfinal.Helpers.Companion.CONNECTING_STATUS
import com.example.bluetoothfinal.Helpers.Companion.MESSAGE_READ
import com.example.bluetoothfinal.R.color.*
import com.google.android.material.button.MaterialButton
import java.util.*


var newNumber = 0

var mmSocket: BluetoothSocket? = null
var handler: Handler? = null
private val deviceName: String? = null
private var deviceAddress: String? = null

var connectedThread:ConnectedThread? = null
var createConnectThread:CreateConnectThread? = null




class MainActivity : AppCompatActivity() {


    lateinit var buttonConnect:Button
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var textViewInfo:TextView
    lateinit var buttonToggle: Button
    lateinit var imageView: ImageView
    lateinit var buttonPressed: Button

    lateinit var homingButton: MaterialButton
    lateinit var programmingButton: MaterialButton
    lateinit var massageButton: MaterialButton



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonConnect = findViewById(R.id.buttonConnect)
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
//        textViewInfo = findViewById(R.id.textViewInfo)
//        buttonToggle = findViewById(R.id.buttonToggle)
//        imageView = findViewById(R.id.imageView)
//        buttonPressed = findViewById(R.id.buttonPressed)

        homingButton = findViewById(R.id.startHomingButton)
        programmingButton = findViewById(R.id.startProgrammingButton)
        massageButton = findViewById(R.id.startMassageButton)




        progressBar.visibility = View.GONE
//        buttonToggle.isEnabled = false
//        imageView.setBackgroundColor(ContextCompat.getColor(this,purple_200))

        // If a bluetooth device has been selected from SelectDeviceActivity
        val deviceName = intent.getStringExtra("deviceName")

        if (deviceName != null) {
            // Get the device address to make BT Connection
            deviceAddress = intent.getStringExtra("deviceAddress")
            // Show progress and connection status
            toolbar.subtitle = "Connecting to $deviceName..."
            progressBar.visibility = View.VISIBLE
            buttonConnect.isEnabled = false

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            createConnectThread = CreateConnectThread(bluetoothAdapter, deviceAddress)
            createConnectThread!!.start()
        }


        /*
        Second most important piece of Code. GUI Handler
         */

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> {
                            toolbar.subtitle = "Connected to $deviceName"
                            progressBar.visibility = View.GONE
                            buttonConnect.isEnabled = true
                            buttonToggle.isEnabled = true
                        }
                        -1 -> {
                            toolbar.subtitle = "Device fails to connect"
                            progressBar.visibility = View.GONE
                            buttonConnect.isEnabled = true
                        }
                    }
                    MESSAGE_READ -> {
                        val arduinoMsg: String = msg.obj.toString() // Read message from Arduino
                        when (arduinoMsg.lowercase(Locale.getDefault())) {
                            "led is turned on" -> {
                                imageView.setBackgroundColor(ContextCompat.getColor(this@MainActivity,purple_200))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                            "led is turned off" -> {
                                imageView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, teal_700))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                            else -> {
                                imageView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, teal_200))
                                textViewInfo.text = "Arduino Message : $arduinoMsg"
                            }
                        }
                    }
                }
            }
        }

        // Select Bluetooth Device
        buttonConnect.setOnClickListener { // Move z to adapter list
            val intent = Intent(this@MainActivity, SelectDeviceActivity::class.java)
            startActivity(intent)
        }


        buttonToggle.setOnClickListener {
            var cmdText:String = ""
            when (buttonToggle.text.toString().lowercase(Locale.getDefault())) {
                "turn on" -> {
                    buttonToggle.text = "Turn Off"
                    // Command to turn on LED on Arduino. Must match with the command in Arduino code
                    cmdText = "1/"
                }
                "turn off" -> {
                    buttonToggle.text = "Turn On"
                    // Command to turn off LED on Arduino. Must match with the command in Arduino code
                    cmdText = "2/"
                }
            }
            // Send command to Arduino board
            connectedThread?.write(cmdText)
        }

        val number = 0
//        buttonPressed.setOnLongClickListener {
//            val handler = Handler(Looper.myLooper()!!)
//            val runnable : Runnable = object : Runnable {
//
//                override fun run() {
//                    handler.removeCallbacks(this)
//                    if (buttonPressed.isPressed) {
//                        val newNumber= number + 1
//                        textViewInfo.text = "$newNumber Items"
//                        handler.postDelayed(this, 100)
//                    }
//                }
//            }
//            handler.postDelayed(runnable,0)
//            true
//        }



//        buttonPressed.setOnTouchListener(RepeatListener(400, 100, object : View.OnClickListener {
//            override fun onClick(view: View?) {
//                // the code to execute repeatedly
//
//            }
//        }))


        buttonPressed.setOnTouchListener(object : OnTouchListener {
            private var mHandler: Handler? = null
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (mHandler != null) return true
                        mHandler = Handler()
                        buttonPressed.performClick()
                        newNumber += 1
                        textViewInfo.text = "$newNumber Items"
                        mHandler!!.postDelayed(mAction, 500)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mHandler == null) return true
                        mHandler!!.removeCallbacks(mAction)
                        mHandler = null
                        newNumber -= 3
                        textViewInfo.text = "$newNumber Items"
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