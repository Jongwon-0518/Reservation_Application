package com.example.reservation_system

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.room_information.*

class RoomInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_information)

        val room_number = intent.getIntExtra("code", 0)
        val code_string = "#" + room_number.toString()
        room_code_TextView.text = code_string
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}