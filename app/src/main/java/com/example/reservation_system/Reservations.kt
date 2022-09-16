package com.example.reservation_system

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
//    val now_My_Reservations = arrayListOf(
//        room_Data("010-XXXX-XXXX", "1번방", "1번방 설명입니다.", 1, "health", ""),
//        room_Data("010-XXXX-XXXX", "2번방", "2번방 입니다.", 2, "health", ""),
//        room_Data("010-XXXX-XXXX", "3번방", "3번방 이에요.", 3, "health", ""),
//        room_Data("010-XXXX-XXXX", "4번방", "4번방 입니다.", 4, "health", ""),
//    )
//    val previous_My_Reservations = arrayListOf(
//        room_Data("010-XXXX-XXXX", "5번방", "5번방", 5, "health", ""),
//        room_Data("010-XXXX-XXXX", "6번방", "6번방", 6, "health", ""),
//        room_Data("010-XXXX-XXXX", "7번방", "7번방의 선물", 7, "health", ""),
//        room_Data("010-XXXX-XXXX", "8번방", "8입니당", 8, "health", "")
//    )

    val now_My_Reservations = arrayListOf<reservation_Data>()
    val previous_My_Reservations = arrayListOf<reservation_Data>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_reservations, container, false)

        database = Firebase.database.reference
        my_Reservations = rootView.reservations_recyclerview
        previous_Reservations = rootView.reservations_recyclerView2

        // Recyclerveiw
        // 구분선
        my_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        val my_Reservations_adapter = RecyclerView_Reservations(now_My_Reservations)
        my_Reservations.adapter = my_Reservations_adapter

        previous_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        val previous_Reservations_adapter = RecyclerView_Reservations(now_My_Reservations)
        previous_Reservations.adapter = previous_Reservations_adapter

        get_db_reservations(now_My_Reservations, my_Reservations_adapter, 0)
        get_db_reservations(previous_My_Reservations, previous_Reservations_adapter, 1)

        return rootView
    }

    // Reservations RecyclerViewHolder
    inner class RecyclerViewHolder_Reservations(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.reservation_room_title
        val code : TextView = v.reservation_room_code
        val menu : TextView = v.reservation_room_menu
        val time : TextView = v.reservation_room_time

        @SuppressLint("SetTextI18n")
        fun bind(item: reservation_Data) {
            title.text = item.title
            code.text = item.code.toString()
            menu.text = item.menu
            val m = item.time.slice(0..1)
            val d = item.time.slice(2..3)
            val t = item.time.slice(4..5) + ":" + item.time.slice(6..7)
            time.text = m + "." + d + " " + t

            itemView.setOnClickListener {
                activity?.let {
                    val myReservationDialog = MyReservationImfo(requireContext())
                    myReservationDialog.show()
                }
            }
        }
    }


    // Reservations RecyclerView
    inner class RecyclerView_Reservations(val my_reservations_info : ArrayList<reservation_Data>) : RecyclerView.Adapter<RecyclerViewHolder_Reservations>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder_Reservations {
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.reservation_room_list, parent, false)
            return RecyclerViewHolder_Reservations(cellForRow)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder_Reservations, position: Int) {
            holder.bind(my_reservations_info[position])
        }

        override fun getItemCount() = my_reservations_info.size
    }

    private fun get_db_reservations(reservations : ArrayList<reservation_Data>, r_adapter : RecyclerView_Reservations, mode : Int){
        var reservation_type = "Reservation"
        if (mode == 1){
            reservation_type = "p_Reservation"
        }
        database.child(reservation_type).orderByChild("user").equalTo(getUserPhoneNumber())
            .addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as HashMap<*, *>
                    val reservation_time = map["time"] as String
                    if (mode == 0){
                        if (convertTimestampToDate(System.currentTimeMillis()).toInt() > reservation_time.toInt()){
                            // TODO : prev에 데이터 추가, 원래꺼에 삭제
                            return
                        }
                    }
                    val time_format = reservation_time.slice(0..5) + ":" + reservation_time.slice(6..7)
                    reservations.add(0,
                        reservation_Data((map["code"] as Long).toInt(), map["title"] as String, map["menu"] as String, time_format, map["user"] as String)
                    )
                    r_adapter.notifyItemInserted(0)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val map = snapshot.value
                    reservations.removeIf{it == map}
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
}