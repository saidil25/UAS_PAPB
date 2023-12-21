package com.example.uas

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var updateDesc: EditText
    private lateinit var updateTitle: EditText
    private var title: String = ""
    private var desc: String = ""
    private var imageUrl: String = ""
    private var key: String = ""
    private var oldImageURL: String = ""
    private var uri: Uri? = null
    private lateinit var databaseReference: CollectionReference
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)

        updateButton = findViewById(R.id.UpdateButton)
        updateDesc = findViewById(R.id.UpdateDesc)
        updateImage = findViewById(R.id.UdateImage)
        updateTitle = findViewById(R.id.UpdateJudul)

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    uri = data?.data
                    updateImage.setImageURI(uri)
                } else {
                    Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            }

        val bundle = intent.extras
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage)
            updateTitle.setText(bundle.getString("Title"))
            updateDesc.setText(bundle.getString("Description"))
            key = bundle.getString("Key").orEmpty()
            oldImageURL = bundle.getString("Image").orEmpty()
        }




        databaseReference = FirebaseFirestore.getInstance().collection("Movies")

        updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        updateButton.setOnClickListener {
            saveData()
            val intent = Intent(this@UpdateActivity, AdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveData() {
        if (uri != null) {
            storageReference = FirebaseStorage.getInstance().reference.child("Android Images").child(uri?.lastPathSegment.toString())

            val builder = AlertDialog.Builder(this@UpdateActivity)
            builder.setCancelable(false)
            builder.setView(R.layout.progres_layout)
            val dialog = builder.create()
            dialog.show()

            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnCompleteListener { uriTask ->
                    imageUrl = uriTask.result.toString()
                    updateData()
                    dialog.dismiss()
                }
            }.addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this@UpdateActivity, "Error uploading image: $e", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case when uri is null
            Toast.makeText(this@UpdateActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateData() {
        title = updateTitle.text.toString().trim()
        desc = updateDesc.text.toString().trim()


        val dataClass = Movies(title, desc, imageUrl)

        databaseReference.document(key).set(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL)
                reference.delete()
                Toast.makeText(this@UpdateActivity, "Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@UpdateActivity, "Error updating data: $e", Toast.LENGTH_SHORT).show()
        }
    }
}
