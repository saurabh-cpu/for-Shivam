package com.example.bluetoothfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothfinal.databinding.ActivityMassageScreenBinding
import java.util.*


class MassageScreen : AppCompatActivity() {

    lateinit var binding: ActivityMassageScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.motorPositionsRC.layoutManager = LinearLayoutManager(this)
        binding.motorPositionsRC.adapter = RecyclerAdapter(positionData)
    }
}