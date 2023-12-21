package com.example.uas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class Movie : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<Movies>
    private lateinit var adapter: UserMovieAdapter // Ganti dengan UserMovieAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var eventListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = gridLayoutManager

        dataList = ArrayList()
        adapter = UserMovieAdapter(requireContext(), dataList) // Ganti dengan UserMovieAdapter
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        eventListener = firestore.collection("Movies").addSnapshotListener { snapshot, exception ->
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
                adapter.notifyDataSetChanged()
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener.remove()
    }
}
