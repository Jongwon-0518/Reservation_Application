package com.example.reservation_system

import android.R
import android.os.Bundle
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



        time_setting_save_button.setOnClickListener {
            this.finish()
        }

        button_Monday.setOnClickListener{
            println("Monday")
        }
        button_Tuesday.setOnClickListener{
            println("Tuesday")
        }
        button_Wednesday.setOnClickListener{
            println("Wednesday")
        }
        button_Thursday.setOnClickListener{
            println("Thursday")
        }
        button_Friday.setOnClickListener{
            println("Friday")
        }
        button_Saturday.setOnClickListener{
            println("Saturday")
        }
        button_Sunday.setOnClickListener{
            println("Sunday")
        }


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(com.example.reservation_system.R.anim.slide_left_enter, com.example.reservation_system.R.anim.slide_left_exit)
    }
}