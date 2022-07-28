package com.example.reservation_system

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
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

        // TODO : 날짜 별 시간 받아오기
        val time_array = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

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
        
        // 날짜에 맞게 버튼 생성
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        time_array.forEach{
            var dynamicButton = Button(this)
            dynamicButton.text = it.toString()
            dynamicButton.layoutParams = layoutParams
            button_reservation_container.addView(dynamicButton)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }
}