package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_createmanage.*


class CreateManage : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_createmanage, container, false)
        make_room_button.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Makeroom::class.java)
                startActivity(intent)
            }
        }
        return view
    }
}