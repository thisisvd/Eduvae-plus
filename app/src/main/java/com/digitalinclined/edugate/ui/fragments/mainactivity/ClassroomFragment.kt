package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomDiscussionRecyclerAdapter
import com.digitalinclined.edugate.adapter.ClassroomRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants.JOINED_CLASSROOM_LIST
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClassroomFragment : Fragment() {

    // TAG
    private val TAG = "ClassroomFragment"

    // binding
    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!

    // recycler adapter
    private lateinit var recyclerAdapter: ClassroomRecyclerAdapter

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

            // setup recycler
            setupRecyclerView()

            // fetch classrooms data
            fetchClassroomFromFirebase()
        }
    }

    // fetch classrooms data from firebase
    private fun fetchClassroomFromFirebase() {
        if (!JOINED_CLASSROOM_LIST.isNullOrEmpty()) {
            var classroomList = ArrayList<ClassroomDetailsClass>()
            lifecycleScope.launch(Dispatchers.IO) {
                Firebase.firestore.collection("classroom").get()
                    .addOnSuccessListener { documentResult ->
                        if (documentResult != null) {
                            Log.d(
                                TAG,
                                "DocumentSnapshot data size : ${documentResult.documents.size}"
                            )
                            for (document in documentResult) {
                                val dataClass =
                                    document.toObject(ClassroomDetailsClass::class.java)!!
                                if (JOINED_CLASSROOM_LIST.contains(dataClass.classroomID)) {
                                    classroomList.add(dataClass)
                                }
                            }

                            Log.d(TAG, "List size : ${classroomList.size}")

                            if (classroomList.isNotEmpty()) {
                                recyclerAdapter.differ.submitList(classroomList)
                            } else {
                                Snackbar.make(
                                    binding.root,
                                    "No Classroom available!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

//                        binding.progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener { e ->
                        Log.d(TAG, "Error adding document", e)
                        Snackbar.make(binding.root, "Error occurred!", Snackbar.LENGTH_LONG).show()
//                    binding.progressBar.visibility = View.GONE
                    }
            }
        }
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
        input.apply {
            hint = "Enter classroom link"
            inputType = InputType.TYPE_CLASS_TEXT
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(16, 0, 16, 0)
            this.layoutParams = params
        }
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

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = ClassroomRecyclerAdapter()
        binding.apply {
            classroomRecyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

        // on click listener
        recyclerAdapter.apply {
            setClassroomOnItemClickListener { dataClass ->
                val bundle = bundleOf(
                    "classroomName" to dataClass.classroomName,
                    "classColor" to dataClass.classColor,
                    "classDueDate" to dataClass.classDueDate,
                    "imageInt" to 1234,
                    "classroomID" to dataClass.classroomID
                )
                findNavController().navigate(R.id.action_classroomFragment_to_openClassroomFragment,bundle)
            }
        }
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