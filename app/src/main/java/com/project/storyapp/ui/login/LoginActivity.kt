package com.project.storyapp.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}