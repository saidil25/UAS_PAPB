package com.example.uas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormat
import java.util.Calendar

class StoreActivity : AppCompatActivity() {

    private lateinit var uploadImage: ImageView
    private lateinit var uploadDesc: EditText
    private lateinit var uploadJudul: EditText
    private lateinit var uploadLang: EditText
    private lateinit var saveButton: Button
    private var imageURL: String? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.strore_layout)

        uploadImage = findViewById(R.id.uploadImage)
        uploadDesc = findViewById(R.id.uploadDesc)
        uploadJudul = findViewById(R.id.uploadJudul)
        saveButton = findViewById(R.id.saveButton)

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    uri = data?.data
                    uploadImage.setImageURI(uri)
                } else {
                    Toast.makeText(this@StoreActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            }

        uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri?.lastPathSegment!!)

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            uriTask.addOnSuccessListener { urlImage ->
                imageURL = urlImage.toString()
                uploadData()
            }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }

    }


    private fun uploadData() {
        val title: String = uploadJudul.text.toString()
        val desc: String = uploadDesc.text.toString()

        val dataClass = Movies(dataTitle = title, dataDesc = desc, dataImage = imageURL)

        // Menggunakan waktu saat ini sebagai ID dokumen
        val currentDate: String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        firestore.collection("Movies").document(currentDate)
            .set(dataClass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@StoreActivity, "Saved", Toast.LENGTH_SHORT).show()
                    // Pindah ke AdminActivity setelah berhasil menyimpan data
                    val adminIntent = Intent(this@StoreActivity, AdminActivity::class.java)
                    startActivity(adminIntent)

                    // Tutup StoreActivity agar pengguna tidak dapat kembali ke halaman sebelumnya
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@StoreActivity, e.message, Toast.LENGTH_SHORT).show()
            }
    }

}
