package com.example.reservation_system

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_reservations.view.*
import kotlinx.android.synthetic.main.reservation_room_list.view.*
import java.text.SimpleDateFormat


class Reservations : Fragment() {

    lateinit var database : DatabaseReference
    lateinit var my_Reservations : RecyclerView
    lateinit var previous_Reservations : RecyclerView

    // TODO : 서버의 데이터베이스와 연결

    val now_My_Reservations = arrayListOf<reservation_Data_withkey>()
    val previous_My_Reservations = arrayListOf<reservation_Data_withkey>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_reservations, container, false)

        database = Firebase.database.reference
        my_Reservations = rootView.reservations_recyclerview
        previous_Reservations = rootView.reservations_recyclerView2

        // Recyclerveiw
        // 구분선
        my_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        val my_Reservations_adapter = RecyclerView_Reservations(now_My_Reservations, 0)
        my_Reservations.adapter = my_Reservations_adapter

        previous_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        val previous_Reservations_adapter = RecyclerView_Reservations(previous_My_Reservations, 1)
        previous_Reservations.adapter = previous_Reservations_adapter

        get_db_reservations(now_My_Reservations, my_Reservations_adapter)
        get_db_prev_reservations(previous_My_Reservations, previous_Reservations_adapter)

        return rootView
    }

    // Reservations RecyclerViewHolder
    inner class RecyclerViewHolder_Reservations(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.reservation_room_title
        val code : TextView = v.reservation_room_code
        val menu : TextView = v.reservation_room_menu
        val time : TextView = v.reservation_room_time

        @SuppressLint("SetTextI18n")
        fun bind(item: reservation_Data_withkey, mode: Int) {
            title.text = item.title
            code.text = item.code.toString()
            menu.text = item.menu
            val m = item.time.slice(0..1)
            val d = item.time.slice(2..3)
            val t = item.time.slice(4..5) + ":" + item.time.slice(6..7)
            time.text = m + "." + d + " " + t

            itemView.setOnClickListener {
                activity?.let {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(title.text.toString() + "의 " + menu.text.toString() + time.text)
                    builder.setMessage("해당 예약을 삭제하시겠습니까?")
                    builder.setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
                        if (mode == 0){
                            database.child("Reservation").child(item.key).setValue(null)
                        } else {
                            database.child("p_Reservation").child(item.key).setValue(null)
                        }
                    }
                    builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }
                    builder.show()
                }
            }
        }
    }


    // Reservations RecyclerView
    inner class RecyclerView_Reservations(val my_reservations_info : ArrayList<reservation_Data_withkey>, val mode : Int) : RecyclerView.Adapter<RecyclerViewHolder_Reservations>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder_Reservations {
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.reservation_room_list, parent, false)
            return RecyclerViewHolder_Reservations(cellForRow)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder_Reservations, position: Int) {
            holder.bind(my_reservations_info[position], mode)
        }

        override fun getItemCount() = my_reservations_info.size
    }


    private fun get_db_reservations(reservations: ArrayList<reservation_Data_withkey>, r_adapter: RecyclerView_Reservations){
        database.child("Reservation").orderByChild("user").equalTo(getUserPhoneNumber())
            .addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as HashMap<*, *>
                    val reservation_time = map["time"] as String
                    if (convertTimestampToDate(System.currentTimeMillis()).toInt() > reservation_time.toInt()){
                        write_prev_reservations(reservation_Data_withkey((map["code"] as Long).toInt(), map["title"] as String, map["menu"] as String, reservation_time, map["user"] as String, snapshot.key.toString()))
                        database.child("Reservation").child(snapshot.key as String).setValue(null)
                        return
                    } else {
                        reservations.add(0,
                            reservation_Data_withkey((map["code"] as Long).toInt(), map["title"] as String, map["menu"] as String, reservation_time, map["user"] as String, snapshot.key.toString())
                        )
                        r_adapter.notifyItemInserted(0)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    reservations.removeIf{it.key == snapshot.key}
                    Toast.makeText(context, "예약이 삭제되었습니다!", Toast.LENGTH_SHORT).show()
                    r_adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error while createing Home : " + error)
                }

            })
    }

    private fun get_db_prev_reservations(reservations : ArrayList<reservation_Data_withkey>, r_adapter : RecyclerView_Reservations){
        database.child("p_Reservation").orderByChild("user").equalTo(getUserPhoneNumber())
            .addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as HashMap<*, *>
                    reservations.add(0,
                        reservation_Data_withkey((map["code"] as Long).toInt(), map["title"] as String, map["menu"] as String, map["time"] as String, map["user"] as String, snapshot.key.toString())
                    )
                    r_adapter.notifyItemInserted(0)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    reservations.removeIf{it.key == snapshot.key}
                    Toast.makeText(context, "예약이 삭제되었습니다!", Toast.LENGTH_SHORT).show()
                    r_adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error while createing Home : " + error)
                }

            })
    }
    @SuppressLint("SimpleDateFormat")
    private fun convertTimestampToDate(timestamp: Long) : String {
        val sdf = SimpleDateFormat("MMddhhmm")
        val date = sdf.format(timestamp)
        return date
    }

    private fun write_prev_reservations(prev_reservation : reservation_Data_withkey){

        database.child("p_Reservation").push().setValue(prev_reservation)
            .addOnSuccessListener {
            }
            .addOnFailureListener{
                Log.e("firebase", "Error while writing reservation")
            }
    }

}