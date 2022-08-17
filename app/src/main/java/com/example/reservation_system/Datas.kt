package com.example.reservation_system

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*
import kotlin.collections.ArrayList


@IgnoreExtraProperties
// 서버에서 가져올 title, information 데이터들을 room_Data type으로 만들고 Arraylist에 담음
class room_Data(room_maker: String, room_title: String, room_info: String, room_code: Int, room_category: String){
    var maker = room_maker
    var title = room_title
    var information = room_info
    var code = room_code
    var room_category = room_category
}

class room_Number(number : Int){
    var number = number
}

class room_reservation_list(arr : ArrayList<Int>){
    var room_numbers = arr
}

class user_Data(
    var phonenumber: String,
    var reservation_code: String
)

class makeroom_Codes(
    var makeroom_codes: Int
)
