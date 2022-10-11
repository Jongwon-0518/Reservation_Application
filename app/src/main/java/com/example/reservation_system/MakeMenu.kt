package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_make_menu.*
import kotlinx.android.synthetic.main.activity_menusetting.*
import kotlin.properties.Delegates


class MakeMenu : AppCompatActivity() {

    private lateinit var key: String
    private var menu_list_number by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_menu)

        database = Firebase.database.reference
        val room_code = intent.getIntExtra("room_code", 0)
        val menu_cnt = intent.getIntExtra("menu_cnt", 0)
        val mode = intent.getIntExtra("mode", 0)

        if (mode == 1){

            linearlayout_menu_firstmake.visibility = View.GONE
            linearlayout_menu_update.visibility = View.VISIBLE

            val room_name = intent.getStringExtra("name")
            val cost = intent.getStringExtra("cost")
            val desc = intent.getStringExtra("desc")
            menu_list_number = intent.getIntExtra("menu_list_number", 0)
            val menu_list_number_asLong = menu_list_number.toLong()
            database.child("Menu").child(room_code.toString()).orderByChild("menu_list_number")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        (snapshot.value as HashMap<*, *>).forEach {
                            if ((it.value as HashMap<*, *>)["menu_list_number"] == menu_list_number_asLong){
                                key = it.key.toString()
                                return@forEach
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            editText_MakeMenu_title.setText(room_name)
            editText_MakeMenu_cost.setText(cost)
            editText_MakeMenu_desc.setText(desc)

        }


        button_makemenu_confirm.setOnClickListener {
            val menu_name = editText_MakeMenu_title.text.toString()
            val menu_desc = editText_MakeMenu_desc.text.toString()
            val menu_cost = editText_MakeMenu_cost.text.toString()

            if (menu_name.length == 0){
                makeToast_short("메뉴 이름을 입력해주세요")
            } else {
                //데이터 저장 키 임의값 추가
                val menuRef: DatabaseReference = database.child("Menu").child(room_code.toString())
                menuRef.push().setValue(room_Menu(menu_cnt, menu_name, menu_desc, menu_cost))
                finish()
            }
        }

        button_makemenu_cancel.setOnClickListener {
            finish()
        }

        button_makemenu_save.setOnClickListener {
            // TODO : 업데이트로 구현
            val menu_name = editText_MakeMenu_title.text.toString()
            val menu_desc = editText_MakeMenu_desc.text.toString()
            val menu_cost = editText_MakeMenu_cost.text.toString()
            if (menu_name.length == 0){
                makeToast_short("메뉴 이름을 입력해주세요")
            } else {
                
                val add_menu = room_Menu(menu_list_number, menu_name, menu_desc, menu_cost)
                val MenuRef: DatabaseReference = database.child("Menu").child(room_code.toString())
                val MenuUpdates: MutableMap<String, Any> = HashMap()
                
                // 데이터 추가
                MenuUpdates.put(key, add_menu)
                MenuRef.updateChildren(MenuUpdates)
                finish()
            }
        }

        button_makemenu_delete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("메뉴를 삭제하시겠습니까?")
                .setPositiveButton("네", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        database.child("Menu").child(room_code.toString()).child(key).setValue(null)
                            .addOnSuccessListener(OnSuccessListener<Void?>
                            //데이터베이스에 넘어간 이후 처리
                            { Toast.makeText(applicationContext, "메뉴를 삭제하였습니다.", Toast.LENGTH_SHORT).show() })
                            .addOnFailureListener(OnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "메뉴 삭제에 실패했습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })

                        finish()
                    }
                })
                .setNegativeButton("아니요", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                    }
                })
                .create()
                .show()
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