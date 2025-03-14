package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.CarouselAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.APP_SHARE_URL
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.utils.DotItemDecorator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    // TAG
    private val TAG = "HomeFragment"

    // view binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // adapter
    private var carouselAdapter: CarouselAdapter? = null

    // firebase db
    private val db = Firebase.firestore

    // handler
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var currentPosition = 0
    private val delay: Long = 4000L
    private lateinit var carouselLinearLayoutManager: LinearLayoutManager

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // sharedPreferences init
        sharedPreferences = (activity as MainActivity).sharedPreferences

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Home"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // slider images banner
        sliderImageView()

        // user name for the top
        getUserName()

        // on click listeners
        onClickListeners()

        // start auto scroll
        startAutoScroll()
    }

    // on click listeners
    private fun onClickListeners() {
        binding.apply {

            // open video btn
            videosItemBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_videosBranchFragment)
            }

            // apply for jobs btn
            jobItemBtn.setOnClickListener {
                val bundle = bundleOf(
                    "url" to "https://www.naukri.com/", "urlSiteName" to "Naukri.com"
                )
                findNavController().navigate(R.id.webViewFragment, bundle)
            }

            // discussion form
            discussionItemBtn.setOnClickListener {
                findNavController().navigate(R.id.discussionFragment)
            }

            // share app
            shareAppLayout.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody =
                    "Share, discuss, learn, post and do many more exciting things only in one app.\n\nDownload the app now via link.\n\n${
                        getString(R.string.app_name)
                    } : $APP_SHARE_URL"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(sharingIntent)
            }

            // feedback app
            feedbackAppLayout.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_feedbackFragment)
            }

            // university app
            universityAppLayout.setOnClickListener {
                val bundle = bundleOf(
                    "url" to "https://www.rgpv.ac.in/", "urlSiteName" to "RGPV University"
                )
                findNavController().navigate(R.id.webViewFragment, bundle)
            }

            // notification
            notificationsAppLayout.setOnClickListener {
                findNavController().navigate(R.id.notificationFragment)
            }

            // syllabus click listener
            quizBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
            }

            // oldYearPaper click listener
            classQuestions.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_previousYearPapersFragment)
            }

            // notes click listener
            classNotes.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
            }

            // videos click listener
            classVideos.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_videosBranchFragment)
            }
        }
    }

    // get user name
    private fun getUserName() {
        binding.apply {
            val name =
                (requireActivity() as MainActivity).sharedPreferences.getString(USER_NAME, "")!!
                    .split(" ")
            userName.text = "${name[0]} "
        }
    }

    // Slider image view container
    private fun sliderImageView() {
        binding.apply {
            autoSlideHome.apply {
                carouselLinearLayoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                layoutManager = carouselLinearLayoutManager
                carouselAdapter = CarouselAdapter(this.context)
                PagerSnapHelper().attachToRecyclerView(this)
                addItemDecoration(DotItemDecorator(3))
                adapter = carouselAdapter
                isNestedScrollingEnabled = true
            }
        }
    }

    private fun startAutoScroll() {
        if (runnable == null) {
            runnable = object : Runnable {
                override fun run() {
                    if (carouselAdapter?.itemCount == 0) return
                    if (currentPosition >= (carouselAdapter!!.itemCount)) {
                        currentPosition = 0
                    }

                    val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                        override fun getHorizontalSnapPreference(): Int {
                            return SNAP_TO_END
                        }
                    }
                    smoothScroller.targetPosition = currentPosition++
                    carouselLinearLayoutManager.startSmoothScroll(smoothScroller)
                    handler.postDelayed(this, delay)
                }
            }
            handler.postDelayed(runnable!!, delay)
        }
    }

    private fun stopAutoScroll() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }

    // onPrepareOptionsMenu for Circle layout profile menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val profileMenuItem = menu.findItem(R.id.homeProfileMenu)
        val rootView = profileMenuItem.actionView as FrameLayout
        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
        requestOptions.centerCrop()
        if (sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK, "")
                .toString() != null && sharedPreferences.getString(
                Constants.USER_PROFILE_PHOTO_LINK, ""
            ).toString() != ""
        ) {
            Glide.with(rootView)
                .load(sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK, "").toString())
                .apply(requestOptions).into(rootView.findViewById(R.id.homeProfileImage))
        }
        rootView.setOnClickListener {
            onOptionsItemSelected(profileMenuItem)
        }
        super.onPrepareOptionsMenu(menu)
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeProfileMenu -> {
                val navBuilder = NavOptions.Builder()
                navBuilder.setEnterAnim(R.anim.slide_down).setExitAnim(R.anim.wait_animation)
                    .setPopEnterAnim(R.anim.wait_animation).setPopExitAnim(R.anim.slide_up)
                findNavController().navigate(R.id.myProfile, null, navBuilder.build())
                true
            }

            R.id.uploadVideo -> {
                findNavController().navigate(R.id.action_homeFragment_to_uploadVideoFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // calling own menu for this fragment // (not required any more but not deleted because testing is not done)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopAutoScroll()
    }
}