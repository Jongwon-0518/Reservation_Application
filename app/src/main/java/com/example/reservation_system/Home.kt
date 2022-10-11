package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.room_list.view.*

class Home : Fragment() {

    lateinit var recyclerView1: RecyclerView

    val DataList = ArrayList<room_Data>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        database = Firebase.database.reference

        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView1 = rootView.findViewById(R.id.recyclerView_main)
        // 구분선
        val recyclerViewadapter = HomeRecyclerViewAdapter(DataList)
        recyclerView1.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        recyclerView1.adapter = recyclerViewadapter

        database.child("Room").orderByChild("like")
            .addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key != "number") {
                        val map = snapshot.value as HashMap<*, *>
                        DataList.add(0,
                            room_Data(map["maker"] as String, map["title"] as String, map["information"] as String, (map["code"] as Long).toInt(), map["room_category"] as String, map["location"] as String, (map["like"] as Long).toInt())
                        )
                        recyclerViewadapter.notifyItemInserted(0)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Data Changed
                    if (snapshot.key != "number"){
                        val map = snapshot.value as HashMap<*, *>
                        val code = (map["code"] as Long).toInt()
                        run loop@{
                            var index = 0
                            DataList.forEach{ it ->
                                if (it.code == code) {
                                    DataList[index] = room_Data(map["maker"] as String, map["title"] as String, map["information"] as String, (map["code"] as Long).toInt(), map["room_category"] as String, map["location"] as String, (map["like"] as Long).toInt())
                                    recyclerViewadapter.notifyItemChanged(index)
                                    return@loop
                                }
                                index ++
                            }
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Data Removed
                    val code = ((snapshot.value as HashMap<*, *>)["code"] as Long).toInt()
                    DataList.removeIf{it.code == code}
                    recyclerViewadapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error while createing Home : " + error)
                }

            })

        return rootView
    }

    // 리사이클러 뷰 홀더
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

    // 리사이클러 뷰 어댑터
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

