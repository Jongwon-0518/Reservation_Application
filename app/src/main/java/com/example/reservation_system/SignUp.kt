package com.example.reservation_system

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        checkPermissions()

        // Firebase
        auth = Firebase.auth

        button_signup_back.setOnClickListener{
            this.finish()
        }

        button_signup_signup.setOnClickListener{
            signUp()
        }

        edittext_signup_pwconfirm.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                signUp()
                true
            }
            else false
        }

        // 전화번호 가져오기
        test.setOnClickListener{

            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            var userPhone = tm.line1Number
            userPhone = userPhone.replace("+82", "0")
            Log.d(TAG, "전화번호 : [ getLine1Number ] >>> "+userPhone)
            textview_signup_number.setText(userPhone)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
    }

    // 회원 가입
    // TODO : 사용자 프로필 추가, https://firebase.google.com/docs/auth/android/manage-users?hl=ko, 이메일 인증도 가능
    private fun signUp(){
        val id = edittext_signup_id.text.toString()
        val pw = edittext_signup_pw.text.toString()
        val pwconfirm = edittext_signup_pwconfirm.text.toString()
        Log.d(TAG, id)
        Log.d(TAG, pw)
        Log.d(TAG, pwconfirm)

        if (pw != pwconfirm){
            makeToast_short("비밀번호와 확인이 다릅니다.")
            return
        }

        if (id.length > 0 && pw.length > 0){
            auth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        makeToast_short("회원가입에 성공하였습니다.")
                        val user = auth.currentUser
                        val intent = Intent(this, Login::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
                    } else {
                        // If sign in fails, display a message to the user.ㅂ
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        makeToast_short("이미 있는 아이디거나 확인 사항을 다시 확인해주세요.")
                    }
                }
        } else {
            if (id.length == 0) {
                makeToast_short("아이디를 입력해주세요.")
            } else {
                makeToast_short("비밀번호를 입력해주세요.")
            }
        }
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