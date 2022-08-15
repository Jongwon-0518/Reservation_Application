package com.example.reservation_system

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUp : AppCompatActivity() {

    val REQUEST_PERMISSIONS_CODE = 99
    @RequiresApi(Build.VERSION_CODES.O)
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.READ_SMS
    )

    // TODO : 아이디 만들고나면 저장된거 지우기
    lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        checkPermissions()

        // realtime database
        database = Firebase.database.reference

        // Firebase
        auth = Firebase.auth

        button_signup_back.setOnClickListener{
            this.finish()
        }

        button_signup_signup.setOnClickListener{
            signUp(it)
        }

        // 버튼 클릭 시 자동으로 가입신청
        edittext_signup_nickname.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                signUp(v)
                true
            }
            else false
        }

        // 전화번호 가져오기
        button_getnumber.setOnClickListener{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var userPhone = tm.line1Number
            // userPhone = userPhone.replace("+82", "0")
            // TODO : 나중에 지우기
            userPhone = userPhone.replace("+1555521555", "")

            Log.d(TAG, "전화번호 : [ getLine1Number ] >>> "+userPhone)
            textview_signup_number.text = userPhone
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }

    // 회원 가입
    // TODO : 사용자 프로필 추가, https://firebase.google.com/docs/auth/android/manage-users?hl=ko, 이메일 인증도 가능
    private fun signUp(v : View?){
        val pw = edittext_signup_pw.text.toString()
        val pwconfirm = edittext_signup_pwconfirm.text.toString()
        val nickname = edittext_signup_nickname.text.toString()
        val phonenumber = textview_signup_number.text.toString()
        val id = phonenumber + "@abc.com"

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)

        Log.d(TAG, pw)
        Log.d(TAG, pwconfirm)
        Log.d(TAG, nickname)
        Log.d(TAG, phonenumber)
        Log.d(TAG, id)

        if (pw != pwconfirm){
            makeToast_short("비밀번호와 확인이 다릅니다.")
            return
        } else if (nickname.length == 0) {
            makeToast_short("닉네임을 설정해주세요.")
            return
        } else if (phonenumber.length == 0) {
            // TODO : 이미 사용된 전화번호에 대한 처리
            makeToast_short("전화번호를 가져와 주세요.")
            return
        }

        if (pw.length > 0){
            auth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = Firebase.auth.currentUser

                        val profileUpdates = userProfileChangeRequest {
                            displayName = nickname
//                            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                            }

                        val result = HashMap<Any, Any>()
                        result["phonenumber"] = phonenumber //키, 값
                        writeUser(phonenumber, "", "")


                        Log.d(TAG, "createUserWithEmail:success")
                        makeToast_short("회원가입에 성공하였습니다.")
                        val intent = Intent(this, Login::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        makeToast_short("이미 있는 아이디거나 확인 사항을 다시 확인해주세요.")
                    }
                }
        } else {
            makeToast_short("비밀번호를 입력해주세요.")
        }
    }

    private fun writeUser(phonenumber : String, makeroom_code : String, reservation_code : String) {
        val user = user_Data(phonenumber, makeroom_code, reservation_code)

        //데이터 저장
        database.child("User").child(phonenumber).setValue(user)
            .addOnSuccessListener(OnSuccessListener<Void?> {
            //데이터베이스에 넘어간 이후 처리
            })
            .addOnFailureListener(OnFailureListener {
            })
    }

    fun makeToast_short(s : String){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    // Permission Check
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            makeToast_short("회원가입을 위해서 다음의 권한들이 필요합니다.")
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), REQUEST_PERMISSIONS_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                            finish()
                        }
                    }
                }
            }
        }
    }
}