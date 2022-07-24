package com.example.reservation_system
// 서버에서 가져올 title, information 데이터들을 room_Data type으로 만들고 Arraylist에 담음
class room_Data(room_title : String, room_info : String, room_code : Int){
    var title = room_title
    var information = room_info
    var code = room_code
}