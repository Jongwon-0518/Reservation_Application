package com.example.reservation_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener{
            val id = editText_userid.text.toString()
            val pw = editText_userpassword.text.toString()

            if (id == "1111" && pw == "1111") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}