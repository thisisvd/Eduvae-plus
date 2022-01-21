package com.digitalinclined.edugate.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.SHARED_PREFERENCES_NAME
import com.digitalinclined.edugate.constants.Constants.USER_CITY
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.constants.Constants.USER_PHONE
import com.digitalinclined.edugate.constants.Constants.USER_PROFILE_PHOTO_LINK
import com.digitalinclined.edugate.constants.Constants.USER_YEAR
import com.digitalinclined.edugate.databinding.ActivityMainBinding
import com.digitalinclined.edugate.models.UserProfile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force No Night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Screen orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // init shared pref
        sharedPreferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

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

        }
    }

    // fetching the user data from firebase and saving it in to shared preferences
    fun fetchFirebaseUserData() {
        dbReference.document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val userProfile = document.toObject(UserProfile::class.java)!!

                    if(userProfile != null) {
                        sharedPreferences.edit()
                            .putString(USER_NAME, userProfile.name)
                            .putString(USER_EMAIL, userProfile.name)
                            .putString(USER_PHONE, userProfile.name)
                            .putString(USER_COURSE, userProfile.name)
                            .putString(USER_YEAR, userProfile.name)
                            .putString(USER_CITY, userProfile.name)
                            .putString(USER_PROFILE_PHOTO_LINK, userProfile.name)
                            .apply()

                        // Show snack bar when open main activity
                        Snackbar.make(
                            binding.root,
                            "Logged in as ${userProfile.name}!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
                        R.id.myProfile -> {
                            toolbar.visibility = View.GONE
                            viewTop.visibility = View.GONE
                        }
                        else -> {
                            // toolbar visibility VISIBLE
                            toolbar.visibility = View.VISIBLE
                            viewTop.visibility = View.VISIBLE
                        }

                    }
                }
        }
    }

    // Navigation Drawer Setup
    private fun navigationDrawerListener() {
        binding.apply {
            // nav drawer setup and adding toggle button
            val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.drawerArrowDrawable.color =
                ContextCompat.getColor(applicationContext, R.color.white)
//        toggle.isDrawerIndicatorEnabled = false
//        val drawable = ResourcesCompat.getDrawable(
//            resources,
//            R.drawable.nav_drawer_icon,applicationContext.theme
//        )
//        toggle.setHomeAsUpIndicator(drawable)
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
                        Toast.makeText(this@MainActivity,"University Portal",Toast.LENGTH_SHORT).show()
                        true
                    }
                    // Job Update
                    R.id.jobUpdates -> {
                        navHostFragment.findNavController().navigate(R.id.jobUpdateFragment,null,navBuilder.build())
                        true
                    }
                    // About Us
                    R.id.aboutUs -> {
                        navHostFragment.findNavController().navigate(R.id.aboutUsFragment,null,navBuilder.build())
                        true
                    }
                    // Join Our Telegram
                    R.id.joinOurTelegram -> {
                        Toast.makeText(this@MainActivity,"Join Our Telegram",Toast.LENGTH_SHORT).show()
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

    // Toolbar Menu Item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.apply {
            return when (item.itemId) {
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

}