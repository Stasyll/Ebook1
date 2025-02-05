package com.example.ebook1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ebook1.databinding.ActivityMainBinding

// Класс входа в аккаунт или пропуска входа
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

        binding.skipBtn.setOnClickListener{
            startActivity(Intent(this,DashboardActivity::class.java))
        }
    }
}