package com.digitalinclined.edugate.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.BASE_YOUTUBE_API_KEY
import com.digitalinclined.edugate.databinding.ActivityYoutubeVideoBinding
import com.digitalinclined.edugate.models.youtubemodel.Item
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YoutubeVideoActivity : YouTubeBaseActivity() {

    // TAG
    private val TAG = "YoutubeVideoActivity"

    // view binding
    private lateinit var binding: ActivityYoutubeVideoBinding

    // var
    private lateinit var videoItem: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {

            // init var
            videoItem = (intent.getSerializableExtra("videoItem") as? Item)!!

            // youtube init
            youtubeListener()

            // on click listeners
            onClickListeners()
        }
    }

    // on click listeners
    private fun onClickListeners() {
        binding.apply {

            // back
            back.setOnClickListener {
                onBackPressed()
            }

            if (videoItem != null) {

                // channel name
                channelName.text = videoItem.snippet.channelTitle

                // title
                videoTitle.text = videoItem.snippet.title

                // description
                videoDescription.text = videoItem.snippet.description

                // share video
                shareVideo.setOnClickListener {
                    val videoLink = "https://www.youtube.com/watch?v=${videoItem.id.videoId}"
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "Hii!, I founded an useful video.\n\n" +
                            "See the video below.\n\nVideo link : $videoLink" +
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(sharingIntent)
                }
            }

        }
    }

    // youtube listener
    private fun youtubeListener(){
        binding.apply {
            val listener = object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    youtubePlayer: YouTubePlayer?,
                    p2: Boolean
                ) {

                    if (videoItem != null && !videoItem.id.videoId.isNullOrEmpty()) {
                        // Load video using youtube video id
                        youtubePlayer?.loadVideo(videoItem.id.videoId)

                        Log.d(TAG,videoItem.id.videoId)

                        //start video
                        youtubePlayer?.play()
                    }
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    // Display snack
                    Snackbar.make(binding.root,"Loading failed!",Snackbar.LENGTH_SHORT).show()
                }
            }

            // init api
            youtubePlayer.initialize(BASE_YOUTUBE_API_KEY,listener)
        }
    }

}