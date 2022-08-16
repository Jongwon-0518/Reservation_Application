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
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_my_reservation_information.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*


class Mypage : Fragment() {


    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val user = Firebase.auth.currentUser!!

        database = Firebase.database.reference

        // 로그아웃
        rootView.button_logout.setOnClickListener{
            Firebase.auth.signOut()
            backToLogin()
        }
        val name = getUserName()
        val email = getUseremail()
        val phonenumber : String = email!!.replace("@abc.com", "")

        rootView.MyPage_title.text = name + "님 환영합니다."

        rootView.button_change_nickname.setOnClickListener{
            // TODO : 닉네임 변경 버튼
        }

        // 회원탈퇴
        rootView.button_signout.setOnClickListener{

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("회원 탈퇴")
                .setMessage("탈퇴하시겠습니까??")
                .setPositiveButton("확인"){
                    dialogInterface: DialogInterface, i:Int ->
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User account deleted.")
                                database.child("User").child(phonenumber).removeValue()

                            }
                        }

                    backToLogin()
                }
                .setNegativeButton("취소", null)
            builder.show()

            Toast.makeText(this.context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        rootView.button_change_nickname.setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("닉네임을 입력해주세요.")
            builder.setMessage("Message")
            var editNickname = EditText(this.context)
            builder.setView(editNickname)

            // TODO : 닉네임 바꾸기 AlertDialog
            builder.setPositiveButton("Positive") { dialogInterface: DialogInterface, i: Int ->
//                toast("Positive")
            }
            builder.setNegativeButton("Negative") { dialogInterface: DialogInterface, i: Int ->
//                toast("Negative")
            }
//            builder.setNeutralButton("Neutral") { dialogInterface: DialogInterface, i: Int ->
//                toast("Neutral")
//            }
            builder.show()
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