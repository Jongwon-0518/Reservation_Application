package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_reservation_menu.*
import kotlinx.android.synthetic.main.activity_update_log.*
import kotlinx.android.synthetic.main.update_list.view.*
import kotlin.properties.Delegates


class Updatelog: AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var updatelog_RecylerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_log)

        database = Firebase.database.reference
        val Update_list = arrayListOf<update>()

        // RecyclerView
        val adapter = UpdateRecyclerViewAdapter(Update_list)
        updatelog_RecylerView = update_RecylerViewinxml
        updatelog_RecylerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        updatelog_RecylerView.layoutManager = LinearLayoutManager(this)
        updatelog_RecylerView.adapter = adapter

        database.child("Update").orderByChild("date")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: List<String> = snapshot.value as List<String>
                    for (i in 1..list.size-1 step(1)) {
                        val map = list[i] as HashMap<*, *>
                        Update_list.add(0, update((map["date"] as Long).toString(), map["info"] as String))
                        adapter.notifyItemInserted(0)
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

    inner class UpdateRecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val date : TextView = v.update_date
        private val info : TextView = v.update_info

        fun bind(item: update) {
            date.text = item.date
            info.text = item.info

            // TODO : When menu clicked
            itemView.setOnClickListener {

            }

        }
    }

    inner class UpdateRecyclerViewAdapter(val Update_list : ArrayList<update>) : RecyclerView.Adapter<UpdateRecyclerViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateRecyclerViewHolder {
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.update_list, parent, false)
            return UpdateRecyclerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: UpdateRecyclerViewHolder, position: Int) {
            holder.bind(Update_list[position])
        }

        override fun getItemCount() = Update_list.size
    }
}


