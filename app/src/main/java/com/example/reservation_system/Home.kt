package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.room_list.view.*

class Home : Fragment() {

    lateinit var recyclerView1: RecyclerView

    // TODO : 서버의 데이터베이스와 연결
    val DataList = arrayListOf(
        room_Data("1번방", "1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다.", 1),
        room_Data("2번방", "2번방 입니다. 2번방 입니다. 2번방 입니다. 2번방 입니다.2번방 입니다. 2번방 입니다.", 2),
        room_Data("3번방", "3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요.", 3),
        room_Data("4번방", "4번방 입니다. 4번방 입니다. 4번방 입니다. 4번방 입니다. 4번방 입니다. 4번방 입니다.", 4),
        room_Data("5번방", "5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방 5번방", 5),
        room_Data("6번방", "6번방 입니다. 6번방 입니다. 6번방 입니다. 6번방 입니다. 6번방 입니다. 6번방 입니다.", 6),
        room_Data("7번방", "7번방의 선물 7번방의 선물 7번방의 선물 7번방의 선물 7번방의 선물 7번방의 선물 7번방의 선물", 7),
        room_Data("8번방", "8입니당~~ 8입니당~~ 8입니당~~ 8입니당~~ 8입니당~~ 8입니당~~ 8입니당~~ 8입니당~~", 8)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var rootView = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView1 = rootView.findViewById(R.id.recyclerView_main)
        // 구분선
        recyclerView1.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = HomeRecyclerViewAdapter(DataList)

        return rootView
    }

    // 리사이클러 뷰 홀더
    inner class HomeRecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.room_tittle
        val information : TextView = v.room_info
        val code : TextView = v.room_code
    }

    // 리사이클러 뷰 어댑터
    inner class HomeRecyclerViewAdapter(val room_data_list : ArrayList<room_Data>) : RecyclerView.Adapter<HomeRecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
            // 0, 1, 2 parameter -> 0 : view로 팽창할 것, 1 : 팽창해서 recyclerView(parent)에 표시, 2 : false
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.room_list, parent, false)
            return HomeRecyclerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
            holder.title.text = room_data_list[position].title
            holder.information.text = room_data_list[position].information
            holder.code.text = room_data_list[position].code.toString()
        }

        override fun getItemCount() = room_data_list.size
    }
}

