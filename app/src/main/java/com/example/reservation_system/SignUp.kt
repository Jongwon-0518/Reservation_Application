package com.example.reservation_system

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.math.sign

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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
    private fun signUp(){
        val id = edittext_signup_id.text.toString()
        val pw = edittext_signup_pw.text.toString()
        val pwconfirm = edittext_signup_pwconfirm.text.toString()
        Log.d(TAG, id)
        Log.d(TAG, pw)
        Log.d(TAG, pwconfirm)

        // 25분 50초
        if (pw != pwconfirm) return

        auth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    // UI (when success)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    // UI (when failed)
                }
            }
    }
}