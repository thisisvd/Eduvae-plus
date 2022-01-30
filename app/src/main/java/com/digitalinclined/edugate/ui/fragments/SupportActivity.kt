package com.digitalinclined.edugate.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ActivitySupportBinding

class SupportActivity : AppCompatActivity() {

    // viewBinding
    private lateinit var binding: ActivitySupportBinding

    // Nav Host Fragment
    private lateinit var navHostFragment: NavHostFragment

    // previous destination fragment
    var previousFragment: String? = null
    var fragmentPdfName = ""
    var URL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {

            // toolbar setup
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
            supportActionBar?.title = ""

            // getting intend data
            intent.apply {
                previousFragment = getStringExtra("fragment")
                fragmentPdfName = getStringExtra("pdfName").toString()
                URL = getStringExtra("url_link").toString()
            }

            // nav host fragment init
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
            val navController = navHostFragment.navController

            if (previousFragment == "universityPortal") {
                navController.navigate(R.id.webViewFragment)
            } else if(previousFragment == "aboutUs") {
                navController.navigate(R.id.aboutUsFragment)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }
}