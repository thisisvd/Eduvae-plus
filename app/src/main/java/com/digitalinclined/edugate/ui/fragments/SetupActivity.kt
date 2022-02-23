package com.digitalinclined.edugate.ui.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ActivitySetupBinding
import com.digitalinclined.edugate.utils.NetworkListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {

    // viewBinding
    private lateinit var binding: ActivitySetupBinding

    // dialog
    private lateinit var networkDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var networkDialog: AlertDialog

    // network listener
    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        binding = ActivitySetupBinding.inflate(layoutInflater)

        //Screen orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // setup dialog
        dialogSetupForNoNetwork()

        // check for network connectivity
        checkForNetworkConnectivity()

        // Nav controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

    }

    // check for network connectivity
    private fun checkForNetworkConnectivity() {
        // network listener init and collecting values from mutableStateFlow
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(this@SetupActivity)
                .collect { status ->
                    Log.d("NetworkListener",status.toString())
                    if(status) {
                        networkDialog.dismiss()
                    } else {
                        networkDialog.show()
                    }
                }
        }
    }

    // dialog to check for account
    private fun dialogSetupForNoNetwork(){
        networkDialogBuilder = MaterialAlertDialogBuilder(this).apply {
            setTitle("Network not found!")
                .setMessage("Facing an issue with the network!. " +
                        "Please establish internet connection so you can proceed further.")
            setPositiveButton("TURN ON WIFI") { _,_ ->
                Toast.makeText(this@SetupActivity,"TURN ON WIFI",Toast.LENGTH_SHORT).show()
            }
            setCancelable(false)
        }
        networkDialog = networkDialogBuilder.create()
    }

}