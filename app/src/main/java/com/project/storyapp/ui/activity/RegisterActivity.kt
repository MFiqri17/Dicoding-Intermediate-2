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
import com.project.storyapp.databinding.ActivityRegisterBinding
import com.project.storyapp.ui.viewModel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        playAnimation()
    }


    private fun setupViews() {
        binding.apply {
            val signInBtn = textSignInClickable
            val registerBtn = buttonRegister

            signInBtn.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            registerBtn.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()
                when {
                    name.isEmpty() -> {
                        edRegisterName.error = "Name is Required"
                    }
                    email.isEmpty() -> {
                        edRegisterEmail.error = "Email is Required"
                    }
                    password.isEmpty() -> {
                        edRegisterPassword.error = "Password is Required"
                    }
                    else -> {
                        register(name, email, password)
                    }
                }
            }
        }
    }


    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonRegister.visibility = View.GONE
        binding.edRegisterName.visibility = View.GONE
        binding.edRegisterEmail.visibility = View.GONE
        binding.edRegisterPassword.visibility = View.GONE
        binding.textSignIn.visibility = View.GONE
        binding.textSignInClickable.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.buttonRegister.visibility = View.VISIBLE
        binding.edRegisterName.visibility = View.VISIBLE
        binding.edRegisterEmail.visibility = View.VISIBLE
        binding.edRegisterPassword.visibility = View.VISIBLE
        binding.textSignIn.visibility = View.VISIBLE
        binding.textSignInClickable.visibility = View.VISIBLE
    }

    private fun register(name: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                showLoading()

                viewModel.register(name, email, password).collect { response ->
                    if (response.isSuccess) {
                        showToast("Register Success")
                        navigateToLoginActivity()
                    } else {
                        showToast("Register Failed: ${response.exceptionOrNull()?.message}")
                    }
                }
            } catch (exception: Throwable) {
                showToast("Register Failed: ${exception.message}")
            } finally {
                hideLoading()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {

        val textSignIn = ObjectAnimator.ofFloat(binding.textSignIn, View.ALPHA, 1f).setDuration(500)
        val textSignInClickable = ObjectAnimator.ofFloat(binding.textSignInClickable, View.ALPHA, 1f).setDuration(500)
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(textSignIn, textSignInClickable)
        }

        AnimatorSet().apply {
            playSequentially(edName, edEmail, edPassword, together)
            start()
        }
    }
}