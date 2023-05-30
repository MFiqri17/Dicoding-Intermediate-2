package com.project.storyapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.project.storyapp.databinding.ActivityLoginBinding
import com.project.storyapp.ui.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        playAnimation()
    }


    private fun setupViews() {
        binding.apply {
            val signUpBtn = textSignUpClickable
            val loginBtn = buttonLogin

            signUpBtn.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            loginBtn.setOnClickListener {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()
                when {
                    email.isEmpty() -> {
                        edLoginEmail.error = "Email is Required"
                    }
                    password.isEmpty() -> {
                        edLoginPassword.error = "Password is Required"
                    }
                    else -> {
                        login(email, password)
                    }
                }

            }
        }
    }


    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.GONE
        binding.edLoginEmail.visibility = View.GONE
        binding.edLoginPassword.visibility = View.GONE
        binding.textSignUp.visibility = View.GONE
        binding.textSignUpClickable.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.buttonLogin.visibility = View.VISIBLE
        binding.edLoginEmail.visibility = View.VISIBLE
        binding.edLoginPassword.visibility = View.VISIBLE
        binding.textSignUp.visibility = View.VISIBLE
        binding.textSignUpClickable.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                showLoading()
                viewModel.login(email, password).collect { result ->
                    if (result.isSuccess) {
                        showToast("Login Success")
                        navigateToMainActivity()
                    } else {
                        val errorMessage = result.exceptionOrNull()?.message ?: "Unknown Error"
                        showToast("Login Failed: $errorMessage")
                    }
                }
            } catch (exception: Exception) {
                showToast("Login Failed: ${exception.message}")
            } finally {
                hideLoading()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun playAnimation() {

        val textSignUp = ObjectAnimator.ofFloat(binding.textSignUp, View.ALPHA, 0f, 1f).setDuration(500)
        val textSignUpClickable =
            ObjectAnimator.ofFloat(binding.textSignUpClickable, View.ALPHA, 0f,1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f,1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f,1f).setDuration(500)


        val together = AnimatorSet().apply {
            playTogether(textSignUp, textSignUpClickable)
        }


        AnimatorSet().apply {
            playSequentially(edEmail, edPassword, together)
            start()
        }
    }
}
