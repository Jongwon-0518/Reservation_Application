package com.example.reservation_system

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_my_reservation_information.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*


class Mypage : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_mypage, container, false)

        // 로그아웃
        rootView.button_logout.setOnClickListener{
            Firebase.auth.signOut()
            backToLogin()
        }

        // 회원탈퇴
        rootView.button_signout.setOnClickListener{

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("회원 탈퇴")
                .setMessage("탈퇴하시겠습니까??")
                .setPositiveButton("확인"){
                    dialogInterface: DialogInterface, i:Int ->

                    val user = Firebase.auth.currentUser!!

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User account deleted.")
                            }
                        }

                    backToLogin()
                }
                .setNegativeButton("취소", null)
            builder.show()

            Toast.makeText(this.context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
        return rootView
    }

    private fun backToLogin(){
        val intent = Intent(context, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("From_Logout", true)
        startActivity(intent)
    }
}