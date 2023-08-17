package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.datingapp.R
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.message.fcm.NotiModel
import com.example.datingapp.message.fcm.PushNotification
import com.example.datingapp.message.fcm.RetrofitInstance
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.example.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "매칭 액티비티"

    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()
    lateinit var listViewAdapter : ListViewAdapter
    lateinit var getterUid : String
    lateinit var getterToken : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        val userListView = findViewById<ListView>(R.id.userListView)

        listViewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listViewAdapter

        // 내가 좋아요한 사람들
        getMyLikeList()

        // 전체 유저 중에서 내가 좋아요 한 사람을 가져와서
        // 이 사람이 나와 매칭이 됐는지 확인하는 것.
//        userListView.setOnItemClickListener { parent, view, position, id ->
//
//            Log.d(TAG, likeUserList[position].uid.toString())
//            checkMatching(likeUserList[position].uid.toString())
//
//            val notiModel = NotiModel("a", "b")
//
//            val pushModel = PushNotification(notiModel, likeUserList[position].token.toString())
//
//            testPush(pushModel)
//        }

        userListView.setOnItemClickListener{ parent, view, position, id ->

            checkMatching(likeUserList[position].uid.toString())
            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()

            return@setOnItemClickListener
        }

        // 내가 좋아요 한 유저를 클릭하면 메시지 보내기 창이 떠서 보낼 수 있게 하고,
        // 메시지 보내고 상대방에게 push 알람 띄워주기.
    }

    private fun checkMatching(otherUid: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.children.count() == 0){
                    Toast.makeText(this@MyLikeListActivity, "상대방이 좋아요 한 사람이 아무도 없습니다.", Toast.LENGTH_LONG).show()
                }else{
                    for(dataModel in dataSnapshot.children){
                        // 받아온 리스트안에 나의 UID가 있는지 확인.
                        val likeUserKey = dataModel.key.toString()
                        if(likeUserKey.equals(uid)){
                            Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다.", Toast.LENGTH_LONG).show()

                            // Dialog
                            showDialog()

                        }else{
//                            Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다.", Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

    private fun getMyLikeList(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    // 내가 좋아요한 사람들의 uid가 likeUserList에 들어있음
                    likeUserListUid.add(dataModel.key.toString())
                }
                getUserDataList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserDataModel::class.java)
                    // 전체 유저중에 내가 좋아요한 사람들의 정보만 add함
                    if(likeUserListUid.contains(user?.uid)){
                        // 내가 좋아요한 사람들의 정보만 뽑아올 수 있음
                        likeUserList.add(user!!)
                    }
                }
                listViewAdapter.notifyDataSetChanged()
                Log.d(TAG, likeUserList.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }


        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch{
        RetrofitInstance.api.postNotification(notification)
    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메시지 보내기")

        val mAlertDialog = mBuilder.show()
        val btn = mAlertDialog.findViewById<Button>(R.id.sendBtnArea)
        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)
        btn?.setOnClickListener {

            val msgText = textArea!!.text.toString()

            val msgModel = MsgModel(MyInfo.myNickname, msgText)

            FirebaseRef.userMsgRef.child(getterUid).push().setValue(msgModel)

            val notiModel = NotiModel(MyInfo.myNickname, msgText)

            val pushModel = PushNotification(notiModel, getterToken)

            testPush(pushModel)

            mAlertDialog.dismiss()
        }
    }
}