package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DetailActivity : AppCompatActivity() {

    private lateinit var detailDesc: TextView
    private lateinit var detailTitle: TextView
    private lateinit var detailImage: ImageView
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var editButton: FloatingActionButton
    private var key: String = ""
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        detailDesc = findViewById(R.id.detailDesc)
        detailImage = findViewById(R.id.detailImage)
        detailTitle = findViewById(R.id.detailTitle)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)


        val bundle = intent.extras
        if (bundle != null) {
            detailDesc.text = bundle.getString("Description")
            detailTitle.text = bundle.getString("Title")
            key = bundle.getString("Key").orEmpty()
            imageUrl = bundle.getString("Image").orEmpty()
            Glide.with(this).load(bundle.getString("Image")).into(detailImage)
        }

        deleteButton.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()

            val storageReference = storage.getReferenceFromUrl(imageUrl)

            // Hapus objek di Firebase Storage
            storageReference.delete()
                .addOnSuccessListener {
                    // Objek di Firebase Storage berhasil dihapus, sekarang hapus dokumen di Firestore
                    firestore.collection("Movies").document(key).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, AdminActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error deleting document: $e", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    // Kesalahan saat menghapus objek dari Firebase Storage
                    Toast.makeText(this, "Error deleting object: $e", Toast.LENGTH_SHORT).show()
                }
        }
        editButton.setOnClickListener {
            val intent = Intent(this, UpdateActivity::class.java)
                .putExtra("Title", detailTitle.text.toString())
                .putExtra("Description", detailDesc.text.toString())
                .putExtra("Image", imageUrl)
                .putExtra("Key", key)
            startActivity(intent)
        }


    }
}
