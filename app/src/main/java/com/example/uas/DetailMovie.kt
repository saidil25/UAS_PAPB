package com.example.uas

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailMovie : AppCompatActivity() {

    private lateinit var detailDesc: TextView
    private lateinit var detailTitle: TextView
    private lateinit var detailImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_user_movie)

        detailDesc = findViewById(R.id.detailDesc)
        detailImage = findViewById(R.id.detailImage)
        detailTitle = findViewById(R.id.detailTitle)

        val bundle = intent.extras
        if (bundle != null) {
            detailDesc.text = bundle.getString("Description")
            detailTitle.text = bundle.getString("Title")
            Glide.with(this).load(bundle.getString("Image")).into(detailImage)
        }
    }
}
