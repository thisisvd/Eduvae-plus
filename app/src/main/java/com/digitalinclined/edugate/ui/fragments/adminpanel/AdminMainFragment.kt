package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAdminMainBinding

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
                Toast.makeText(requireContext(),"Security policy",Toast.LENGTH_SHORT).show()
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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}