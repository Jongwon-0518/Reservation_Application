package com.example.reservation_system

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener {
            Log_in(it)
        }

        editText_userpassword.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Log_in(v)
                true
            }
            else false
        }

        // 불러오기
        val pref = this.getPreferences(0)
        editText_userid.setText(pref.getString("ID", ""))
        editText_userpassword.setText(pref.getString("PW", ""))

        val logged_out = intent.getBooleanExtra("From_Logout", false)
        // 로그아웃이 진행된 경우 자동로그인 해제
        if (!logged_out){
            checkbox_autoLogin.isChecked = pref.getBoolean("Cb_Autologin", false)
        }
        // 자동로그인 진행
        if (checkbox_autoLogin.isChecked){
            Log_in(null)
        }
    }

    private fun Log_in(v : View?){
        // 키보드 숨기기
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)

        if (editText_userid.text.toString() == "1111" && editText_userpassword.text.toString() == "1111"){
            Toast.makeText(this, "OOO님 환영합니다!", Toast.LENGTH_SHORT).show()

            // checkbox 저장
            this.getPreferences(0).edit().putBoolean("Cb_Autologin", checkbox_autoLogin.isChecked).apply()

            // 저장
            this.getPreferences(0).edit().putString("ID", editText_userid.text.toString()).apply()
            this.getPreferences(0).edit().putString("PW", editText_userpassword.text.toString()).apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ID", "아이디")
            startActivity(intent)
        }
    }
}