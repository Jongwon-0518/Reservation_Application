package com.example.reservation_system

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    // TODO : 로그아웃 진행 시 저장된 정보 삭제

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase
        auth = Firebase.auth

        // Button Listener
        button_login.setOnClickListener {
            Log_in(it)
        }

        button_register.setOnClickListener{
            startActivity(Intent(this, SignUp::class.java))
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
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

    fun Log_in(v : View?) {
        val id_number = editText_userid.text.toString()
        val pw = editText_userpassword.text.toString()
        val email = id_number + "@abc.com"

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)

        auth?.signInWithEmailAndPassword(email, pw)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {
                    // checkbox 저장
                    val auth = Firebase.auth
                    val user = auth.currentUser!!
                    Toast.makeText(this, user.displayName + "님 환영합니다!", Toast.LENGTH_SHORT).show()
                    this.getPreferences(0).edit().putBoolean("Cb_Autologin", checkbox_autoLogin.isChecked).apply()

                    // 저장
                    this.getPreferences(0).edit().putString("ID", editText_userid.text.toString()).apply()
                    this.getPreferences(0).edit().putString("PW", editText_userpassword.text.toString()).apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Login Failed
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
}