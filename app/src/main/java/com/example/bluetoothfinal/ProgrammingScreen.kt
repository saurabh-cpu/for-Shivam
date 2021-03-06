package com.example.bluetoothfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import com.example.bluetoothfinal.databinding.ActivityProgrammingScreenBinding
import java.util.*


class ProgrammingScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProgrammingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MachineState.MESSAGE_READ -> {
                        val arduinoMsg: String = msg.obj.toString()                 // Read message from Arduino
                        binding.statusReceivedTextView.text = arduinoMsg

                        if (arduinoMsg.contains("Current Travel motor position value ") ) {
                            binding.travelMotorTextView.text = arduinoMsg
                        } else if (arduinoMsg.contains("Current Load motor position value ") ) {
                            binding.loadMotorTextView.text = arduinoMsg
                        } else if (arduinoMsg.contains("Machine ready for massage")){
                                binding.loadMotorTextView.text = arduinoMsg
                                val intent =  Intent(this@ProgrammingScreen, MainActivity::class.java)
                                intent.putExtra("machineState", "Machine ready for massage")
                                startActivity(intent)
                                finish()
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
            connectedThread?.write(MachineState.SAVE_MOTOR_POSITIONS.toString())
            binding.commandSentTextView.text = "Saving motor positions"
        }

        binding.saveProgramButton.setOnClickListener {
            connectedThread?.write(MachineState.MANUAL_PROGRAMMING_COMPLETE.toString())
            binding.commandSentTextView.text = "Finishing program"

        }



    }

    private fun handleTouch(motionEvent: MotionEvent?, buttonPressedInput: Int, statusText: String) {
        when(motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                connectedThread?.write(buttonPressedInput.toString())
                binding.commandSentTextView.text = statusText
            }
            MotionEvent.ACTION_UP -> {
                connectedThread?.write(MachineState.MASSAGE_PAUSED.toString())
                binding.commandSentTextView.text = ""
            }
        }
    }

}



