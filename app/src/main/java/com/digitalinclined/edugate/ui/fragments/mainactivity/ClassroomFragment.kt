package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar

class ClassroomFragment : Fragment() {

    // binding
    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!

    // alert progress dialog
    private lateinit var dialog: Dialog

    // instance timer
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomBinding.inflate(layoutInflater,container,false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Classroom"

        return binding.root
    }

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // init Loading Dialog
            dialog = Dialog(requireContext())
            dialog.apply {
                setContentView(R.layout.custom_dialog)
                setCancelable(false)
                if(window != null) {
                   window!!.setBackgroundDrawable(ColorDrawable(0))
                }
            }

            // on click listeners
            setUpOnCLickListeners()
        }
    }

    // on click listeners
    private fun setUpOnCLickListeners() {
        binding.apply {
            open1.setOnClickListener {
                startProgressCountDown()
            }

            open2.setOnClickListener {
                startProgressCountDown()
            }

            open3.setOnClickListener {
                startProgressCountDown()
            }

            open4.setOnClickListener {
                startProgressCountDown()
            }
        }
    }

    // start count down for progress
    private fun startProgressCountDown() {
        dialog.show()
        if(timer != null) {
            timer!!.cancel()
        }
        timer = object: CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                dialog.dismiss()
                Snackbar.make(binding.root,"Can't connect to the classroom!",Snackbar.LENGTH_SHORT).show()
            }
        }
        timer!!.start()
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_classroom -> {
                Snackbar.make(binding.root,"Loading!...",Snackbar.LENGTH_SHORT).show()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // calling own menu for this fragment // (not required any more but not deleted because testing is not done)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.classroom_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}