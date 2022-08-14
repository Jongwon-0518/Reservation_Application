package com.example.reservation_system

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
// 서버에서 가져올 title, information 데이터들을 room_Data type으로 만들고 Arraylist에 담음
class room_Data(room_title : String, room_info : String, room_code : Int, room_category : String){
    var title = room_title
    var information = room_info
    var code = room_code
    var room_category = room_category
}


class User {
    var name: String? = null
    var email: String? = null
    var age: String? = null

    constructor() {}
    constructor(name: String?, email: String?, age: String?) {
        this.name = name
        this.email = email
        this.age = age
    }

    override fun toString(): String {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}'
    }
}