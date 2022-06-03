package com.project.storyapp.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.data.source.Resource
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityLoginBinding
import com.project.storyapp.ui.main.MainActivity
import com.project.storyapp.ui.signup.SignUpActivity
import com.project.storyapp.utils.*
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loading: AlertDialog

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)

        processedLogin()

        onAction()
    }

    @SuppressLint("CheckResult")
    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map {
                    edtEmail.error != null
                }

            val passwordStream = RxTextView.textChanges(edtPassword)
                .skipInitialValue()
                .map {
                    edtPassword.error != null
                }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) { emailInvalid, passwordInvalid ->
                !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe { isValid ->
                if (isValid) btnLogin.enable() else btnLogin.disable()
            }

            btnLogin.setOnClickListener { logIn() }

        }
    }

    private fun logIn() {
        binding.apply {
            hideSoftKeyboard(this@LoginActivity, binding.root)

            val email = edtEmail.text?.trim().toString()
            val password = edtPassword.text?.trim().toString()

            viewModel.doLogin(email, password).observe(this@LoginActivity) {
                when (it) {
                    is Resource.Loading -> loading.show()
                    is Resource.Success -> {
                        val login = it.data

                        if (login != null) {
                            Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                                startActivity(intent)
                                finishAffinity()
                            }
                            viewModel.saveUserSession(true)
                            viewModel.saveUserToken(login.loginResult.token)
                        } else
                            this@LoginActivity.showToast(getString(R.string.login_failed))

                        loading.dismiss()
                    }
                    is Resource.Error -> {
                        Timber.e(it.message.toString())
                        this@LoginActivity.showToast(it.message.toString())
                        loading.dismiss()
                    }
                }
            }
        }
    }

    private fun onAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                Intent(this@LoginActivity, SignUpActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }
}