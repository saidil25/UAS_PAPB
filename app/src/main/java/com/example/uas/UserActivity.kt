package com.example.uas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uas.databinding.UserLayoutBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: UserLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Profile())

        binding.bottomnav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.nav_ticket-> replaceFragment(Movie())
                R.id.nav_profile -> replaceFragment(Profile())

                else -> {}
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}