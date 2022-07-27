package com.example.reservation_system

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_make_reservation.*
import kotlinx.android.synthetic.main.make_time.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MakeReservation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_reservation)
        var day_string = ""

        val dateFormat: DateFormat = SimpleDateFormat("yyyy년MM월dd일")
        calendar?.run{
            val date = Date(this.date)
            cal_text.text = dateFormat.format(date)
        }
        calendar.setOnDateChangeListener{ calendarView, year, month, day ->
            day_string = "${year}년 ${month+1}월 ${day}일"
            cal_text.text = day_string
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}