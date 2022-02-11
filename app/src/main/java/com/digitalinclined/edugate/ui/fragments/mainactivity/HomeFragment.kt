package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.fragments.YoutubeVideoActivity
import com.google.firebase.auth.FirebaseAuth
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class HomeFragment : Fragment(R.layout.fragment_home) {

    // TAG
    private val TAG = "HomeFragment"

    // viewBinding
    private lateinit var binding: FragmentHomeBinding

    // Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

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

        // calling carousel method
        carouselImageView()

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
//                findNavController().navigate(R.id.action_homeFragment_to_chaptersScreenFragment)
//                Toast.makeText(requireContext(),"Videos",Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(),YoutubeVideoActivity::class.java))
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

    // Carousel image view container
    private fun carouselImageView() {

        binding.carousel.registerLifecycle(lifecycle)

        val list = mutableListOf<CarouselItem>()

        list.add(
            CarouselItem(
//                imageUrl = "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080"
                imageDrawable = R.drawable.digens_img
            )
        )

        list.add(
            CarouselItem(
//                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
                imageDrawable = R.drawable.digens_img
            )
        )

        list.add(
            CarouselItem(
                imageDrawable = R.drawable.digens_img
            )
        )

        binding.carousel.setData(list)
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