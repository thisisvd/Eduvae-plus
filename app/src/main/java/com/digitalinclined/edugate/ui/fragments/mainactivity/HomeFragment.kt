package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.ui.fragments.SetupActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment: Fragment(R.layout.fragment_home) {

    // viewBinding
    private lateinit var binding: FragmentHomeBinding

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {

            // signOut
            signOut.setOnClickListener{
                firebaseAuth.signOut()
                startActivity(Intent(requireActivity(),SetupActivity::class.java))
                requireActivity().finish()
            }

        }

    }

}