package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.room_information.*

class RoomInformation : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_information)

        database = Firebase.database.reference
        val room_code = intent.getIntExtra("code", 0)
        val code_string = "#" + room_code.toString()
        textView_roomInfo_code.text = code_string

        database.child("Room").child(room_code.toString()).get().addOnSuccessListener { it ->
            val map = it.value as HashMap<*, *>
            textView_roomInfo_title.text = map["title"] as String
            // TODO : Time
            textView_roomInfo_time.text = "영업 시간 : " + "09:00 ~ 20:30"
            textView_roomInfo_category.text = "카테고리 : " + map["room_category"] as String
            textView_roomInfo_information.text = map["information"] as String
            textView_roomInfo_phonenumber.text = "전화번호 : " + map["maker"] as String

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data while RoomInformation ", it)
        }

        button_back.setOnClickListener{
            this.finish()
        }

        button_gomenu.setOnClickListener{
            val intent = Intent(this, ReservationMenu::class.java)
            intent.putExtra("room_code", room_code)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}