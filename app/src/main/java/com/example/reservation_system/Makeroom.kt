package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.edit_room.*
import kotlinx.android.synthetic.main.make_room.*


class Makeroom : AppCompatActivity() {

    private val SEARCH_ADDRESS_ACTIVITY = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_room)

        database = Firebase.database.reference

        Private_checkbox.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                Private_password.visibility = View.VISIBLE
            }else{
                Private_password.visibility = View.GONE
            }
        }

        Location_checkbox.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                address_edittext.visibility = View.VISIBLE
                detail_address_edittext.visibility = View.VISIBLE
            }else{
                address_edittext.visibility = View.GONE
                detail_address_edittext.visibility = View.GONE
            }
        }

        address_edittext.setOnClickListener{

            val intent = Intent(this, AddressSearch::class.java)
            startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY)

        }

        Make_room_complete.setOnClickListener{
            val getRoomTitle: String = room_title_make.text.toString()
            val getRoomCategory: String = Category_make.text.toString()
            val getRoomInformation: String = room_info_make.text.toString()
            val getMaker: String = getUserPhoneNumber()
            val location : String = if (Location_checkbox.isChecked && address_edittext.text.toString() != "") {
                address_edittext.text.toString() + " " + detail_address_edittext.text.toString()
            } else {
                "비공개"
            }
            var roomId: String
            // Read Database
            database.child("Room").child("number").get().addOnSuccessListener {
                roomId = if (it.value != null) (it.value as HashMap<*, *>)["number"].toString() else "1"
                writeRoom(getMaker, roomId, getRoomTitle, getRoomCategory, getRoomInformation, location)
                writeRoomNumber(roomId.toInt() + 1)

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

            this.finish()
        }
    }

    private fun writeRoom(room_maker: String, roomId: String, title: String, room_category: String, information: String, location: String) {
        val room = room_Data(room_maker, title, information, roomId.toInt(), room_category, location)

        //데이터 저장
        database.child("Room").child(roomId).setValue(room)
            .addOnSuccessListener(OnSuccessListener<Void?>
            //데이터베이스에 넘어간 이후 처리
            {
                // 성공시 요일 기본 Form 추가
                Toast.makeText(applicationContext, "저장을 완료했습니다", Toast.LENGTH_SHORT).show()
                val taskMap = HashMap<String, Any>()
                val days = ArrayList<ArrayList<String>>()
                for (i in 0..6) {
                    days.add(arrayListOf("", ""))
                }
                taskMap.put("요일", days)
                database.child("Room").child(roomId).updateChildren(taskMap)
                database.child("Room").child(roomId).updateChildren(mapOf("나눈시간" to 30))
            })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "저장에 실패했습니다",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun writeRoomNumber(num : Int) {

        val newroomId = room_Number(num)
        database.child("Room").child("number").setValue(newroomId)
            .addOnSuccessListener(OnSuccessListener<Void?>
            //데이터베이스에 넘어간 이후 처리
            { })
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
                        address_edittext.setText(addressData)
                    }
                }
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}