package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAdminMainBinding
import com.digitalinclined.edugate.ui.fragments.SetupActivity
import com.google.android.material.snackbar.Snackbar

class AdminMainFragment : Fragment() {

    // TAG
    private val TAG = "AdminMainFragment"

    // binding
    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!

    // nav-args
    private val args: AdminMainFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminMainBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // set up user name
            adminName.text = args.adminName

            // setup on click listeners
            onClickListeners()

        }
    }

    // setup on click listeners
    private fun onClickListeners() {
        binding.apply {

            // back
            homeNewsButton1.setOnClickListener {
                (activity as SetupActivity).launchChromeCustomTab("http://cryptobeetle.centralindia.cloudapp.azure.com//privacypoliciespage")
            }

            // logout
            logoutBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            // create new classroom click
            createNewClassroom.setOnClickListener {
                findNavController().navigate(R.id.action_adminMainFragment_to_adminCreateClassroomFragment)
            }

            // manage class rooms
            editClassroom.setOnClickListener {
                findNavController().navigate(R.id.action_adminMainFragment_to_adminClassroomFragment)
            }

            // upload notes & questions
            uploadBtn.setOnClickListener {
                findNavController().navigate(R.id.action_adminMainFragment_to_uploadVideoFragment2)
            }

            // post notifications
            createNotify.setOnClickListener{
                findNavController().navigate(R.id.action_adminMainFragment_to_adminCreateNotificationFragment)
            }

            // upload notes & questions
            manageQuiz.setOnClickListener {
                Snackbar.make(binding.root,"Error in managing quiz!",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}