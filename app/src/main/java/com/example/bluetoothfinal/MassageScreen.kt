package com.example.bluetoothfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothfinal.databinding.ActivityMassageScreenBinding



class MassageScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMassageScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.motorPositionsRC.layoutManager = LinearLayoutManager(this)
        binding.motorPositionsRC.adapter = RecyclerAdapter(positionData)
    }
}