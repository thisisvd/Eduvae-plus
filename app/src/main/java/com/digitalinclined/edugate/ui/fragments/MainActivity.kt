package com.digitalinclined.edugate.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // viewBinding
    private lateinit var binding: ActivityMainBinding

    // Nav Host Fragment
    private lateinit var navHostFragment: NavHostFragment

    // drawer layout
    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    // Bottom Navigation Bar Setup
    private fun bottomNavigationDrawer() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.apply {
            val popupMenu = PopupMenu(context, null)
            popupMenu.inflate(R.menu.nav_bar_menu)
            val menu = popupMenu.menu
            // setting bottom nav bar things with nav controller
            setupWithNavController(menu,navController)

            // Re-selecting bottom nav bar items
            setOnItemReselectedListener {
                // not to be implemented
            }

        }
    }

    // Navigation Drawer Setup
    private fun navigationDrawerListener() {
        // nav drawer setup and adding toggle button
        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(applicationContext,R.color.white)
//        toggle.isDrawerIndicatorEnabled = false
//        val drawable = ResourcesCompat.getDrawable(
//            resources,
//            R.drawable.nav_drawer_icon,applicationContext.theme
//        )
//        toggle.setHomeAsUpIndicator(drawable)
        toggle.syncState()
    }

    // Toolbar Menu Item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.navDrawerCloseIcon -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}