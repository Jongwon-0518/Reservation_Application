package com.example.reservation_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menusetting.*
import kotlinx.android.synthetic.main.menu_list.view.*
import kotlinx.android.synthetic.main.room_list.view.*
import kotlin.properties.Delegates

class Menusetting : AppCompatActivity() {

    private lateinit var menu_RecylerView : RecyclerView
    private lateinit var database: DatabaseReference
    private var room_code by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menusetting)

        database = Firebase.database.reference
        room_code = intent.getIntExtra("code", 0)
        val Menu_list = arrayListOf<room_Menu>()
        var menu_cnt = 0

        // RecyclerView
        val adapter = HomeRecyclerViewAdapter(Menu_list)
        menu_RecylerView = recyclerView_Menu
        menu_RecylerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        menu_RecylerView.layoutManager = LinearLayoutManager(this)
        menu_RecylerView.adapter = adapter

        // ItemTouchHelper
        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        val helper = ItemTouchHelper(itemTouchHelperCallback)
        helper.attachToRecyclerView(menu_RecylerView)

        imageButton_menusetting_back.setOnClickListener{
            finish()
        }

        val intent = Intent(this, MakeMenu::class.java)

        make_menu_button.setOnClickListener {
            intent.putExtra("menu_cnt", menu_cnt)
            intent.putExtra("room_code", room_code)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }

        database.child("Menu").child(room_code.toString()).orderByChild("menu_list_number")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val map = snapshot.value as HashMap<*, *>
                    Menu_list.add(
                        room_Menu((map["menu_list_number"] as Long).toInt(), map["menu_name"].toString(), map["menu_description"].toString(), map["menu_cost"].toString())
                    )
                    adapter.notifyItemInserted(menu_cnt)
                    menu_cnt++
                    if (menu_cnt > 0 && textView_pleaseaddmenu.visibility == View.VISIBLE) textView_pleaseaddmenu.visibility = View.GONE
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Data Changed
                    val map = snapshot.value as HashMap<*, *>
                    val menu_number = (map["menu_list_number"] as Long).toInt()
                    run loop@{
                        var index = 0
                        Menu_list.forEach{ it ->
                            if (it.menu_list_number == menu_number) {
                                Menu_list[index] = room_Menu((map["menu_list_number"] as Long).toInt(), map["menu_name"].toString(), map["menu_description"].toString(), map["menu_cost"].toString())
                                adapter.notifyItemChanged(index)
                                return@loop
                            }
                            index ++
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Data Removed
                    val erase_number = ((snapshot.value as HashMap<*, *>)["menu_list_number"] as Long).toInt()
                    Menu_list.removeIf{it.menu_list_number == erase_number}
                    adapter.notifyDataSetChanged()
                    menu_cnt--
                    if (menu_cnt == 0) textView_pleaseaddmenu.visibility = View.VISIBLE
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
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
                    val intent = Intent(applicationContext, MakeMenu::class.java)
                    intent.putExtra("mode", 1)
                    intent.putExtra("name", item.menu_name)
                    intent.putExtra("cost", item.menu_cost)
                    intent.putExtra("desc", item.menu_description)
                    intent.putExtra("menu_list_number", item.menu_list_number)
                    intent.putExtra("room_code", room_code)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }
        }
    }

    inner class HomeRecyclerViewAdapter(val menu_list : ArrayList<room_Menu>) : RecyclerView.Adapter<MenuRecyclerViewHolder>(), ItemTouchHelperListener {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuRecyclerViewHolder {
            // 0, 1, 2 parameter -> 0 : view로 팽창할 것, 1 : 팽창해서 recyclerView(parent)에 표시, 2 : false
            val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.menu_list, parent, false)
            return MenuRecyclerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: MenuRecyclerViewHolder, position: Int) {
            holder.bind(menu_list[position])
        }

        override fun getItemCount() = menu_list.size

        // 아이템을 드래그되면 호출되는 메소드
        override fun onItemMove(from_position: Int, to_position: Int): Boolean {
            val name = menu_list[from_position]
            // 리스트 갱신
            menu_list.removeAt(from_position)
            menu_list.add(to_position, name)

            // fromPosition에서 toPosition으로 아이템 이동 공지
            notifyItemMoved(from_position, to_position)
            return true
        }

        // 아이템 스와이프되면 호출되는 메소드
        override fun onItemSwipe(position: Int) {
            // 리스트 아이템 삭제
            menu_list.removeAt(position)
            // 아이템 삭제되었다고 공지
            notifyItemRemoved(position)
        }
    }
}