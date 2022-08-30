package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_createmanage.*
import kotlinx.android.synthetic.main.fragment_createmanage.view.*
import kotlinx.android.synthetic.main.room_list.view.*


//             TODO : 메뉴 지울 때, 메뉴 edit, recyclerView에서 이동이 menu_list_number에 반영
class CreateManage : Fragment() {

    private lateinit var make_room_recylerView : RecyclerView
    private lateinit var database: DatabaseReference

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_createmanage, container, false)
        val DataList = arrayListOf<room_Data>()
        val adapter = HomeRecyclerViewAdapter(DataList)
        var room_cnt = 0

        database = Firebase.database.reference

        // RecyclerView
        make_room_recylerView = rootView.recyclerView_createmanage
        // 구분선
        make_room_recylerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        make_room_recylerView.layoutManager = LinearLayoutManager(requireContext())
        make_room_recylerView.adapter = adapter
        rootView.make_room_button.setOnClickListener{
            activity?.let {
                val intent = Intent(context, Makeroom::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }
        }

        database.child("Room").orderByChild("maker").equalTo(getUserPhoneNumber())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as HashMap<*, *>
                    DataList.add(
                        room_Data(map["maker"] as String, map["title"] as String, map["information"] as String, (map["code"] as Long).toInt(), map["room_category"] as String, (map["like"] as Long).toInt())
                    )
                    adapter.notifyItemInserted(room_cnt)
                    room_cnt++
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Data Changed
                    val map = snapshot.value as HashMap<*, *>
                    val code = (map["code"] as Long).toInt()
                    run loop@{
                        var index = 0
                        DataList.forEach{ it ->
                            if (it.code == code) {
                                DataList[index] = room_Data(map["maker"] as String, map["title"] as String, map["information"] as String, (map["code"] as Long).toInt(), map["room_category"] as String, (map["like"] as Long).toInt())
                                adapter.notifyItemChanged(index)
                                return@loop
                            }
                            index ++
                        }
                    }
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Data Removed
                    val code = ((snapshot.value as HashMap<*, *>)["code"] as Long).toInt()
                    DataList.removeIf{it.code == code}
                    adapter.notifyDataSetChanged()
                    room_cnt--
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

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
                    val intent = Intent(context, EditRoom::class.java)
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