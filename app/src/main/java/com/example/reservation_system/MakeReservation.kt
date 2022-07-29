package com.example.reservation_system

import android.content.Intent
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
        val time_array = arrayListOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
            "18:00", "18:30", "19:00", "19:30", "20:00", "20:30")
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

        // TODO : 위에 calendar.setOnDateChangeListener에 넣고 바뀔때마다 바뀌게 해야함
        // 날짜에 맞게 버튼 생성
        // 날짜에 맞게 linearlayout 생성
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        var linearlayouts = arrayListOf<LinearLayout>()
        val length = time_array.size / 4
        for (i in 0..length){
            val line_linearlayout = LinearLayout(this)
            if (i*4+3 < time_array.size) {
                for (j in i*4..i*4+3){
                    var dynamicButton = Button(this)
                    dynamicButton.text = time_array[j]
                    dynamicButton.layoutParams = layoutParams
                    line_linearlayout.addView(dynamicButton)
                }
            }
            else{
                for (j in i*4..time_array.size-1){
                    var dynamicButton = Button(this)
                    dynamicButton.text = time_array[j]
                    dynamicButton.layoutParams = layoutParams
                    line_linearlayout.addView(dynamicButton)
                }
            }
            linearlayouts.add(line_linearlayout)
            button_reservation_container.addView(line_linearlayout)
        }

        button_reservation_last.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("reserved", 1)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }
}