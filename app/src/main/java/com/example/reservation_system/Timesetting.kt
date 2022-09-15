package com.example.reservation_system

import android.R
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import kotlinx.android.synthetic.main.time_set.*


class Timesetting : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.reservation_system.R.layout.time_set)

        database = Firebase.database.reference
        val room_code = intent.getIntExtra("room_code", 0)

        val holiday = arrayListOf(Mon_holiday_check, Tue_holiday_check, Wed_holiday_check, Thu_holiday_check, Fri_holiday_check, Sat_holiday_check, Sun_holiday_check)
        val start_times = arrayListOf(Mon_open_time, Tue_open_time, Wed_open_time, Thu_open_time, Fri_open_time, Sat_open_time, Sun_open_time)
        val finish_times = arrayListOf(Mon_finish_time, Tue_finish_time, Wed_finish_time, Thu_finish_time, Fri_finish_time, Sat_finish_time, Sun_finish_time)

        // Read Database
        database.child("Room").child(room_code.toString()).child("요일").get().addOnSuccessListener { it ->
            val lst = it.value as ArrayList<*>
            for (day in 0..6) {
                val d = lst[day] as ArrayList<*>
                if (d[0].toString() == "휴무"){
                    holiday[day].isChecked = true
                }
                start_times[day].setText(d[0].toString())
                finish_times[day].setText(d[1].toString())
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


        for (day in 0..6) {
            holiday[day].setOnCheckedChangeListener { compoundButton, ischecked ->
                if (ischecked) {
                    start_times[day].setText("휴무")
                    finish_times[day].setText("휴무")
                    // Can't Change
                    start_times[day].clearFocus()
                    finish_times[day].clearFocus()
                    start_times[day].isFocusableInTouchMode = false
                    finish_times[day].isFocusableInTouchMode = false
                } else {
                    start_times[day].setText("")
                    finish_times[day].setText("")
                    start_times[day].isFocusableInTouchMode = true
                    finish_times[day].isFocusableInTouchMode = true
                }
            }
        }

        time_setting_save_button.setOnClickListener {
            for (day in 0..6) {
                val st = start_times[day].text.toString()
                val fn = finish_times[day].text.toString()
                var a = 0
                if (st != "휴무"){
                    a = 1
                }
                if (st.length == 0 || fn.length == 0){
                    Toast.makeText(this, "모든 요일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (a == 1){
                    val st_int = st.toInt()
                    val fn_int = fn.toInt()
                    if (st_int < 0 || st_int > 2400 || fn_int < 0 || fn_int > 2400 || (st_int % 100) > 59){
                        Toast.makeText(this, "알맞은 시간 형태를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (st_int > fn_int){
                        Toast.makeText(this, "시작시간이 종료시간보다 큽니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            val taskMap = HashMap<String, Any>()
            val days = ArrayList<ArrayList<String>>()
            for (i in 0..6) {
                days.add(arrayListOf(start_times[i].text.toString(), finish_times[i].text.toString()))
            }
            taskMap.put("요일", days)
            database.child("Room").child(room_code.toString()).updateChildren(taskMap)

            this.finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(com.example.reservation_system.R.anim.slide_left_enter, com.example.reservation_system.R.anim.slide_left_exit)
    }
}