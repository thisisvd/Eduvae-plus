package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentVideoBinding
import com.digitalinclined.edugate.models.youtubemodel.Item
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class VideoFragment : Fragment() {

    // view binding
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    // nav args
    private val args: VideoFragmentArgs by navArgs()

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Video Premiere"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_baseline_arrow_back_ios_new_24
            )
        )
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // setup UI
            setupUI(args.item)
        }
    }

    // setup ui
    private fun setupUI(videoItem: Item) {
        binding.apply {

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
                val shareBody =
                    "Hii!, I founded an useful video.\n\n" + "See the video below.\n\nVideo link : $videoLink" + sharingIntent.putExtra(
                        Intent.EXTRA_SUBJECT, "Subject Here"
                    )
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(sharingIntent)
            }

            // load video
            lifecycle.addObserver(youtubePlayerView)
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoItem.id.videoId, 0f)
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}