package com.example.reservation_system

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.math.sign

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Firebase
        auth = Firebase.auth

        button_signup_back.setOnClickListener{
            this.finish()
        }

        button_signup_signup.setOnClickListener{
            signUp()
        }

        edittext_signup_pwconfirm.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                signUp()
                true
            }
            else false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }

    // 회원 가입
    // TODO : 사용자 프로필 추가, https://firebase.google.com/docs/auth/android/manage-users?hl=ko, 이메일 인증도 가능
    private fun signUp(){
        val id = edittext_signup_id.text.toString()
        val pw = edittext_signup_pw.text.toString()
        val pwconfirm = edittext_signup_pwconfirm.text.toString()
        Log.d(TAG, id)
        Log.d(TAG, pw)
        Log.d(TAG, pwconfirm)

        if (pw != pwconfirm) return

        auth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, Login::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
                } else {
                    // If sign in fails, display a message to the user.ㅂ
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@SignUp, "이미 있는 아이디거나 확인 사항을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}