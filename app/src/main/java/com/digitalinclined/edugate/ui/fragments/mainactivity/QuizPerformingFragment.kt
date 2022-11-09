package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.databinding.FragmentQuizPerformingBinding
import com.digitalinclined.edugate.models.DiscussionDataClass
import com.digitalinclined.edugate.models.QuizDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class QuizPerformingFragment : Fragment() {

    // binding
    private var _binding: FragmentQuizPerformingBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    private val dbReference = Firebase.firestore

    // instance vars
    private var selectedOption: String = ""
    private var counter: Int = 1

    private var questionName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizPerformingBinding.inflate(layoutInflater,container,false)

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Quiz"
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // click listeners
            setUpClickListeners()

            // fetch quiz
            fetchQuizFromServer()
        }
    }

    private fun fetchQuizFromServer() {
        lifecycleScope.launch {

        }
    }

    private fun setUpClickListeners() {
        binding.apply {

            // radio click listener
            quizRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
                selectedOption = binding.root.findViewById<RadioButton>(i).text.toString()
            }

            // next listener
            nextButton.setOnClickListener {
                if(counter < 6) {
                    if(selectedOption == questionName) {
                        Toast.makeText(requireContext(), selectedOption, Toast.LENGTH_SHORT).show()
                        selectedOption = ""
                        counter++
                    } else {
                        Toast.makeText(requireContext(), "Wrong2", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "end", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}