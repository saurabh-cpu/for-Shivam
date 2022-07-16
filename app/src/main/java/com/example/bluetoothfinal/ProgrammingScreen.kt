package com.example.bluetoothfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.example.bluetoothfinal.databinding.ActivityProgrammingScreenBinding



class ProgrammingScreen : AppCompatActivity() {

    private lateinit var binding: ActivityProgrammingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loadMotorFwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,Helpers.MOVE_LOAD_MOTOR_FORWARD, "Load motor moving forward")
            true
        }

        binding.loadMotorBwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,Helpers.MOVE_LOAD_MOTOR_BACKWARD, "Load Motor moving backward")
            true
        }

        binding.travelMotorFwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,Helpers.MOVE_TRAVEL_MOTOR_FORWARD, "Travel Motor moving forward")
            true
        }

        binding.travelMotorBwdButton.setOnTouchListener { view, motionEvent ->
            view.performClick()
            handleTouch(motionEvent,Helpers.MOVE_TRAVEL_MOTOR_BACKWARD, "Travel Motor moving backward")
            true
        }

        binding.saveLoadPositionButton.setOnClickListener {
            connectedThread?.write(Helpers.SAVE_LOAD_MOTOR_POSITION.toString())
        }

        binding.saveTravelPositionButton.setOnClickListener {
            connectedThread?.write(Helpers.SAVE_TRAVEL_MOTOR_POSITION.toString())
        }

    }

    private fun handleTouch(motionEvent: MotionEvent?, buttonPressedInput: Int, statusText: String) {
        when(motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                connectedThread?.write(buttonPressedInput.toString())
                binding.commandSentTextView.text = statusText
            }
            MotionEvent.ACTION_UP -> {
                connectedThread?.write(Helpers.MASSAGE_PAUSED.toString())
                binding.commandSentTextView.text = ""
            }
        }
    }


}



