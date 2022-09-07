package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menusetting.*
import kotlinx.android.synthetic.main.activity_reservation_menu.*
import kotlinx.android.synthetic.main.menu_list.view.*
import kotlin.properties.Delegates

class ReservationMenu : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var menu_RecylerView : RecyclerView
    private var room_code by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_menu)

        database = Firebase.database.reference
        room_code = intent.getIntExtra("room_code", 0)
        val Menu_list = arrayListOf<room_Menu>()

        // RecyclerView
        val adapter = HomeRecyclerViewAdapter(Menu_list)
        menu_RecylerView = recyclerView_reservationmenu
        menu_RecylerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        menu_RecylerView.layoutManager = LinearLayoutManager(this)
        menu_RecylerView.adapter = adapter

        database.child("Menu").child(room_code.toString()).orderByChild("menu_list_number")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 도중 삭제 가능
                    if (snapshot.value != null){
                        (snapshot.value as HashMap<*, *>).forEach { snap ->
                            val map = snap.value as HashMap<*, *>
                            Menu_list.add(room_Menu((map["menu_list_number"] as Long).toInt(), map["menu_name"].toString(), map["menu_description"].toString(), map["menu_cost"].toString()))
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

    inner class MenuRecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val name : TextView = v.textView_menu_name
        private val cost : TextView = v.textView_menu_cost
        private val desc : TextView = v.textView_menu_desc

        fun bind(item: room_Menu) {
            name.text = item.menu_name
            cost.text = item.menu_cost
            desc.text = item.menu_description

            // TODO : When menu clicked
            itemView.setOnClickListener {
                val intent = Intent(applicationContext, MakeReservation::class.java)
                intent.putExtra("menu_list_number", item.menu_list_number)
                intent.putExtra("room_code", room_code)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
            }
        }
    }

    inner class HomeRecyclerViewAdapter(val menu_list : ArrayList<room_Menu>) : RecyclerView.Adapter<MenuRecyclerViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuRecyclerViewHolder {
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.menu_list, parent, false)
            return MenuRecyclerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: MenuRecyclerViewHolder, position: Int) {
            holder.bind(menu_list[position])
        }

        override fun getItemCount() = menu_list.size
    }
}