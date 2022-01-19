package com.digitalinclined.edugate.ui.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatDelegate
import com.digitalinclined.edugate.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    // viewBinding
    private lateinit var binding: ActivitySplashScreenBinding

    // firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force No Night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Screen orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // delay Splash Screen for 5 sec
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do something here in every sec
            }

            // After finish navigate to next fragment
            override fun onFinish() {
                if (firebaseAuth.currentUser != null) {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashScreenActivity, SetupActivity::class.java))
                }
                finish()
            }
        }.start()

    }
}