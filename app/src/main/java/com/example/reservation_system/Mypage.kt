package com.example.reservation_system

import android.annotation.SuppressLint
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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_mypage.view.*


class Mypage : Fragment() {


    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val user = Firebase.auth.currentUser!!



        database = Firebase.database.reference
        database.child("Room").orderByChild("maker").equalTo(getUserPhoneNumber()).get().addOnSuccessListener {
            val list = it.value

        }

        // 로그아웃
        rootView.button_logout.setOnClickListener{
            Firebase.auth.signOut()
            backToLogin()
        }
        var name = getUserName()
        val email = getUseremail()
        val phonenumber : String = email!!.replace("@abc.com", "")

        rootView.MyPage_title.text = name + "님 환영합니다."

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
                                // TODO : 예약 남아있다면 회원 탈퇴 철회
                                Log.d(TAG, "User account deleted.")
                                database.child("User").child(phonenumber).removeValue()
                                Toast.makeText(this.context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                backToLogin()
                            }
                        }
                }
                .setNegativeButton("취소", null)
            builder.show()
        }

        rootView.update_log.setOnClickListener{
            val intent = Intent(context, Updatelog::class.java)
            startActivity(intent)
        }

        rootView.button_change_nickname.setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("닉네임을 입력해주세요.")
            builder.setMessage("Message")
            val editNickname = EditText(this.context)
            builder.setView(editNickname)

            builder.setPositiveButton("바꾸기") { dialogInterface: DialogInterface, i: Int ->
                if (editNickname.length() == 0){
                    Toast.makeText(requireContext(), "변경하실 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = editNickname.text.toString()
                    }
                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                Toast.makeText(requireContext(), "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                name = getUserName()
                                rootView.MyPage_title.text = name + "님 환영합니다."
                            }
                        }
                }
            }
            builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }
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