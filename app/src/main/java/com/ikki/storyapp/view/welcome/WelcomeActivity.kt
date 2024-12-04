package com.ikki.storyapp.view.welcome

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ikki.storyapp.R
import com.ikki.storyapp.databinding.ActivityWelcomeBinding
import com.ikki.storyapp.view.login.LoginActivity
import com.ikki.storyapp.view.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
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
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegis.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun playAnimation() {
        val welcomeImage: ImageView = findViewById(R.id.welcomeImage)
        val imageAnimator = ObjectAnimator.ofFloat(welcomeImage, "translationX", -1000f, 0f)
        imageAnimator.duration = 1000
        imageAnimator.start()

        val welcomeText: TextView = findViewById(R.id.welcomeText)
        val textAnimator = ObjectAnimator.ofFloat(welcomeText, "translationX", -1000f, 0f)
        textAnimator.duration = 1000
        textAnimator.start()

        val descText: TextView = findViewById(R.id.descText)
        val descAnimator = ObjectAnimator.ofFloat(descText, "translationX", -1000f, 0f)
        descAnimator.duration = 1000
        descAnimator.start()

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnLoginAnimator = ObjectAnimator.ofFloat(btnLogin, "translationX", -1000f, 0f)
        btnLoginAnimator.duration = 1000
        btnLoginAnimator.start()

        val btnRegis: Button = findViewById(R.id.btnRegis)
        val btnRegisAnimator = ObjectAnimator.ofFloat(btnRegis, "translationX", -1000f, 0f)
        btnRegisAnimator.duration = 1000
        btnRegisAnimator.start()
    }
}
