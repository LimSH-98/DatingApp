package com.example.datingapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.datingapp.R
import com.example.datingapp.auth.UserDataModel
import com.example.datingapp.utils.FirebaseAuthUtils
import com.example.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMsgActivity : AppCompatActivity() {

    private val TAG = "MyMsgActivity"

    lateinit var listViewAdapter : MsgAdapter
    val msgList = mutableListOf<MsgModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_msg)

        val listView = findViewById<ListView>(R.id.msgListView)

        listViewAdapter = MsgAdapter(this, msgList)
        listView.adapter = listViewAdapter

        getMyMsg()
    }

    private fun getMyMsg(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                msgList.clear()

                for(dataModel in dataSnapshot.children){
                    val msg = dataModel.getValue(MsgModel::class.java)
                    msgList.add(msg!!)
                    Log.d(TAG, msg.toString())
                }
                msgList.reverse()

                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", error.toException())
            }
        }
        FirebaseRef.userMsgRef.child(FirebaseAuthUtils.getUid()).addValueEventListener(postListener)
    }
}