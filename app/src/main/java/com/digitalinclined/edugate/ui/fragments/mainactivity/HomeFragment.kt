package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SliderImageAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.FETCHED_DATA_CLASS
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.models.FetchDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.fragments.YoutubeVideoActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("extraData")

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // fetching banners
        if(FETCHED_DATA_CLASS != null) {
            sliderImageView()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                dbReference.document("data").get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("REALTAG", "DocumentSnapshot data: ${document.data}")
                            FETCHED_DATA_CLASS = document.toObject(FetchDataClass::class.java)!!
                            if (FETCHED_DATA_CLASS != null) {
                                Log.d("REALTAG", FETCHED_DATA_CLASS!!.banner!!.size.toString())
                                sliderImageView()
                            }
                        }
                    }
            }
        }

        // user name for the top
        getUserName()

        // on click listeners
        onClickListeners()

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
                    "url" to "https://www.naukri.com/",
                    "urlSiteName" to "Naukri.com"
                )
                findNavController().navigate(R.id.webViewFragment,bundle)
            }

            // discussion form
            discussionItemBtn.setOnClickListener {
                findNavController().navigate(R.id.discussionFragment)
            }

            // share app
            shareAppLayout.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = "Share, discuss, learn, post and do many more exciting things only in one app.\n\nDownload the app now via link.\n\n${getString(R.string.app_name)} : Link"
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
                    "url" to "https://www.rgpv.ac.in/",
                    "urlSiteName" to "RGPV University"
                )
                findNavController().navigate(R.id.webViewFragment,bundle)
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
            val name = (requireActivity() as MainActivity).sharedPreferences.getString(USER_NAME,"")!!.split(" ")
            userName.text = "${name[0]} "
        }
    }

    // Slider image view container
    private fun sliderImageView() {
        binding.apply {

            // Slider Image Adapter
            val adapter = SliderImageAdapter(requireContext(),webViewProgressBar)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}