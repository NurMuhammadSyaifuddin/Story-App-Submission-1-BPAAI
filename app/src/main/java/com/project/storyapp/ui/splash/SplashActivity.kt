package com.project.storyapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivitySplashBinding
import com.project.storyapp.ui.login.LoginActivity
import com.project.storyapp.ui.main.MainActivity
import com.project.storyapp.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            progressBar.visible()

            viewModel.getUserIsLogin().observe(this@SplashActivity){
                if (it){
                    Intent(this@SplashActivity, MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Intent(this@SplashActivity,LoginActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }
    }
}