package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_createmanage.*
import kotlinx.android.synthetic.main.fragment_createmanage.view.*
import kotlinx.android.synthetic.main.room_list.view.*


//             TODO : 방삭제시, 방 메뉴 시간 등
class CreateManage : Fragment() {

    lateinit var make_room_recylerView : RecyclerView
    private lateinit var database: DatabaseReference

    val DataList = arrayListOf(
        room_Data("010-XXXX-XXXX", "1번방", "1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다. 1번방 설명입니다.", 1, "health"),
        room_Data("010-XXXX-XXXX", "2번방", "2번방 입니다. 2번방 입니다. 2번방 입니다. 2번방 입니다.2번방 입니다. 2번방 입니다.", 2, "health"),
        room_Data("010-XXXX-XXXX", "3번방", "3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요. 3번방 이에요.", 3, "health")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_createmanage, container, false)

        database = Firebase.database.reference
        // Read Database
        // TODO : 이름마다 바꾸기
        database.child("User").child("4").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val userInformation = it.value
            val makeroom_codes = (userInformation as HashMap<*, *>)["makeroom_code"]
            Log.i("firebase", "makeroom_codes $makeroom_codes")

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        make_room_recylerView = rootView.recyclerView_createmanage
        // 구분선
        make_room_recylerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        make_room_recylerView.layoutManager = LinearLayoutManager(requireContext())
        make_room_recylerView.adapter = HomeRecyclerViewAdapter(DataList)

        rootView.make_room_button.setOnClickListener{
            activity?.let {
                val intent = Intent(context, Makeroom::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }
        }
        return rootView
    }

    inner class HomeRecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.room_tittle
        val information : TextView = v.room_info
        val code : TextView = v.room_code

        fun bind(item: room_Data) {
            title.text = item.title
            information.text = item.information
            code.text = item.code.toString()

            itemView.setOnClickListener {
                activity?.let {
                    val intent = Intent(context, RoomInformation::class.java)
                    intent.putExtra("code", item.code)
                    startActivity(intent)
                    activity!!.overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                }
            }
        }
    }

    inner class HomeRecyclerViewAdapter(val room_data_list : ArrayList<room_Data>) : RecyclerView.Adapter<HomeRecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
            // 0, 1, 2 parameter -> 0 : view로 팽창할 것, 1 : 팽창해서 recyclerView(parent)에 표시, 2 : false
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.room_list, parent, false)
            return HomeRecyclerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
            holder.bind(room_data_list[position])
        }

        override fun getItemCount() = room_data_list.size
    }
}