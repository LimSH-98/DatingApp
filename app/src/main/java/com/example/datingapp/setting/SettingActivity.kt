package com.example.datingapp.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.datingapp.R
import com.example.datingapp.auth.IntroActivity
import com.example.datingapp.message.MyLikeListActivity
import com.example.datingapp.message.MyMsgActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val myBtn = findViewById<Button>(R.id.myPageBtn)
        myBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val myMsgBtn = findViewById<Button>(R.id.myMsgBtn)
        myMsgBtn.setOnClickListener {

            val intent = Intent(this, MyMsgActivity::class.java)
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