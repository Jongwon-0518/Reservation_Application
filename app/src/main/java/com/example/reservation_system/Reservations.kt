package com.example.reservation_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_reservations.view.*
import kotlinx.android.synthetic.main.reservation_room_list.view.*


class Reservations : Fragment() {

    lateinit var my_Reservations : RecyclerView
    lateinit var previous_Reservations : RecyclerView

    // TODO : 서버의 데이터베이스와 연결
    val now_My_Reservations = arrayListOf(
        room_Data("010-XXXX-XXXX", "1번방", "1번방 설명입니다.", 1, "health", ""),
        room_Data("010-XXXX-XXXX", "2번방", "2번방 입니다.", 2, "health", ""),
        room_Data("010-XXXX-XXXX", "3번방", "3번방 이에요.", 3, "health", ""),
        room_Data("010-XXXX-XXXX", "4번방", "4번방 입니다.", 4, "health", ""),
    )
    val previous_My_Reservations = arrayListOf(
        room_Data("010-XXXX-XXXX", "5번방", "5번방", 5, "health", ""),
        room_Data("010-XXXX-XXXX", "6번방", "6번방", 6, "health", ""),
        room_Data("010-XXXX-XXXX", "7번방", "7번방의 선물", 7, "health", ""),
        room_Data("010-XXXX-XXXX", "8번방", "8입니당", 8, "health", "")
    )

    val now_My_Reservations_time = arrayListOf(
        reservation_Data(1, "파마", 200, 20220202, 1300),
        reservation_Data(1, "pt", 100, 20220202, 1500),
        reservation_Data(1, "커트", 100, 20220202, 900),
    )
    val previous_My_Reservations_time = arrayListOf(
        reservation_Data(1, "파마", 200, 20220202, 1300),
        reservation_Data(1, "pt", 100, 20220202, 1500),
        reservation_Data(1, "커트", 100, 20220202, 900),
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_reservations, container, false)
        my_Reservations = rootView.reservations_recyclerview
        previous_Reservations = rootView.reservations_recyclerView2

        // Recyclerveiw
        // 구분선
        my_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        my_Reservations.adapter = RecyclerView_Reservations(now_My_Reservations)

        previous_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        previous_Reservations.adapter = RecyclerView_Reservations(previous_My_Reservations)

        return rootView
    }

    // Reservations RecyclerViewHolder
    inner class RecyclerViewHolder_Reservations(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.reservation_room_title
        val code : TextView = v.reservation_room_code

        fun bind(item: room_Data) {
            title.text = item.title
            code.text = item.code.toString()

            itemView.setOnClickListener {
                activity?.let {
                    val myReservationDialog = MyReservationImfo(requireContext())
                    myReservationDialog.show()
                }
            }
        }
    }



    // Reservations RecyclerView
    inner class RecyclerView_Reservations(val my_reservations_info : ArrayList<room_Data>) : RecyclerView.Adapter<RecyclerViewHolder_Reservations>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder_Reservations {
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.reservation_room_list, parent, false)
            return RecyclerViewHolder_Reservations(cellForRow)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder_Reservations, position: Int) {
            holder.bind(my_reservations_info[position])
        }

        override fun getItemCount() = my_reservations_info.size
    }
}