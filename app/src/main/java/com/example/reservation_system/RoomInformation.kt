package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.room_information.*

class RoomInformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_information)

        // TODO : 나머지 부분도 채우기
        val room_number = intent.getIntExtra("code", 0)
        val code_string = "#" + room_number.toString()
        room_code_TextView.text = code_string

        button_back.setOnClickListener{
            this.finish()
        }

        button_goreservation.setOnClickListener{
            val intent = Intent(this, ReservationMenu::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}