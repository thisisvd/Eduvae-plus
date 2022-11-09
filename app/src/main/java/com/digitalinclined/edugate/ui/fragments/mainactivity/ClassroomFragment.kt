package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClassroomFragment : Fragment() {

    // TAG
    private val TAG = "ClassroomFragment"

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
                startProgressCountDown(firstTv.text.toString(),"#FEF8E2",firstLastUpdateTv.text.toString(),R.drawable.classroom_icon1)
            }

            open2.setOnClickListener {
                startProgressCountDown(secondTv.text.toString(),"#E7FAE9",secondLastUpdateTv.text.toString(),R.drawable.classroom_icon2)
            }

            open3.setOnClickListener {
                startProgressCountDown(thirdTv.text.toString(),"#EEF9FF",thirdLastUpdateTv.text.toString(),R.drawable.classroom_icon4)
            }

            open4.setOnClickListener {
                startProgressCountDown(fourTv.text.toString(),"#FDEBF9",fourLastUpdateTv.text.toString(),R.drawable.classroom_icon3)
            }
        }
    }

    // start count down for progress
    private fun startProgressCountDown(classroomName: String, classColor: String, classDueDate: String, imageInt: Int) {
        dialog.show()
        if(timer != null) {
            timer!!.cancel()
        }
        timer = object: CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                dialog.dismiss()
                val bundle = bundleOf(
                    "classroomName" to classroomName,
                    "classColor" to classColor,
                    "classDueDate" to classDueDate,
                    "imageInt" to imageInt,
                )
                findNavController().navigate(R.id.action_classroomFragment_to_openClassroomFragment,bundle)
            }
        }
        timer!!.start()
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_classroom -> {
                showClassroomLinkDialog()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // new classroom link dialog
    private fun showClassroomLinkDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Join new classroom!")
        val input = EditText(requireContext())
        input.hint = "Enter classroom link"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            dialog.show()
            if(timer != null) {
                timer!!.cancel()
            }
            timer = object: CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Can't connect to the classroom!", Snackbar.LENGTH_SHORT).show()
                }
            }
            timer!!.start()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
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