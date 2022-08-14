package com.example.reservation_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // defaultvalue main화면 2번
        var screen_location = intent.getIntExtra("location", 2)
        screen_location = intent.getIntExtra("reserved", screen_location)

        view_pager2.post {
            // 키자마자 메인화면 2번
            view_pager2.setCurrentItem(screen_location, true)
        }

        // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
        val viewpagerFragmentAdapter = ViewpagerFragmentAdapter(this)

        // 3) ViewPager2의 adapter에 설정, BottomNavigationView와 연결
        view_pager2.adapter = viewpagerFragmentAdapter
        view_pager2.registerOnPageChangeCallback( PageChangeCallback() )
        bottomNavigationView.setOnNavigationItemSelectedListener { navigationSelected(it) }

        // 사용자 프로필 가져오기
        val user = Firebase.auth.currentUser
        val name = user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val phonenumber = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
            name
        }
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
    }

    // BottomNav Menu
    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.nav_serach -> {
                view_pager2.currentItem = 0
                return true
            }
            R.id.nav_reservation_check -> {
                view_pager2.currentItem = 1
                return true
            }
            R.id.nav_home -> {
                view_pager2.currentItem = 2
                return true
            }
            R.id.nav_create_and_management -> {
                view_pager2.currentItem = 3
                return true
            }
            R.id.nav_my -> {
                view_pager2.currentItem = 4
                return true
            }
        }
        return false
    }

    // BottomNav와 fragment와 연결
    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bottomNavigationView.selectedItemId = when (position) {
                0 -> R.id.nav_serach
                1 -> R.id.nav_reservation_check
                2 -> R.id.nav_home
                3 -> R.id.nav_create_and_management
                4 -> R.id.nav_my
                else -> error("no such position: $position")
            }
        }
    }
}