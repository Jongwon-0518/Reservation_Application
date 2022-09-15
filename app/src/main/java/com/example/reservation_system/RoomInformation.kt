package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
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
            val weektime = arrayListOf(textView_roomInfo_mon_time, textView_roomInfo_tue_time, textView_roomInfo_wed_time,
                textView_roomInfo_thu_time, textView_roomInfo_fri_time, textView_roomInfo_sat_time, textView_roomInfo_sun_time)
            val week = listOf("월", "화", "수", "목", "금", "토", "일")
            val per_week = map["요일"] as ArrayList<*>
            for (day in 0..6) {
                val d = per_week[day] as ArrayList<*>
                if (d[0].toString() == ""){
                    weektime[day].visibility = GONE
                    textView_room_time.text = "영업시간이 설정되지 않았습니다."
                    continue
                }
                if (d[0].toString() == "휴무"){
                    weektime[day].text = week[day] + " : 휴무"
                    continue
                }
                weektime[day].text = week[day] + " : " + d[0].toString() + " ~ " + d[1]
            }
            textView_roomInfo_title.text = map["title"] as String
            textView_roomInfo_category.text = "카테고리 : " + map["room_category"] as String
            textView_roomInfo_information.text = map["information"] as String
            textView_roomInfo_phonenumber.text = "전화번호 : " + map["maker"] as String
            textView_roomInfo_location.text = "위치 : " + map["location"] as String

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