package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class QuizFragment : Fragment() {

    // binding
    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClassroomBinding.inflate(layoutInflater,container,false)

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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}