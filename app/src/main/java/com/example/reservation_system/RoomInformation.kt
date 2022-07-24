package com.example.reservation_system

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.room_information.*

class RoomInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_information)

        val room_number = intent.getIntExtra("code", 0)
        room_code_TextView.text = room_number.toString()
    }
}