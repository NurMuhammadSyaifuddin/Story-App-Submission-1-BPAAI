package com.project.storyapp.ui.signup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.data.source.Resource
import com.project.storyapp.databinding.ActivitySignUpBinding
import com.project.storyapp.ui.login.LoginActivity
import com.project.storyapp.utils.*
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpViewModel by viewModel()

    private val customDialog by lazy { CustomDialog() }

    private lateinit var loading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)

        processedAccount()

        onAction()
    }

    private fun onAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@SignUpActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun processedAccount() {
        binding.apply {
            val nameStream = RxTextView.textChanges(edtName)
                .skipInitialValue()
                .map {
                    edtName.error != null
                }

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
                nameStream,
                emailStream,
                passwordStream
            ) { nameInvalid, emailInvalid, passwordInvalid ->
                !nameInvalid && !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe { isValid ->
                if (isValid) btnSignUp.enable() else btnSignUp.disable()
            }

            btnSignUp.setOnClickListener { signUp() }
        }
    }

    private fun signUp() {
        binding.apply {

            hideSoftKeyboard(this@SignUpActivity, binding.root)

            val name = edtName.text?.trim().toString()
            val email = edtEmail.text?.trim().toString()
            val password = edtPassword.text?.trim().toString()

            viewModel.doRegister(name, email, password).observe(this@SignUpActivity) {
                when (it) {
                    is Resource.Loading -> loading.show()
                    is Resource.Success -> {
                        customDialog.showSuccessCreateAccount(this@SignUpActivity) {
                            Intent(
                                this@SignUpActivity,
                                LoginActivity::class.java
                            ).also { intent ->
                                startActivity(intent)
                                finishAffinity()
                            }
                        }

                        loading.dismiss()
                    }

                    is Resource.Error -> {
                        this@SignUpActivity.showToast(it.message.toString())
                        Timber.e(it.message.toString())
                        loading.dismiss()
                    }

                }
            }
        }
    }
}