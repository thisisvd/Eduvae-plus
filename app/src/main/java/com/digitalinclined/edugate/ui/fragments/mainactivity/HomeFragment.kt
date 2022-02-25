package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SliderImageAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.restapi.models.banner.Banner
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.fragments.YoutubeVideoActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations


class HomeFragment : Fragment(R.layout.fragment_home) {

    // TAG
    private val TAG = "HomeFragment"

    // viewBinding
    private lateinit var binding: FragmentHomeBinding

    // Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // viewModel
    private val viewModel: MainViewModel by activityViewModels()

    // banner Image List
    private var bannerList = arrayListOf<Banner>()

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // sharedPreferences init
        sharedPreferences = (activity as MainActivity).sharedPreferences

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Home"

        // fetching banners
        viewModel.getBanner()

        // viewModelObservers
        viewModelObservers()

        // user name for the top
        getUserName()

        binding.apply {

            // syllabus click listener
            syllabus.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_syllabusFragment)
            }

            // oldYearPaper click listener
            oldYearPaper.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_yearOfPapersFragment)
            }

            // notes click listener
            notes.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
            }

            // videos click listener
            videos.setOnClickListener {
                startActivity(Intent(requireActivity(),YoutubeVideoActivity::class.java))
            }

        }

    }

    // viewModel Observers
    private fun viewModelObservers() {
        viewModel.apply {

            // banners observers
            getBannerDetail.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { bannerDetails ->
                            if(bannerDetails.status == 200) {
                                Log.d(TAG, "${bannerDetails.banners.size.toString()}")
                                for(item in bannerDetails.banners) {
                                    bannerList.add(item)
                                }
                            }
                            if(bannerList.size > 0) {
                                sliderImageView()
                            }
                        }
                    }
                    is Resource.Error -> {
                        Log.d(TAG, "Error occurred while loading data! ${response.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Loading!")
                    }
                }
            }

        }
    }

    // get user name
    private fun getUserName() {
        binding.apply {
            val name = (requireActivity() as MainActivity).sharedPreferences.getString(USER_NAME,"")!!.split(" ")
            userName.text = "${name[0]} "
        }
    }

    // Slider image view container
    private fun sliderImageView() {
        binding.apply {

            // Slider Image Adapter
            val adapter = SliderImageAdapter()

            // setting up banners in the adapter list
            for(banner in bannerList) {
                adapter.addItem(banner)
            }

            // adapter init
            sliderView.setSliderAdapter(adapter)
            sliderView.setIndicatorAnimation(IndicatorAnimationType.SWAP)
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            sliderView.startAutoCycle()
        }
    }

    // onPrepareOptionsMenu for Circle layout profile menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        val profileMenuItem = menu!!.findItem(R.id.homeProfileMenu)
        val rootView = profileMenuItem.actionView as FrameLayout
        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
        requestOptions.centerCrop()
        if(sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK,"").toString() != null &&
            sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK,"").toString() != "") {
            Glide.with(rootView)
                .load(sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK,"").toString())
                .apply(requestOptions)
                .into(rootView.findViewById(R.id.homeProfileImage))
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
                findNavController().navigate(R.id.myProfile,null,navBuilder.build())
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

}