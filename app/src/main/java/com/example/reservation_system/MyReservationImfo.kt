package com.example.reservation_system

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

import kotlinx.android.synthetic.main.activity_my_reservation_information.*

class MyReservationImfo(context: Context) : BottomSheetDialog(context) {
    init {
        //R.layout.confirm_bottom_dialog 하단 다이어로그 생성 버튼을 눌렀을 때 보여질 레이아웃
        val view: View = layoutInflater.inflate(R.layout.activity_my_reservation_information, null)
        setContentView(view)

        //확인 버튼
        imageButton_myresrvation_back.setOnClickListener {
            //다이어 로그 숨기기
            dismiss()
        }

        button_reservation_cancel.setOnClickListener{
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("예약 취소")
                .setMessage("정말로 취소하시겠습니까?")
                .setPositiveButton("확인"){
                    dialogInterface: DialogInterface, i:Int ->
                    // TODO : Cancel Reservation in Firebase
                    Toast.makeText(this.context, "취소 완료!", Toast.LENGTH_SHORT).show()
                    dismiss()
//                    val intent = Intent(this.context, MainActivity::class.java)
//                    startActivity(intent)
                }
                .setNegativeButton("취소", null)
            // Show Dialog
            builder.show()
        }
    }
}
