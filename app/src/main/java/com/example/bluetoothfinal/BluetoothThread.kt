package com.example.bluetoothfinal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.util.Log
import com.example.bluetoothfinal.Helpers.Companion.CONNECTING_STATUS
import com.example.bluetoothfinal.Helpers.Companion.MESSAGE_READ
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream




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
                Log.e(ContentValues.TAG, "Could not close the client socket", closeException)
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
            Log.e(ContentValues.TAG, "Could not close the client socket", e)
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
            Log.e(ContentValues.TAG, "Socket's create() method failed", e)
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
