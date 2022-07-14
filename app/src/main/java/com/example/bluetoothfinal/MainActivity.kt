package com.example.bluetoothfinal

import android.R.attr.button
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
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
import com.example.bluetoothfinal.R.color.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


var newNumber = 0

var mmSocket: BluetoothSocket? = null
var handler: Handler? = null
private val deviceName: String? = null
private var deviceAddress: String? = null
var connectedThread: MainActivity.ConnectedThread? = null
var createConnectThread: MainActivity.CreateConnectThread? = null

private val CONNECTING_STATUS = 1 // used in bluetooth handler to identify message status
private val MESSAGE_READ = 2 // used in bluetooth handler to identify message update


class MainActivity : AppCompatActivity() {


    lateinit var buttonConnect:Button
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var textViewInfo:TextView
    lateinit var buttonToggle: Button
    lateinit var imageView: ImageView
    lateinit var buttonPressed: Button




    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonConnect = findViewById(R.id.buttonConnect)
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        textViewInfo = findViewById(R.id.textViewInfo)
        buttonToggle = findViewById(R.id.buttonToggle)
        imageView = findViewById(R.id.imageView)
        buttonPressed = findViewById(R.id.buttonPressed)

        progressBar.visibility = View.GONE
        buttonToggle.isEnabled = false
        imageView.setBackgroundColor(ContextCompat.getColor(this,purple_200))

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

        // Button to ON/OFF LED on Arduino Board
        // Button to ON/OFF LED on Arduino Board
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

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    @SuppressLint("MissingPermission")
    class CreateConnectThread(bluetoothAdapter: BluetoothAdapter, address: String?) : Thread() {
        @SuppressLint("MissingPermission")
        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.cancelDiscovery()
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket!!.connect()
                Log.e("Status", "Device connected")
                handler?.obtainMessage(CONNECTING_STATUS, 1, -1)!!.sendToTarget()
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket!!.close()
                    Log.e("Status", "Cannot connect to device")
                    handler!!.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget()
                } catch (closeException: IOException) {
                    Log.e(TAG, "Could not close the client socket", closeException)
                }
                return
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = ConnectedThread(mmSocket!!)
            connectedThread!!.run()
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }

        init {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            var tmp: BluetoothSocket? = null
            val uuid = bluetoothDevice.uuids[0].uuid
            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.e(TAG, "Socket's create() method failed", e)
            }
            mmSocket = tmp
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        override fun run() {
            val buffer = ByteArray(1024) // buffer store for the stream
            var bytes = 0 // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = mmInStream?.read()?.toByte()!! //as Byte
                    var readMessage: String
                    if (buffer[bytes] == '\n'.code.toByte()) {
                        readMessage = String(buffer, 0, bytes)
                        Log.e("Arduino Message", readMessage)
                        handler!!.obtainMessage(MESSAGE_READ, readMessage).sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        fun write(input: String) {
            val bytes = input.toByteArray() //converts entered String into bytes
            try {
                //mmOutStream?.write(bytes)
                mmOutStream?.write(bytes)
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }




}