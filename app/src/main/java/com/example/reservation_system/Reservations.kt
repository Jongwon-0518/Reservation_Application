package com.example.reservation_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_reservations.view.*
import kotlinx.android.synthetic.main.room_list.view.*


class Reservations : Fragment() {

    lateinit var my_Reservations : RecyclerView
    lateinit var previous_Reservations : RecyclerView

    // TODO : 서버의 데이터베이스와 연결
    val now_My_Reservations = arrayListOf(
        room_Data("1번방", "1번방 설명입니다."),
        room_Data("2번방", "2번방 입니다."),
        room_Data("3번방", "3번방 이에요."),
        room_Data("4번방", "4번방 입니다."),
    )
    val previous_My_Reservations = arrayListOf(
        room_Data("5번방", "5번방"),
        room_Data("6번방", "6번방"),
        room_Data("7번방", "7번방의 선물"),
        room_Data("8번방", "8입니당")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_reservations, container, false)
        my_Reservations = rootView.reservations_recyclerview
        previous_Reservations = rootView.reservations_recyclerView2

        // 구분선
        my_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        my_Reservations.adapter = RecyclerView_Reservations(now_My_Reservations)

        previous_Reservations.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        previous_Reservations.adapter = RecyclerView_Reservations(previous_My_Reservations)

        return rootView
    }

    // Reservations RecyclerViewHolder
    inner class RecyclerViewHolder_Reservations(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.room_tittle
        val information : TextView = v.room_info
    }

    // Reservations RecyclerView
    inner class RecyclerView_Reservations(val my_reservations_info : ArrayList<room_Data>) : RecyclerView.Adapter<RecyclerViewHolder_Reservations>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder_Reservations {
            // TODO : inflate할 layout변경
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.room_list, parent, false)
            return RecyclerViewHolder_Reservations(cellForRow)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder_Reservations, position: Int) {
            holder.title.text = my_reservations_info[position].title
            holder.information.text = my_reservations_info[position].information
        }

        override fun getItemCount() = my_reservations_info.size
    }
}