package com.example.reservation_system

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.edit_room.*
import kotlinx.android.synthetic.main.make_room.*
import kotlinx.android.synthetic.main.room_information.*


class EditRoom : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val SEARCH_ADDRESS_ACTIVITY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_room)

        database = Firebase.database.reference

        val User_number = getUserPhoneNumber()

        val room_number = intent.getIntExtra("code", 0)
        Log.d("-----------", room_number.toString())

        database.child("Room").child(room_number.toString()).get().addOnSuccessListener {
            val room_title = (it.value as HashMap<*, *>)["title"].toString()
            val information = (it.value as HashMap<*, *>)["information"].toString()
            val room_category = (it.value as HashMap<*, *>)["room_category"].toString()
            val location = (it.value as HashMap<*, *>)["location"].toString()

            if (location == "") {
                Location_checkbox_edit.setChecked(false)
            } else {
                Location_checkbox_edit.setChecked(true)
                address_edittext_edit.hint = location
            }
            room_title_edit.hint = room_title
            Category_edit.hint = room_category
            room_info_edit.hint = information

        }.addOnFailureListener{
            Log.e("-----------------------", "Error getting data", it)
        }

        Location_checkbox_edit.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                address_edittext_edit.visibility = View.VISIBLE
                detail_address_edittext_edit.visibility = View.VISIBLE
            }else{
                address_edittext_edit.visibility = View.GONE
                detail_address_edittext_edit.visibility = View.GONE
            }
        }

        address_edittext_edit.setOnClickListener{
            val intent = Intent(this, AddressSearch::class.java)
            startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY)

        }

        time_setting_button.setOnClickListener{
            val intent = Intent(this, Timesetting::class.java)
            intent.putExtra("room_code", room_number)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }

        edit_save_button.setOnClickListener{
            val getMaker: String = User_number
            val getRoomTitle : String = if (room_title_edit.text.toString().trim().isEmpty()) {
                room_title_edit.hint as String
            } else {
                room_title_edit.text.toString()
            }
            val getRoomCategory : String = if (Category_edit.text.toString().trim().isEmpty()) {
                Category_edit.hint as String
            } else {
                Category_edit.text.toString()
            }
            val getRoomInformation : String = if (room_info_edit.text.toString().trim().isEmpty()) {
                room_info_edit.hint as String
            } else {
                room_info_edit.text.toString()
            }
            val getlocation : String = if (Location_checkbox_edit.isChecked) {
                address_edittext_edit.text.toString() + detail_address_edittext_edit.text.toString()
            } else {
                ""
            }
            // Read Database
            writeRoom(getMaker, room_number.toString(), getRoomTitle, getRoomCategory, getRoomInformation, getlocation.toString())

            this.finish()
        }

        delete_button.setOnClickListener{

            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("삭제하시겠습니까??")
                .setPositiveButton("네", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "positive")
                        database.child("Room").child(room_number.toString()).setValue(null)
                            .addOnSuccessListener(OnSuccessListener<Void?>
                            //데이터베이스에 넘어간 이후 처리
                            { Toast.makeText(applicationContext, "삭제를 완료했습니다", Toast.LENGTH_SHORT).show() })
                            .addOnFailureListener(OnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "삭제에 실패했습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })

                        this.finish()
                    }
                })
                .setNegativeButton("아니요", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")

                    }
                })
                .create()
                .show()
        }

        menu_setting_button.setOnClickListener {
            val intent = Intent(this, Menusetting::class.java)
            intent.putExtra("code", room_number)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }
    }

    private fun writeRoom(room_maker: String, roomId: String, title: String, room_category: String, information: String, location: String) {
        val room = room_Data(room_maker, title, information, roomId.toInt(), room_category, location)

        //데이터 저장
        database.child("Room").child(roomId).setValue(room)
            .addOnSuccessListener(OnSuccessListener<Void?>
            //데이터베이스에 넘어간 이후 처리
            { Toast.makeText(applicationContext, "저장을 완료했습니다", Toast.LENGTH_SHORT).show() })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "저장에 실패했습니다",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (requestCode) {
            SEARCH_ADDRESS_ACTIVITY -> {
                if (resultCode == RESULT_OK) {
                    // 주소를 가져와서 보여주는 부분
                    val addressData = intent?.extras?.getString("data")
                    if (addressData != null) {
                        address_edittext_edit.setText(addressData)
                    }
                }
            }
        }
    }

    private fun DialogInterface.OnClickListener.finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}



