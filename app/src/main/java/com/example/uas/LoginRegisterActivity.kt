package com.example.uas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uas.databinding.LoginRegisterActivityBinding

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: LoginRegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginRegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            viewPager.adapter = LoginRegisterAdapter(supportFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
        }


    }


}