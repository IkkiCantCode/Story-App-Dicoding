package com.ikki.storyapp.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ikki.storyapp.ViewModelFactory
import com.ikki.storyapp.databinding.ActivityLoginBinding
import com.ikki.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Email atau password tidak boleh kosong.")
                return@setOnClickListener
            }

            viewModel.login(email, password)
            viewModel.loginResult.observe(this) { result ->
                result.onSuccess { loginResponse ->
                    AlertDialog.Builder(this).apply {
                        setTitle("Berhasil")
                        setMessage("Login berhasil!")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                result.onFailure { exception ->
                    Log.e("LoginActivity", "Login failed: ${exception.message}")
                    showAlert("Error", "Login gagal: ${exception.message}")
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun playAnimation() {
        val fadeInDuration = 800L

        ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            start()
        }

        ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 200
            start()
        }

        ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 400
            start()
        }

        ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 600
            start()
        }

        ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 800
            start()
        }

        ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1000
            start()
        }

        ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1200
            start()
        }

        ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1400
            start()
        }
    }
}