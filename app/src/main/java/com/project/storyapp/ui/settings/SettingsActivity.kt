package com.project.storyapp.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.project.storyapp.databinding.ActivitySettingsBinding
import com.project.storyapp.ui.login.LoginActivity
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.Executors

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.apply {

            tvChangeLanguage.setOnClickListener {
                Intent(Settings.ACTION_LOCALE_SETTINGS).also { intent ->
                    startActivity(intent)
                }
            }

            tvLogout.setOnClickListener {
                val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                val scope = CoroutineScope(dispatcher)

                scope.launch {
                    viewModel.removeUserIsLogin()
                    viewModel.removeUserToken()

                    withContext(Dispatchers.Main){
                        Intent(this@SettingsActivity, LoginActivity::class.java).also { intent ->
                            startActivity(intent)
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }
}