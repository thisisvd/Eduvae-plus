package com.digitalinclined.edugate.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ActivityYoutubeVideoBinding
import com.digitalinclined.edugate.databinding.FragmentWebViewBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YoutubeVideoActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityYoutubeVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listener = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                youtubePlayer: YouTubePlayer?,
                p2: Boolean
            ) {
                // Load video using youtube video id
                youtubePlayer?.loadVideo("GuVyQ1zuZuw")
                //start video
                youtubePlayer?.play()
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                // Display toast
                Toast.makeText(this@YoutubeVideoActivity,"Loading Failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.youtubePlayer.initialize("AIzaSyCdn27lKADWOvbq8-nclKDnKphucdUKmls",listener)


    }
}