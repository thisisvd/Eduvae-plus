package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentCompleteProfileBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class CompleteProfileFragment : Fragment(R.layout.fragment_complete_profile) {

    // ROUGH LIST -
    private var roughCourseList = ArrayList<String>()
    private var roughYearList = ArrayList<String>()

    // viewBinding
    private lateinit var binding: FragmentCompleteProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCompleteProfileBinding.bind(view)

        // implement ROUGH DATA to spinners
        roughCourseList.add("BBA")
        roughCourseList.add("MBA")
        roughCourseList.add("MCA")
        roughCourseList.add("B.TECH")

        roughYearList.add("First Year")
        roughYearList.add("Second Year")
        roughYearList.add("Third Year")
        roughYearList.add("Fourth Year")

        // setting adapter method
        adapterForSpinners()

        // autocomplete checks
        checksForAutoCompleteTexts()

        binding.apply {

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_completeProfileFragment_to_loginFragment)
            }

            // complete sign up button onClick listener
            completeButton.setOnClickListener {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }

        }

    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
        binding.apply {

            // adapter for course list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                roughCourseList
            )

            // course spinner adapter
            chooseCourseAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            // adapter for year list
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                roughYearList
            )

            // course spinner adapter
            yearAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

        }
    }

    // Checking for autocomplete and correct text views
    private fun checksForAutoCompleteTexts() {
        binding.apply {
            chooseCourseAutoTextView.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        binding.chooseCourseAutoTextView.setText("MBA")
                    }
                }
            yearAutoTextView.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        binding.yearAutoTextView.setText("First Year")
                    }
                }
        }
    }

}