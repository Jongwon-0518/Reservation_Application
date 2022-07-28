package com.example.reservation_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reservation_menu.*

class ReservationMenu : AppCompatActivity() {
    
    // TODO : Server의 Menu 가져오기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_menu)

        menu1.setOnClickListener{
            val intent = Intent(this, MakeReservation::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}