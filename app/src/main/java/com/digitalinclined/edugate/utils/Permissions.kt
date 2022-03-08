package com.digitalinclined.edugate.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.digitalinclined.edugate.constants.Constants.STORAGE_REQUEST_CODE

class Permissions {

    // check for storage permissions
    fun hasStoragePermissions(context: Context, activity: Activity): Boolean {
        val permissions = ContextCompat.checkSelfPermission(context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return if(permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
            false
        } else {
            true
        }

    }

}