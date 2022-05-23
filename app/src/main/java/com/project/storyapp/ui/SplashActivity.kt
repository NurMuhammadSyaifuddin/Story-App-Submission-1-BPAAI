package com.project.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}