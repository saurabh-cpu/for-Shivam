package com.example.bluetoothfinal

class Helpers {

    companion object {

        const val CONNECTING_STATUS = 1 // used in bluetooth handler to identify message status
        const val MESSAGE_READ = 2 // used in bluetooth handler to identify message update


        const val  MASSAGE_PAUSED = 0                                       // For pause resume
        const val  MASSAGE_RESUME = 1
        const val  MASSAGE_RESTART = 2
        const val  PREPARE_MACHINE = 3                                      // Homing & Origin cycle of each motor
        const val  MANUAL_PROGRAMMING_MODE_START = 4                        // For manual setting of travel & load motor step positions
        const val  MASSAGE_CYCLE_START_DEFAULT_POSITIONS = 5                // Preset values of Travel & Load motors defined in terms of steps
        const val  MASSAGE_CYCLE_START_MANUAL_POSITIONS = 6                 // Manually set values of Travel & Load motors defined in terms of steps
        const val  MASSAGE_FULL_CYCLE_START = 7
        const val  MASSAGE_FULL_CYCLE_COMPLETE = 8                          // Momentary state turns to 0 after showing a message
        const val  MANUAL_PROGRAMMING_MODE_COMPLETE = 9


        const val MOVE_LOAD_MOTOR_FORWARD = 11
        const val MOVE_LOAD_MOTOR_BACKWARD = 12
        const val MOVE_TRAVEL_MOTOR_FORWARD = 13
        const val MOVE_TRAVEL_MOTOR_BACKWARD = 14

        const val SAVE_LOAD_MOTOR_POSITION = 15
        const val SAVE_TRAVEL_MOTOR_POSITION = 16


    // Homing states
        const val  LOAD_MOTOR_HOMING_START = 20
        const val  LOAD_MOTOR_HOMING_ONGOING = 21
        const val  LOAD_MOTOR_HOMING_COMPLETE = 22

        const val  TRAVEL_MOTOR_HOMING_START = 23
        const val  TRAVEL_MOTOR_HOMING_ONGOING = 24
        const val  TRAVEL_MOTOR_HOMING_COMPLETE = 25

        const val  BOTH_MOTORS_HOMING_COMPLETE = 26


    // Origin states
        const val  LOAD_MOTOR_ORIGIN_START = 30
        const val  LOAD_MOTOR_ORIGIN_ONGOING = 31
        const val  LOAD_MOTOR_ORIGIN_COMPLETE = 32

        const val  TRAVEL_MOTOR_ORIGIN_START = 33
        const val  TRAVEL_MOTOR_ORIGIN_ONGOING = 34
        const val  TRAVEL_MOTOR_ORIGIN_COMPLETE = 35

        const val BOTH_MOTORS_ORIGIN_COMPLETE = 36

    // Limit switch pressed states
        const val  TRAVEL_MOTOR_LIMIT_SWITCH_PRESS1 = 40
        const val  TRAVEL_MOTOR_LIMIT_SWITCH_PRESS2 = 41
        const val  LOAD_MOTOR_LIMIT_SWITCH_PRESS1 = 42
        const val  LOAD_MOTOR_LIMIT_SWITCH_PRESS2 = 43


    // For manual position selection
        const val  MANUAL_MODE_ONGOING = 50
        const val  MANUAL_MODE_COMPLETE = 51

    // For massage cycle
        const val  MASSAGE_SINGLE_CYCLE_START = 60
        const val  MASSAGE_SINGLE_CYCLE_ONGOING = 61
        const val  MASSAGE_SINGLE_CYCLE_COMPLETE = 62

    // machine ready states
        const val  MACHINE_READY_FOR_MASSAGE = 80

    }
}