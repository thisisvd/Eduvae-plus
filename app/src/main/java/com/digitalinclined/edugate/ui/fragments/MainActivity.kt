package com.digitalinclined.edugate.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.FOLLOWING_USER_ID
import com.digitalinclined.edugate.constants.Constants.INDIAN_CITY_DATA
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.constants.Constants.SHARED_PREFERENCES_NAME
import com.digitalinclined.edugate.constants.Constants.STORAGE_REQUEST_CODE
import com.digitalinclined.edugate.constants.Constants.USER_CITY
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.constants.Constants.USER_PHONE
import com.digitalinclined.edugate.constants.Constants.USER_PROFILE_PHOTO_LINK
import com.digitalinclined.edugate.constants.Constants.USER_YEAR
import com.digitalinclined.edugate.databinding.ActivityMainBinding
import com.digitalinclined.edugate.models.UserProfile
import com.digitalinclined.edugate.utils.NetworkListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    // TAG
    private val TAG = "MainActivity"

    // viewBinding
    private lateinit var binding: ActivityMainBinding

    // Nav Host Fragment
    private lateinit var navHostFragment: NavHostFragment

    // Shared Preference
    lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")

    // toggle button
    lateinit var toggle: ActionBarDrawerToggle

    // network listener
    private lateinit var networkListener: NetworkListener
    private var isNetworkConnected = true

    // live data toggle button
    private var toggleEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Screen orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // init shared pref
        sharedPreferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // check for network connectivity
        checkForNetworkConnectivity()

        // FETCH CITY DB IN PRIMARY MEMORY
        readIndianCityJson()

        // fetching users data
        fetchFirebaseUserData()

        binding.apply {

            // toolbar setup
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""

            // Bottom Navigation Bar And nav controller Setup
            bottomNavigationDrawer()

            // Navigation Drawer Listener
            navigationDrawerListener()

            // toggle livedata observer
            toggleEnabled.observe(this@MainActivity){ isToggleMenu ->
                if(isToggleMenu) {
                    val drawable = getDrawable(R.drawable.ic_baseline_menu_24)
                    toggle.setHomeAsUpIndicator(drawable)
                    IS_BACK_TOOLBAR_BTN_ACTIVE = false
                }
            }

        }
    }

    // check for network connectivity
    private fun checkForNetworkConnectivity() {
        // network listener init and collecting values from mutableStateFlow
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@MainActivity)
                .collect { status ->
                    Log.d("NetworkListener",status.toString())
                    if(status) {
                        binding.noNetworkLayout.visibility = View.GONE
                        isNetworkConnected = true
                        fetchFirebaseUserData()
                    } else {
                        binding.noNetworkLayout.visibility = View.VISIBLE
                        isNetworkConnected = false
                    }
                }
        }

        binding.apply {

            // open wifi settings panel
            turnOnWifi.setOnClickListener {
                if(!isNetworkConnected) {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }

            // open mobile settings panel
            turnOnMobileNetwork.setOnClickListener {
                if(!isNetworkConnected) {
                    startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                }
            }

        }

    }

    // fetching the user data from firebase and saving it in to shared preferences
    fun fetchFirebaseUserData() {
        if(isNetworkConnected) {
            lifecycleScope.launch(Dispatchers.IO) {
                dbReference.document(firebaseAuth.currentUser!!.uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            val userProfile = document.toObject(UserProfile::class.java)!!

                            // add following users
                            if (userProfile.following != null) {
                                Log.d(TAG, "${userProfile.following!!.size}")
                                FOLLOWING_USER_ID.postValue(userProfile.following)
                            }

                            if (userProfile != null) {
                                sharedPreferences.edit()
                                    .putString(USER_NAME, userProfile.name)
                                    .putString(USER_EMAIL, userProfile.email)
                                    .putString(USER_PHONE, userProfile.phone)
                                    .putString(USER_COURSE, userProfile.course)
                                    .putString(USER_YEAR, userProfile.year)
                                    .putString(USER_CITY, userProfile.city)
                                    .putString(
                                        USER_PROFILE_PHOTO_LINK,
                                        userProfile.profilephotolink
                                    )
                                    .apply()

                                // Show snack bar when open main activity
                                Snackbar.make(
                                    binding.root,
                                    "Logged in as ${userProfile.name}!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            if(userProfile.course.isNullOrEmpty() || userProfile.year.isNullOrEmpty()) {
                                navHostFragment.navController.navigate(R.id.completeProfileFragment2)
                                binding.apply {
                                    toolbar.visibility = View.GONE
                                    viewTop.visibility = View.GONE
                                }

                            }

                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }
    }

    // Bottom Navigation Bar Setup
    private fun bottomNavigationDrawer() {
        binding.apply {

            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
            val navController = navHostFragment.navController
            bottomNavigationView.apply {
                val popupMenu = PopupMenu(context, null)
                popupMenu.inflate(R.menu.nav_bar_menu)
                val menu = popupMenu.menu

                // setting bottom nav bar things with nav controller
                setupWithNavController(menu, navController)

                // Re-selecting bottom nav bar items
                setOnItemReselectedListener {
                    // not to be implemented
                }
            }

            // On navigation changes listener
            navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->

                    when (destination.id) {
                        // when these fragments will open toolbar will not be visible
                        R.id.discussionFragment, R.id.homeFragment, R.id.subjectsFragment -> {
                            // bottom nav bar visibility VISIBLE
                            bottomNavigationView.visibility = View.VISIBLE
                            viewBottom.visibility = View.VISIBLE
                            toggleEnabled.postValue(true)
                        }
                        else -> {
                            // bottom nav bar visibility INVISIBLE/GONE
                            bottomNavigationView.visibility = View.GONE
                            viewBottom.visibility = View.GONE
                            toggleEnabled.postValue(false)
                        }

                    }
                }
        }
    }

    // Navigation Drawer Setup
    private fun navigationDrawerListener() {
        binding.apply {
            // nav drawer setup and adding toggle button
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.drawerArrowDrawable.color =
                ContextCompat.getColor(applicationContext, R.color.white)
            toggle.isDrawerIndicatorEnabled = false
            val drawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_menu_24, applicationContext.theme
            )
            toggle.setHomeAsUpIndicator(drawable)
            toggle.syncState()

            // nav header close icon listener
            mainNavView.getHeaderView(0).findViewById<ImageView>(R.id.navDrawerCloseIcon)
                .setOnClickListener {
                    // close drawer layout after clicking
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

            // Navigation Drawer Item Click Listener
            mainNavView.setNavigationItemSelectedListener { item ->

                // close drawer layout after clicking
                drawerLayout.closeDrawer(GravityCompat.START)

                // nav builder for graph
                val navBuilder = NavOptions.Builder()
                navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.wait_animation)
                    .setPopEnterAnim(R.anim.wait_animation).setPopExitAnim(R.anim.fade_out)

                when(item.itemId) {
                    // Previous Question Papers
                    R.id.previousQuestionPapers -> {
                        navHostFragment.findNavController().navigate(R.id.previousYearPapersFragment,null,navBuilder.build())
                        true
                    }
                    // Syllabus
                    R.id.syllabus -> {
                        navHostFragment.findNavController().navigate(R.id.syllabusFragment,null,navBuilder.build())
                        true
                    }
                    // Notes
                    R.id.notes -> {
                        navHostFragment.findNavController().navigate(R.id.notesFragment,null,navBuilder.build())
                        true
                    }
                    // University Portal
                    R.id.universityPortal -> {
                        val bundle = bundleOf(
                            "url" to "https://www.rgpv.ac.in/",
                        )
                        navHostFragment.findNavController().navigate(R.id.webViewFragment,bundle,navBuilder.build())
                        true
                    }
                    // Notification
                    R.id.notification -> {
                        navHostFragment.findNavController().navigate(R.id.notificationFragment,null,navBuilder.build())
                        true
                    }
                    // Job Update
                    R.id.jobUpdates -> {
                        val bundle = bundleOf(
                            "url" to "https://www.naukri.com/",
                        )
                        navHostFragment.findNavController().navigate(R.id.webViewFragment,bundle,navBuilder.build())
                        true
                    }
                    // About Us
                    R.id.aboutUs -> {
                        navHostFragment.findNavController().navigate(R.id.aboutUsFragment)
                        true
                    }
                    // Join Our Telegram
                    R.id.joinOurTelegram -> {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/RGPVExams"))
                        startActivity(intent)
                        true
                    }
                    // Signing out the user
                    R.id.signOut -> {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@MainActivity,SetupActivity::class.java))
                        finish()
                        sharedPreferences.edit().clear().commit()
                        true
                    }
                    R.id.navDrawerCloseIcon -> {
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                    else -> true
                }
            }
        }
    }

    // read city db from init json
    private fun readIndianCityJson() {

        var json: String? = null

        try {
            val inputStream: InputStream = this.assets.open("indian_cities.json")
            json = inputStream.bufferedReader().use { it.readText() }

            var jsonArray = JSONArray(json)

            for(i in 0 until jsonArray.length()) {
                var jsonObj = jsonArray.getString(i)
                INDIAN_CITY_DATA.add(jsonObj)
            }

        }catch (e: IOException) {
            Log.d(TAG,e.message.toString())
        }

    }

    // Toolbar Menu Item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.apply {
            return when (item.itemId) {
                android.R.id.home -> {
                    if(IS_BACK_TOOLBAR_BTN_ACTIVE) {
                        navHostFragment.navController.popBackStack()
//                        toggle.isDrawerIndicatorEnabled = false
                        val drawable = getDrawable(R.drawable.ic_baseline_menu_24)
                        toggle.setHomeAsUpIndicator(drawable)
                        IS_BACK_TOOLBAR_BTN_ACTIVE = false
                        true
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START)
                        true
                    }
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            STORAGE_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"You need the storage permission to save pdf.", Toast.LENGTH_SHORT).show()
                } else {
                    // do nothing on granted
                }
            }
        }

    }

}