package com.example.bluetoothfinal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.telephony.DisconnectCause
import android.util.Log

import android.view.View

import android.widget.Button

import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import com.example.bluetoothfinal.MachineState.Companion.BOTH_MOTORS_ORIGIN_COMPLETE
import com.example.bluetoothfinal.MachineState.Companion.CONNECTING_STATUS
import com.example.bluetoothfinal.MachineState.Companion.MACHINE_ONLINE
import com.example.bluetoothfinal.MachineState.Companion.MACHINE_READY_FOR_MASSAGE
import com.example.bluetoothfinal.MachineState.Companion.MANUAL_PROGRAMMING_COMPLETE
import com.example.bluetoothfinal.MachineState.Companion.MANUAL_PROGRAMMING_START
import com.example.bluetoothfinal.MachineState.Companion.MASSAGE_COMPLETE
import com.example.bluetoothfinal.MachineState.Companion.MASSAGE_CYCLE_START_MANUAL_POSITIONS
import com.example.bluetoothfinal.MachineState.Companion.MESSAGE_READ
import com.example.bluetoothfinal.MachineState.Companion.PREPARE_MACHINE

import com.google.android.material.button.MaterialButton
import java.util.*




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
    lateinit var buttonDisconnect: Button


    lateinit var homingButton: MaterialButton
    lateinit var programmingButton: MaterialButton
    lateinit var massageButton: MaterialButton
    lateinit var machineStatusTextView: TextView



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonConnect = findViewById(R.id.buttonConnect)
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        machineStatusTextView = findViewById(R.id.machineStatusTextView)
        homingButton = findViewById(R.id.startHomingButton)
        programmingButton = findViewById(R.id.startProgrammingButton)
        massageButton = findViewById(R.id.startMassageButton)
        buttonDisconnect = findViewById(R.id.buttonDisconnect)

        progressBar.visibility = View.GONE


        val deviceName = intent.getStringExtra("deviceName")                    // If a bluetooth device has been selected from SelectDeviceActivity
        if (deviceName != null) {
            deviceAddress =
                intent.getStringExtra("deviceAddress")                // Get the device address to make BT Connection
            toolbar.subtitle =
                "Connecting to $deviceName..."                           // Show progress and connection status
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

        val machineReadyStatus = intent.getStringExtra("machineState")
        if (machineReadyStatus != null){
            toolbar.subtitle = "Connected to $deviceName"
            progressBar.visibility = View.GONE
            homingButton.isEnabled = true
            programmingButton.isEnabled = true
            massageButton.isEnabled = true
            buttonConnect.visibility = View.GONE
            buttonDisconnect.visibility = View.VISIBLE
        }

        //val positionData = intent.getSerializableExtra("positionData")


        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> when (msg.arg1) {
                        1 -> {
                            toolbar.subtitle = "Connected to $deviceName"
                            progressBar.visibility = View.GONE
                            //buttonConnect.isEnabled = false
                            homingButton.isEnabled = true
                            //buttonToggle.isEnabled = true
                            buttonConnect.visibility = View.GONE
                            buttonDisconnect.visibility = View.VISIBLE
                            connectedThread!!.write(MACHINE_ONLINE.toString())
                        }
                        -1 -> {
                            toolbar.subtitle = "Device fails to connect"
                            progressBar.visibility = View.GONE
                            buttonConnect.isEnabled = true
                        }
                    }

                    MESSAGE_READ -> {
                        val arduinoMsg = msg.obj.toString()
                        if (arduinoMsg.contains("Machine ready for programming") ) {
                            machineStatusTextView.text = arduinoMsg
                            programmingButton.isEnabled = true
                        } else {
                            machineStatusTextView.text = arduinoMsg
                        }
                    }

                }
            }
        }


                /*
                        // Read message from Arduino
                        when (arduinoMsg.lowercase(Locale.getDefault())) {
                        //when (arduinoMsg) {
//                            "Machine online" -> {
//                                machineStatusTextView.text = "Machine online"
//                            }
//                            "Homing cycle started" -> {
//                                machineStatusTextView.text = "Homing cycle started"
//                            }
//                            "Manual programming started" -> {
//                                machineStatusTextView.text = "Manual programming started"
//                            }
//                            "Massage cycle started" -> {
//                                machineStatusTextView.text = "Massage cycle started"
//                            }
//
//                            "Load motor homing started" -> {
//                                machineStatusTextView.text = "Load motor homing started"
//                            }
//                            "Load motor LIMIT SWITCH pressed once" -> {
//                                machineStatusTextView.text = "Load motor LIMIT SWITCH pressed once"
//                            }
//                            "Load motor homing complete" -> {
//                                machineStatusTextView.text = "Load motor homing complete"
//                            }
//
//                            "Load motor origin cycle started" -> {
//                                machineStatusTextView.text = "Load motor origin cycle started"
//                            }
//                            "Load motor origin cycle complete" -> {
//                                machineStatusTextView.text = "Load motor origin cycle complete"
//                            }
//                            "Travel motor homing started" -> {
//                                machineStatusTextView.text = "Travel motor homing started"
//                            }
//                            "Travel motor LIMIT SWITCH pressed once" -> {
//                                machineStatusTextView.text = "Travel motor LIMIT SWITCH pressed once"
//                            }
//                            "Travel motor homing cycle complete" -> {
//                                machineStatusTextView.text = "Travel motor homing cycle complete"
//                            }
//                            "Travel motor origin cycle started" -> {
//                                machineStatusTextView.text = "Travel motor origin cycle started"
//                            }
//                            "Travel motor origin cycle complete" -> {
//                                machineStatusTextView.text = "Travel motor origin cycle complete"
//                            }
                            "Machine ready for programming" -> {
                                machineStatusTextView.text = "Machine ready for programming"
                                programmingButton.isEnabled = true
                            }
                            else -> {
                                machineStatusTextView.text = arduinoMsg
                                if (arduinoMsg == arduinoMsg){
                                    Log.e("TAG", "handleMessage: whatever", )
                                    programmingButton.isEnabled = true
                                }






//                            MACHINE_ONLINE.toString() -> {
//                                machineStatusTextView.text = "Machine online"
//                            }
//                            PREPARE_MACHINE.toString() -> {
//                                machineStatusTextView.text = "Homing cycle started"
//                            }
//                            BOTH_MOTORS_ORIGIN_COMPLETE.toString() -> {
//                                machineStatusTextView.text = "Machine ready for programming"
//                                programmingButton.isEnabled = true
//                            }
//                            MANUAL_PROGRAMMING_START.toString() -> {
//                                machineStatusTextView.text = "Manual Programming mode started"
//                            }
//                            MANUAL_PROGRAMMING_COMPLETE.toString() -> {
//                                machineStatusTextView.text = "Manual Programming complete. Machine ready for massage"
//                            }
//                            MACHINE_READY_FOR_MASSAGE.toString() -> {
//                                machineStatusTextView.text = "Machine ready for massage."
//                                massageButton.isEnabled = true
//                            }
//                            MASSAGE_CYCLE_START_MANUAL_POSITIONS.toString() -> {
//                                machineStatusTextView.text = "Massage started"
//                            }
//                            MASSAGE_COMPLETE.toString() -> {
//                                machineStatusTextView.text = "Massage complete"
//                            }
                        }
                    }
                }
            }
        }

 */


                // Select Bluetooth Device
                buttonConnect.setOnClickListener { // Move z to adapter list
                    val intent = Intent(this@MainActivity, SelectDeviceActivity::class.java)
                    startActivity(intent)
                }

                buttonDisconnect.setOnClickListener {
                    connectedThread!!.cancel()
                    toolbar.subtitle = "Disconnected from $deviceName"
                    buttonConnect.visibility = View.VISIBLE
                    buttonDisconnect.visibility = View.GONE
                }

                homingButton.setOnClickListener {
                    machineStatusTextView.text = "Homing: Homing cycle started"
                    connectedThread!!.write(PREPARE_MACHINE.toString())

                }

                massageButton.setOnClickListener {
                    connectedThread!!.write(MASSAGE_CYCLE_START_MANUAL_POSITIONS.toString())
                    val intent = Intent(this@MainActivity, MassageScreen::class.java)
                    startActivity(intent)
                }

                programmingButton.setOnClickListener {
                    machineStatusTextView.text = "Programming cycle started"
                    connectedThread!!.write(MANUAL_PROGRAMMING_START.toString())
                    startActivity(Intent(this@MainActivity, ProgrammingScreen::class.java))
                }


/*
        buttonToggle.setOnClickListener {
            var cmdText = ""
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

            connectedThread?.write(cmdText)     // Send command to Arduino board
        }



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
                //  This part will run repeatedly
                    println("Performing action...")
                    mHandler!!.postDelayed(this, 500)
                }
            }
        })

*/

        }
    }

