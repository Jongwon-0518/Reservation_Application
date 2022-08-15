package com.example.reservation_system

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.make_room.*


class Makeroom : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference

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
                mapview.visibility = View.VISIBLE
            }else{
                mapview.visibility = View.GONE
            }
        }

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Make_room_complete.setOnClickListener{
            val getRoomTitle: String = room_title_make.text.toString()
            val getRoomCategory: String = Category_make.text.toString()
            val getRoomInformation: String = room_info_make.text.toString()

            val result = HashMap<Any, Any>()
            result["title"] = getRoomTitle //키, 값
            result["room_category"] = getRoomCategory
            result["information"] = getRoomInformation

            database.child("Room").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val roomIdmax = dataSnapshot.childrenCount
                    val newroomId = roomIdmax + 1
                    writeRoom(newroomId.toString(), getRoomTitle, getRoomCategory, getRoomInformation)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            this.finish()
        }
    }

    private fun writeRoom(roomId: String, title: String, room_category: String, information: String) {
        val room = room_Data(title, information, roomId.toInt(), room_category)

        //데이터 저장
        database.child("Room").child(roomId).setValue(room)
            .addOnSuccessListener(OnSuccessListener<Void?>
            //데이터베이스에 넘어간 이후 처리
            { Toast.makeText(applicationContext, "저장을 완료했습니다", Toast.LENGTH_LONG).show() })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "저장에 실패했습니다",
                    Toast.LENGTH_LONG
                ).show()
            })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val marker = LatLng(35.241615, 128.695587)
        mMap.addMarker(MarkerOptions().position(marker).title("마커 제목"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}