package com.example.showfirestoredatabaserecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()    // Firestore 인스턴스 선언
    private val itemList = arrayListOf<ListLayout>()    // 리스트 아이템 배열
    private val adapter = ListAdapter(itemList)         // 리사이클러 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load()

        rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvList.adapter = adapter
    }

/* --------------------기능함수-------------------- */

    // 엡이 시작되자마자 FireStore database 의 데이터를 불러오기 위한 함수
    private fun load() {
        db.collection("Contacts") // 작업할 컬렉션
            .get() // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                itemList.clear()
                for(document in result) { // 가져온 문서들은 result 에 들어감
                    val item = ListLayout(document["name"] as String, document["number"] as String)
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged() // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.d("jeongmin", "Error getting documents : $exception")
            }
    }

    // 쓰기 다이얼로그 생성하는 함수
    private fun writeDialog() {
        // 대화상자 생성
        val builder = AlertDialog.Builder(this)

        // 대화상자에 텍스트 입력 필드 추가
        val tvName = TextView(this)
        tvName.text = "Name"
        val tvNumber = TextView(this)
        tvNumber.text = "Number"
        val etName = EditText(this)
        etName.isSingleLine = true
        val etNumber = EditText(this)
        etNumber.isSingleLine = true
        val mLayout = LinearLayout(this)
        mLayout.orientation = LinearLayout.VERTICAL
        mLayout.setPadding(16)
        mLayout.addView(tvName)
        mLayout.addView(etName)
        mLayout.addView(tvNumber)
        mLayout.addView(etNumber)
        builder.setView(mLayout)

        builder.setTitle("데이터 추가")
        builder.setPositiveButton("추가") { dialog, which ->
            // EditText 에서 문자열을 가져와 hashMap 으로 만듦
            val data = hashMapOf(
                "name" to etName.text.toString(),
                "number" to etNumber.text.toString()
            )
            // Contacts 컬렉션에 data 를 자동 이름으로 저장
            db.collection("Contacts")
                .add(data)
                .addOnSuccessListener {
                    // 성공할 경우
                    Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // 실패할 경우
                    Log.w("MainActivity", "Error getting documents: $exception")
                }
        }
        builder.setNegativeButton("취소") { dialog, which ->

        }
        builder.show()
    }

/* --------------------onClick 함수-------------------- */

    fun onClickWrite(view: View) {
        writeDialog()
    }

    fun onClickRefresh(view: View) {
        db.collection("Contacts") // 작업할 컬렉션
            .get() // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                itemList.clear()
                for(document in result) { // 가져온 문서들은 result 에 들어감
                    val item = ListLayout(document["name"] as String, document["number"] as String)
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged() // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.d("jeongmin", "Error getting documents : $exception")
            }
    }
}