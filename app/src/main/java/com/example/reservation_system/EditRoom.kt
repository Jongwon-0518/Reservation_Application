package com.example.reservation_system

import android.R.attr.data
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.edit_room.*
import kotlinx.android.synthetic.main.room_information.*


class EditRoom : AppCompatActivity() {

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_room)

        database = Firebase.database.reference

        // TODO : 나머지 부분도 채우기
        val room_number = intent.getIntExtra("code", 0)
        Log.d("-----------", room_number.toString())




        database.child("Room").child(room_number.toString()).get().addOnSuccessListener {
            val test = it.value
            Log.i("--------------", "Got value ${test}")
            Log.d("--------------", test.toString())
            val room_title = (it.value as HashMap<*, *>)["title"].toString()
            val information = (it.value as HashMap<*, *>)["information"].toString()
            val maker = (it.value as HashMap<*, *>)["maker"].toString()
            val room_category = (it.value as HashMap<*, *>)["room_category"].toString()

            room_title_edit.hint = room_title
            Category_edit.hint = room_category
            room_info_edit.hint = information

        }.addOnFailureListener{
            Log.e("-----------------------", "Error getting data", it)
        }

        edit_complete_button.setOnClickListener{
            this.finish()
        }

        delete_button.setOnClickListener{

        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}