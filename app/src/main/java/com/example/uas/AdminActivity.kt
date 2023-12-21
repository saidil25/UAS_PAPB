package com.example.uas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class AdminActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query
    private lateinit var eventListener: ListenerRegistration
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<Movies>
    private lateinit var adapter: MovieAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_layout)

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        val gridLayoutManager = GridLayoutManager(this@AdminActivity, 1)
        recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@AdminActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progres_layout)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()

        adapter = MovieAdapter(this@AdminActivity, dataList)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()
        query = firestore.collection("Movies")
        dialog.show()
        eventListener = query.addSnapshotListener { snapshot, exception ->
            dataList.clear()
            if (exception != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val dataClass = document.toObject(Movies::class.java)
                    dataClass?.let {
                        it.key = document.id
                        dataList.add(it)
                    }
                }
                // Print the size of dataList to check if there is data
                println("Number of items: ${dataList.size}")
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }




        fab.setOnClickListener {
            val intent = Intent(this@AdminActivity, StoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun searchList(text: String) {
        val searchList = ArrayList<Movies>()
        for (dataClass in dataList) {
            if (dataClass.dataTitle.toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener.remove()
    }
}
