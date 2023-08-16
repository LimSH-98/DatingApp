package com.example.datingapp.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.datingapp.R
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.message.MyLikeListActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // 알림 주는 방법
        // 1. 앱에서 코드로 notification 띄우기.
        // 2. Firebase Console에서 모든 앱에게 push 보내기
        // 3. 특정 사용자에게 메세지 보내기.(Firebase Console에서) -> 토큰을 받아와야 한다. uid랑은 다른 개념.
        // 4. Firebase가 아니라, 앱에서 직접 다른 사람에게 메시지 보내기

        val myBtn = findViewById<Button>(R.id.myPageBtn)
        myBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val mylikeBtn = findViewById<Button>(R.id.myListListBtn)
        mylikeBtn.setOnClickListener {
            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logOutBtn)
        logoutBtn.setOnClickListener {
            val auth = Firebase.auth
            auth.signOut()
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
    }
}