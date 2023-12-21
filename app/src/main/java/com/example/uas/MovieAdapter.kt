package com.example.uas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val context: Context, private var dataList: List<Movies>) :
    RecyclerView.Adapter<MovieAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.film_card, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].dataImage).into(holder.recImage)
        holder.recTitle.text = dataList[position].dataTitle
        holder.recDesc.text = dataList[position].dataDesc

        holder.recCard.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("Image", dataList[position].dataImage)
                putExtra("Description", dataList[position].dataDesc)
                putExtra("Title", dataList[position].dataTitle)
                putExtra("Key", dataList[position].key)
            }
            context.startActivity(intent)
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

        val recImage: ImageView = itemView.findViewById(R.id.recImage)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
        val recDesc: TextView = itemView.findViewById(R.id.recDesc)
        val recTitle: TextView = itemView.findViewById(R.id.recTitle)
    }
}


