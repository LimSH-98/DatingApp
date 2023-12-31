package com.example.datingapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.setting.SettingActivity
import com.example.datingapp.slider.CardStackAdapter
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.example.datingapp.utils.MyInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager: CardStackLayoutManager

    private val usersDataList = mutableListOf<UserDataModel>()

    private var userCount = 0

    private lateinit var currentUserGender: String

    private val TAG = "메인 액티비티"

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val setting = findViewById<ImageView>(R.id.settingIcon)
        setting.setOnClickListener {

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object: CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if(direction == Direction.Right){
//                    Toast.makeText(this@MainActivity, "right", Toast.LENGTH_LONG).show()

                    userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                }
                if(direction == Direction.Left){
//                    Toast.makeText(this@MainActivity, "left", Toast.LENGTH_LONG).show()
                }

                userCount++

                if(userCount == usersDataList.count()){
                    getUserDataList(currentUserGender)
                    Toast.makeText(this@MainActivity, "Get New User!!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }
        })
        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        getMyUserData()
    }

    private fun getMyUserData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, dataSnapshot.toString())
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                Log.d(TAG, data?.gender.toString())

                currentUserGender = data?.gender.toString()

                MyInfo.myNickname = data?.nickname.toString()

                getUserDataList(currentUserGender)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(currentUserGender : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    var user = dataModel.getValue(UserDataModel::class.java)

                    if(user!!.gender.toString() == currentUserGender){

                    }else{
                        usersDataList.add(user!!)
                    }

                }

                cardStackAdapter.notifyDataSetChanged() // Adapter 동기화시키는 함수.
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }
    // 유저의 좋아요를 표시하는 부분
    private fun userLikeOtherUser(myUid: String, otherUid: String){
        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")

        getOtherUserLikeList(otherUid)
    }

    private fun getOtherUserLikeList(otherUid : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    // 받아온 리스트안에 나의 UID가 있는지 확인.
                    val likeUserKey = dataModel.key.toString()
                    if(likeUserKey == uid ){
                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_LONG).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

    // Notification
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendNotification(){
        var builder = NotificationCompat.Builder(this, "Test Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("매칭완료!")
            .setContentText("매칭이 완료되었습니다. ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            try{
                with(NotificationManagerCompat.from(this)){
                    notify(123, builder.build())
                }
                Log.d(TAG,"오류 발생 X")
            }catch(e : SecurityException) {
                Log.d(TAG, "오류 발생!")
            }
    }
}