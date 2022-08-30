package com.example.reservation_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_make_menu.*

class MakeMenu : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_menu)

        database = Firebase.database.reference
        val room_code = intent.getIntExtra("room_code", 0)
        val menu_cnt = intent.getIntExtra("menu_cnt", 0)

        button_makemenu_confirm.setOnClickListener {
            val menu_name = editText_MakeMenu_title.text.toString()
            val menu_desc = editText_MakeMenu_desc.text.toString()
            val menu_cost = editText_MakeMenu_cost.text.toString()

            if (menu_name.length == 0){
                makeToast_short("메뉴 이름을 입력해주세요")
            } else {
                val add_menu = room_Menu_database(menu_cnt, menu_desc, menu_cost)
                //데이터 저장
                database.child("Menu").child(room_code.toString()).child(menu_name).setValue(add_menu)
                    .addOnSuccessListener(OnSuccessListener<Void?>
                    //데이터베이스에 넘어간 이후 처리
                    {
                        Toast.makeText(this, "메뉴를 추가하였습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    })
                    .addOnFailureListener(OnFailureListener {
                        Toast.makeText(this, "메뉴 추가 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                    })
            }
        }

        button_makemenu_cancel.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

    fun makeToast_short(s : String){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}