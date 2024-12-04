package com.ikki.storyapp.view.signup

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
import com.ikki.storyapp.databinding.ActivitySignupBinding
import com.ikki.storyapp.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            Log.d("SignupActivity", "User Input - Name: $name, Email: $email, Password: $password")

            if (password.length < 8) {
                showAlert("Error", "Password harus lebih dari atau sama dengan 8.")
                return@setOnClickListener
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Name, email atau password tidak boleh kosong.")
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
            viewModel.registerResult.observe(this) { result ->
                result.onSuccess { registerResponse ->
                    AlertDialog.Builder(this).apply {
                        setTitle("Success")
                        setMessage("Akun dengan email $email telah dibuat.")
                        setPositiveButton("OK") { _, _ ->
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                result.onFailure { exception ->
                    AlertDialog.Builder(this).apply {
                        setTitle("Error")
                        setMessage("Registrasi gagal: ${exception.message}")
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
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

        ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 400
            start()
        }

        ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 600
            start()
        }

        ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 800
            start()
        }

        ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1000
            start()
        }

        ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1200
            start()
        }

        ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1400
            start()
        }

        ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).apply {
            duration = fadeInDuration
            startDelay = 1600
            start()
        }
    }
}