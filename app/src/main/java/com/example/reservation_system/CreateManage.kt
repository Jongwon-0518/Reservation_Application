package com.example.reservation_system

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_createmanage.*
import kotlinx.android.synthetic.main.fragment_createmanage.view.*


class CreateManage : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_createmanage, container, false)

        rootView.make_room_button.setOnClickListener{
            activity?.let {
                val intent = Intent(context, Makeroom::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }
        }
        return rootView
    }
}