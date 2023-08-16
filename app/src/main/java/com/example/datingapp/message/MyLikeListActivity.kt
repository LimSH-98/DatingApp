package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.datingapp.R
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "매칭 액티비티"

    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()
    lateinit var listViewAdapter : ListViewAdapter

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
        userListView.setOnItemClickListener { parent, view, position, id ->

            Log.d(TAG, likeUserList[position].uid.toString())
            checkMatching(likeUserList[position].uid.toString())
        }
    }

    private fun checkMatching(otherUid: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.children.count() == 0){
                    Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다.", Toast.LENGTH_LONG).show()
                }else{
                    for(dataModel in dataSnapshot.children){
                        // 받아온 리스트안에 나의 UID가 있는지 확인.
                        val likeUserKey = dataModel.key.toString()
                        Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다.", Toast.LENGTH_LONG).show()
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
}