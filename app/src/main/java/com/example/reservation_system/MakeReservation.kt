package com.example.reservation_system

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_make_reservation.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.make_time.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MakeReservation : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    var weektimes = ArrayList<String>()
    var dividetime = 30
    var room_code = 0
    var menu_name = ""
    var room_title = ""
    val instance = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_reservation)

        var day = instance.get(Calendar.DAY_OF_WEEK)
        var time_array : ArrayList<String>
        menu_name = intent.getStringExtra("menu_name").toString()
        room_code = intent.getIntExtra("room_code", 0)
        database = Firebase.database.reference
        // Read Database
        database.child("Room").child(room_code.toString()).child("나눈시간").get().addOnSuccessListener { it ->
            dividetime = (it.value as Long).toInt()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        database.child("Room").child(room_code.toString()).get().addOnSuccessListener { it ->
            val it = it.value as HashMap<*,*>
            room_title = it["title"] as String
            dividetime = (it["나눈시간"] as Long).toInt()
            val days = it["요일"]
            if (day == 1) day = 6 else day -= 2
            weektimes = days as ArrayList<String>
            time_array = get_times_of_day(day)
            get_buttons(time_array)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        var day_string = ""

        val dateFormat: DateFormat = SimpleDateFormat("yyyy년MM월dd일")
        calendar?.run{
            val date = Date(this.date)
            cal_text.text = dateFormat.format(date)
        }
        calendar.setOnDateChangeListener{ calendarView, year, month, i ->
            day_string = "${year}년 ${month+1}월 ${i}일"
            cal_text.text = day_string
            instance.set(year, month, i)
            day = instance.get(Calendar.DAY_OF_WEEK)
            if (day == 1) day = 6 else day -= 2
            time_array = get_times_of_day(day)
            get_buttons(time_array)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }

    private fun get_times_of_day(day : Int) : ArrayList<String> {
        val daytime : ArrayList<String> = weektimes[day] as ArrayList<String>
        if (daytime[0] == "휴무"){
            return arrayListOf()
        }
        var start = daytime[0].slice(0..1).toInt() * 60 + daytime[0].slice(2..3).toInt()
        start = (start / dividetime + if (start % dividetime == 0) 0 else 1) * dividetime
        var finish = daytime[1].slice(0..1).toInt() * 60 + daytime[1].slice(2..3).toInt()
        finish -= finish % dividetime
        val result = arrayListOf<String>()
        for (time in start .. finish step(dividetime)) {
            result.add((time / 60).toString().padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0'))
        }
        return result
    }

    private fun get_buttons(time_array : ArrayList<String>){
        button_reservation_container.removeAllViews()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val linearlayouts = arrayListOf<LinearLayout>()
        val length = time_array.size / 4
        for (i in 0..length){
            val line_linearlayout = LinearLayout(this)
            if (i*4+3 < time_array.size) {
                for (j in i*4..i*4+3){
                    val dynamicButton = Button(this)
                    dynamicButton.text = time_array[j]
                    dynamicButton.layoutParams = layoutParams
                    dynamicButton.setOnClickListener {
                        get_dialog(dynamicButton.text.toString())
                    }
                    line_linearlayout.addView(dynamicButton)
                }
            }
            else{
                for (j in i*4..time_array.size-1){
                    val dynamicButton = Button(this)
                    dynamicButton.text = time_array[j]
                    dynamicButton.layoutParams = layoutParams
                    dynamicButton.setOnClickListener {
                        get_dialog(dynamicButton.text.toString())
                    }
                    line_linearlayout.addView(dynamicButton)
                }
            }
            linearlayouts.add(line_linearlayout)
            button_reservation_container.addView(line_linearlayout)
        }
    }

    private fun get_dialog(time : String) {
        val month = (instance.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val date = instance.get(Calendar.DATE).toString().padStart(2, '0')
        val builder = AlertDialog.Builder(this)
        builder.setTitle("$month 월 $date 일 $time")
        builder.setMessage(menu_name + "을 예약하시겠습니까?")
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
            val reservation_time = month + date + time.slice(0..1) + time.slice(3..4)
            val reserve = reservation_Data(room_code, room_title, menu_name, reservation_time, getUserPhoneNumber())
            writeDatabase(reserve)
        }
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    private fun writeDatabase(reservation : reservation_Data){
        //데이터 저장
        database.child("Reservation").push().setValue(reservation)
            .addOnSuccessListener {
                Toast.makeText(this, "예약되었습니다!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("reserved", 1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
            }
            .addOnFailureListener{
                Log.e("firebase", "Error while writing reservation")
            }
    }
}