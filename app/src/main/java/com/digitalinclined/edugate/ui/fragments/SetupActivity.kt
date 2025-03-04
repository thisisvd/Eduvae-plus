package com.digitalinclined.edugate.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ActivitySetupBinding
import com.digitalinclined.edugate.utils.NetworkListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {

    // TAG
    private val TAG = "SetupActivity"

    // view binding
    private lateinit var binding: ActivitySetupBinding

    // dialog
    private lateinit var networkDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var networkDialog: AlertDialog

    // network listener
    private lateinit var networkListener: NetworkListener

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        binding = ActivitySetupBinding.inflate(layoutInflater)

        //Screen orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // setup dialog
        dialogSetupForNoNetwork()

        // Nav controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, _, _ ->
            if (controller.currentDestination?.displayName == "com.digitalinclined.edugate:id/splashScreenFragment") {
                Log.d(TAG, controller.currentDestination?.displayName.toString())
            } else {
                checkForNetworkConnectivity()
            }
        }

    }

    // check for network connectivity
    private fun checkForNetworkConnectivity() {
        // network listener init and collecting values from mutableStateFlow
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@SetupActivity)
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    if (status) {
                        networkDialog.dismiss()
                    } else {
                        networkDialog.show()
                    }
                }
        }
    }

    // dialog to check for account
    private fun dialogSetupForNoNetwork() {
        networkDialogBuilder = MaterialAlertDialogBuilder(this).apply {
            setIcon(R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24)
            setTitle("Network not found!")
            titleColor = Color.parseColor("#A67E40")
            setMessage(
                "Facing an issue with the network!. " +
                        "Please establish internet connection so you can proceed further. Please TURN ON -"
            )
            setPositiveButton("WIFI") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            setNeutralButton("MOBILE NETWORK") { _, _ ->
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }
            setCancelable(false)
        }
        networkDialog = networkDialogBuilder.create()
    }

    // Chrome custom tab to be used by every required fragment
    fun launchChromeCustomTab(url: String) {
        if (!url.isNullOrEmpty()) {

            // load page
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()

            // change toolbar color
            val defaultColors = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.theme_color))
                .build()
            builder.setDefaultColorSchemeParams(defaultColors)

            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } else {
            Log.d(TAG, "Chrome custom tab : null link provided!")
        }
    }

    override fun onResume() {
        super.onResume()
        checkForNetworkConnectivity()
    }

}