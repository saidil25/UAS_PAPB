package com.example.uas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class UserMovieAdapter(private val context: Context, private var dataList: List<Movies>) :
    RecyclerView.Adapter<UserMovieAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film_user, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].dataImage).into(holder.recImage)
        holder.recTitle.text = dataList[position].dataTitle

        // Tambahkan OnClickListener ke ImageView
        holder.recImage.setOnClickListener {
            // Tindakan khusus ketika gambar diklik
            Toast.makeText(context, "Image Clicked at position $position", Toast.LENGTH_SHORT).show()

            // Contoh: Jika Anda ingin membuka aktivitas baru, gunakan Intent di sini
            val intent = Intent(context, DetailMovie::class.java).apply {
                putExtra("Image", dataList[position].dataImage)
                putExtra("Title", dataList[position].dataTitle)
                putExtra("Key", dataList[position].key)
            }
            context.startActivity(intent)
        }

        // Tambahkan OnClickListener ke CardView (opsional)
        holder.recCard.setOnClickListener {
            // Tindakan khusus ketika CardView diklik
            Toast.makeText(context, "CardView Clicked at position $position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun searchDataList(searchList: ArrayList<Movies>) {
        dataList = searchList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView = itemView.findViewById(R.id.image_movie)
        val recCard: CardView = itemView.findViewById(R.id.card)
        val recTitle: TextView = itemView.findViewById(R.id.judul_movie)
    }
}
