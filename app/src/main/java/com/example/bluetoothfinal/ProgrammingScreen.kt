package com.example.bluetoothfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import com.example.bluetoothfinal.databinding.ActivityProgrammingScreenBinding


lateinit var positionData: MutableList<MotorData>

class ProgrammingScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProgrammingScreenBinding
    private lateinit var loadPositionData: MutableList<Int>
    private lateinit var travelPositionData: MutableList<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPositionData = mutableListOf()
        travelPositionData = mutableListOf()
        positionData = mutableListOf()


        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MachineState.MESSAGE_READ -> {
                        val arduinoMsg: String = msg.obj.toString()                 // Read message from Arduino
                        binding.statusReceivedTextView.text = arduinoMsg
                        if (arduinoMsg.contains("Current Travel motor position value ") ) {
                            binding.travelMotorTextView.text = arduinoMsg
                            travelPositionData.add(arduinoMsg.subSequence((arduinoMsg.indexOf("_",1)+1),arduinoMsg.indexOf("_",arduinoMsg.indexOf("_",1)+1)).toString().toInt())
                            Log.e("motorData", "handleMessage TravelPosition: ${travelPositionData[travelPositionData.size-1]}")
                        } else if (arduinoMsg.contains("Current Load motor position value ") ) {
                            binding.loadMotorTextView.text = arduinoMsg
                            loadPositionData.add(arduinoMsg.subSequence((arduinoMsg.indexOf("_",1)+1),arduinoMsg.indexOf("_",arduinoMsg.indexOf("_",1)+1)).toString().toInt())
                            Log.e("motorData", "handleMessage LoadPosition: ${loadPositionData[loadPositionData.size-1]}")
                        } else if (arduinoMsg.contains("Machine ready for massage")){
                            binding.loadMotorTextView.text = arduinoMsg
                            for ( i in 0 until travelPositionData.size-1) {
                                positionData.add(MotorData(loadPositionData[i],travelPositionData[i]))
                            }
                            val intent =  Intent(this@ProgrammingScreen, MainActivity::class.java)
                            intent.putExtra("machineState", "Machine ready for massage")

                            startActivity(intent)
                            finish()
                        } else if (arduinoMsg.contains("Travel limit switch pressed")) {
                            when (binding.commandSentTextView.text) {
                                else -> enableButtons()
                            }
                            binding.loadMotorTextView.text = arduinoMsg
                        } else if (arduinoMsg.contains("Load limit switch pressed") ) {
                            binding.loadMotorTextView.text = arduinoMsg
                        } else if (arduinoMsg.contains("Disabling all buttons till load motor reaches home") ) {
                            binding.statusReceivedTextView.text = arduinoMsg
                            disableButtons()
                        } else if (arduinoMsg.contains("Load motor home enabling buttons") ) {
                            binding.statusReceivedTextView.text = arduinoMsg
                            enableButtons()
                        } else if (arduinoMsg.contains("Moving load motor & travel motor to home positions") ) {
                            val message = arduinoMsg + "Buttons disabled"
                            binding.statusReceivedTextView.text = message
                            disableButtons()
                            loadPositionData = mutableListOf()
                            travelPositionData = mutableListOf()
                            positionData = mutableListOf()
                        } else if (arduinoMsg.contains("Load motor & Travel motor home. Programming can be started") ) {
                            val message = arduinoMsg +  arduinoMsg + "Buttons enabled"
                            binding.statusReceivedTextView.text = message
                            enableButtons()
                        }


                        /*when (arduinoMsg.lowercase(Locale.getDefault())) {
                            MachineState.TRAVEL_MOTOR_LIMIT_SWITCH_PRESS1.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Travel motor limit switch pressed once"
                            }
                            MachineState.TRAVEL_MOTOR_LIMIT_SWITCH_PRESS2.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Travel motor limit switch pressed twice"
                            }
                            MachineState.LOAD_MOTOR_LIMIT_SWITCH_PRESS1.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Load motor limit switch pressed once"
                            }
                            MachineState.LOAD_MOTOR_LIMIT_SWITCH_PRESS2.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Load motor limit switch pressed twice"
                            }
                            MachineState.BOTH_MOTORS_HOMING_COMPLETE.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Both motors at home"
                            }
                            MachineState.BOTH_MOTORS_ORIGIN_COMPLETE.toString() -> {
                                binding.statusReceivedTextView.text = "Homing: Both motors at origin. Machine ready for massage."
                                val intent = Intent(this@ProgrammingScreen, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }*/


                    }
                }
            }
        }


        binding.loadMotorFwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,MachineState.MOVE_LOAD_MOTOR_FORWARD, "Load motor moving forward")
            true
        }

        binding.loadMotorBwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,MachineState.MOVE_LOAD_MOTOR_BACKWARD, "Load Motor moving backward")
            true
        }

        binding.travelMotorFwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,MachineState.MOVE_TRAVEL_MOTOR_FORWARD, "Travel Motor moving forward")
            true
        }

        binding.travelMotorBwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,MachineState.MOVE_TRAVEL_MOTOR_BACKWARD, "Travel Motor moving backward")
            true
        }

        binding.saveMotorPositionButton.setOnClickListener {
            connectedThread?.write(MachineState.SAVE_MOTOR_POSITIONS.toString() + '/')
            binding.commandSentTextView.text = getString(R.string.saving_motor_positions)
        }

        binding.saveProgramButton.setOnClickListener {
            connectedThread?.write(MachineState.MANUAL_PROGRAMMING_COMPLETE.toString() + '/')
            binding.commandSentTextView.text = getString(R.string.finishing_programming)

        }



    }

    private fun handleTouch(motionEvent: MotionEvent?, buttonPressedInput: Int, statusText: String) {
        when(motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                connectedThread?.write("$buttonPressedInput/")
                binding.commandSentTextView.text = statusText
            }
            MotionEvent.ACTION_UP -> {
                connectedThread?.write(MachineState.MASSAGE_PAUSED.toString())
                binding.commandSentTextView.text = ""
            }
        }
    }

    private fun enableButtons() {
        binding.loadMotorFwdButton.isEnabled = true
        binding.loadMotorBwdButton.isEnabled = true
        binding.travelMotorFwdButton.isEnabled = true
        binding.travelMotorBwdButton.isEnabled = true
        binding.saveProgramButton.isEnabled = true
        binding.saveMotorPositionButton.isEnabled = true
    }

    private fun disableButtons() {
        binding.loadMotorFwdButton.isEnabled = false
        binding.loadMotorBwdButton.isEnabled = false
        binding.travelMotorFwdButton.isEnabled = false
        binding.travelMotorBwdButton.isEnabled = false
        binding.saveProgramButton.isEnabled = false
        binding.saveMotorPositionButton.isEnabled = false
    }



}





