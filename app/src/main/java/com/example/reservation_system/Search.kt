package com.example.reservation_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment

class Search : Fragment() {

    lateinit var searchView : SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_search, container, false)
        searchView = rootView.findViewById(R.id.search_view)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // TODO : Connect With Database
            // 제출 버튼 클릭시
            override fun onQueryTextSubmit(query: String?): Boolean {
                println(query + "완료")
                return true
            }

            // 바뀔때마다
            override fun onQueryTextChange(newText: String?): Boolean {
                println(newText)
                return true
            }
        })

        return rootView
    }
}