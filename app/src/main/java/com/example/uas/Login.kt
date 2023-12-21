package com.example.uas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : Fragment() {

    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        loginEmail = view.findViewById(R.id.email_login)
        loginPassword = view.findViewById(R.id.pasword_login)
        loginButton = view.findViewById(R.id.login_btn)

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                        // Dapatkan informasi pengguna dari Firestore setelah login berhasil
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val username = document.getString("username")
                                    val name = document.getString("name")
                                    val role = document.getString("role")

                                    Log.d("LoginFragment", "Role: $role")  // Tambahkan log ini

                                    // Simpan informasi pengguna ke SharedPreferences
                                    saveUserInfo(username, name, role)

                                    if (role == "admin") {
                                        // Pengguna dengan role admin
                                        val intent = Intent(requireContext(), AdminActivity::class.java)
                                        intent.putExtra("username", username)
                                        intent.putExtra("name", name)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    } else {
                                        // Pengguna dengan role user
                                        val intent = Intent(requireContext(), UserActivity::class.java)
                                        intent.putExtra("username", username)
                                        intent.putExtra("name", name)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }


        }

        return view
    }

    private fun saveUserInfo(username: String?, name: String?, role: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("name", name)
        if (role != "admin") {
            // Hanya simpan role jika bukan admin
            editor.putString("role", role)
        }
        editor.apply()
    }
}
