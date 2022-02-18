package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.NotificationAdapter
import com.digitalinclined.edugate.adapter.PreviousYearsPaperAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentNotificationBinding
import com.digitalinclined.edugate.models.NotificationDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity

class NotificationFragment : Fragment() {

    // viewBinding
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: NotificationAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Notifications"

        // toggle setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // recycler init
            setupRecyclerView()

            // on Click Listeners
            setUpOnClickListeners()

            // viewModels
            viewModelObservers()

        }
    }

    // set up on Click listeners
    private fun setUpOnClickListeners() {
        binding.apply {
            // on click listener
            recyclerAdapter.apply {
                setOnItemClickListener { value ->
                    Toast.makeText(requireContext(), "${value.viewDetails} is clicked!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // rough - viewModels
    private fun viewModelObservers() {
        val list = arrayListOf(
            NotificationDataClass("Received 2 minutes ago","4 Jan 2022",
            "Notification title","Notification content","View Details"),
            NotificationDataClass("Received 2 minutes ago","4 Jan 2022",
                "Notification title","Notification content","View Details"),
            NotificationDataClass("Received 2 minutes ago","4 Jan 2022",
                "Notification title","Notification content","View Details"),
        )
        recyclerAdapter.differ.submitList(list)
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = NotificationAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}