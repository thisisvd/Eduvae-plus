package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.ADMIN_USER_NAME
import com.digitalinclined.edugate.databinding.FragmentAdminLoginBinding
import com.digitalinclined.edugate.models.AdminLoginDataClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminLoginFragment : Fragment() {

    // TAG
    private val TAG = "AdminLoginFragment"

    // binding
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    // alert progress dialog
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminLoginBinding.inflate(layoutInflater, container, false)

        // init Loading Dialog
        dialog = Dialog(requireContext())
        dialog.apply {
            setContentView(R.layout.custom_dialog)
            setCancelable(false)
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(0))
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // on click listener
            onClickListeners()

        }
    }

    // setup on click listener
    private fun onClickListeners() {
        binding.apply {

            // resets
            adminIdLayout.error = null
            adminPasskeyLayout.error = null

            // login clicked
            adminLoginBtn.setOnClickListener {
                if (!isTextEmpty()) {
                    dialog.show()
                    checkAdminAndLogin(adminId.text.toString(), adminPasskey.text.toString())
                }
            }

        }
    }

    // check for valid admin and login
    private fun checkAdminAndLogin(adminID: String, adminPassKey: String) {
        var isAdminFound = false
        var adminName = ""
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("adminPanel").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val adminData = document.toObject(AdminLoginDataClass::class.java)!!
                            if (adminID == adminData.adminID.toString() && adminPassKey == adminData.adminPassKey.toString()) {
                                adminName = adminData.adminName.toString()
                                isAdminFound = true
                            }
                        }
                        if (isAdminFound) {
                            ADMIN_USER_NAME = adminName
                            val bundle = bundleOf(
                                "adminID" to adminID,
                                "adminName" to ADMIN_USER_NAME
                            )
                            dialog.dismiss()
                            findNavController().navigate(
                                R.id.action_adminLoginFragment_to_adminMainFragment,
                                bundle
                            )
                            // reformat texts
                            binding.apply {
                                adminId.setText("")
                                adminPasskey.setText("")
                            }
                        } else {
                            dialog.dismiss()
                            Snackbar.make(
                                binding.root,
                                "Invalid id or passkey!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }.addOnFailureListener { e ->
                    dialog.dismiss()
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root, "Invalid id or passkey!", Snackbar.LENGTH_LONG)
                        .show()
                }
        }
    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {
        // Activate the edittext listener's
        isTextEmptyListeners()

        var isTextEmpty = false

        binding.apply {

            if (adminId.text.isNullOrEmpty()) {
                isTextEmpty = true
                adminIdLayout.error = "Admin ID can't be empty!"
            }

            if (adminPasskey.text.isNullOrEmpty()) {
                isTextEmpty = true
                adminPasskeyLayout.error = "Admin passkey required!"
            }

            return isTextEmpty
        }
    }

    // check for empty texts listener
    private fun isTextEmptyListeners() {
        binding.apply {

            adminId.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    adminIdLayout.error = "Admin ID can't be empty!"
                } else {
                    adminIdLayout.error = null
                }
            }

            adminPasskey.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    adminPasskeyLayout.error = "Admin passkey required!"
                } else {
                    adminPasskeyLayout.error = null
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}