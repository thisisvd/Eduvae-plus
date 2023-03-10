package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomDiscussionRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.JOINED_CLASSROOM_LIST
import com.digitalinclined.edugate.databinding.FragmentOpenClassroomBinding
import com.digitalinclined.edugate.models.ClassroomObjectsDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OpenClassroomFragment : Fragment() {

    // TAG
    private val TAG = "OpenClassroomFragment"

    // binding
    private var _binding: FragmentOpenClassroomBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: ClassroomDiscussionRecyclerAdapter

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // args
    private val args: OpenClassroomFragmentArgs by navArgs()

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOpenClassroomBinding.inflate(layoutInflater,container,false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Open Classroom"
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

            // set up UI
            setUpUi()

            // Recycler view setup
            setupRecyclerView()

            // fetch data
            fetchClassroomFromFirebase()
        }
    }

    // fetch list data from firebase
    private fun fetchClassroomFromFirebase() {
        var discussionsList = ArrayList<ClassroomObjectsDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom/${args.classroomID}/discussionsmaterial").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(ClassroomObjectsDataClass::class.java)!!
                            discussionsList.add(dataClass)
                        }
                        Log.d(TAG, "List size : ${discussionsList.size}")
                        discussionsList.reverse()
                        if (discussionsList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(discussionsList)
                        } else {
                            Snackbar.make(binding.root,"No discussions posted!", Snackbar.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root,"Error occurred!", Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // set up UI
    private fun setUpUi() {
        binding.apply {

            // copy room id
            copyRoomId.setOnClickListener {
                val clipboard: ClipboardManager = requireContext().getSystemService<ClipboardManager>() as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Classroom id", args.classroomID)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(binding.root,"Classroom link copied!",Snackbar.LENGTH_SHORT).show()
            }

            // color
            constraintLayout2.setBackgroundColor(Color.parseColor(args.classColor))

            // classroom icon
            imageView1.apply {
                setImageResource(args.imageInt!!)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(this.drawable),
                    Color.parseColor(args.iconColor)
                )
            }

            // class room name
            classroomNameTv.text = args.classroomName

            // due date
            classroomLastUpdateTv.text = "Last updated on - ${DateTimeFormatFetcher().getDateTime(args.classDueDate.toLong())}"
        }
    }

    // delete classroom id's
    private fun deleteClassroomIDs(classId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .update("joinedClassrooms", FieldValue.arrayRemove(classId))
                .addOnSuccessListener {
                    Log.d(TAG, "Class removed Successfully!")
                    if(JOINED_CLASSROOM_LIST.contains(classId)) {
                        val index = JOINED_CLASSROOM_LIST.indexOf(classId)
                        JOINED_CLASSROOM_LIST.removeAt(index)
                        findNavController().popBackStack()
                    }
                    binding.progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error in removing class", e)
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = ClassroomDiscussionRecyclerAdapter()
        binding.apply {
            classroomDiscussionRecyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setClassroomOnItemClickListener { pdfLink ->
                val bundle = bundleOf(
                    "pdfLink" to pdfLink
                )
                findNavController().navigate(R.id.action_openClassroomFragment_to_PDFWebViewFragment,bundle)
            }
        }
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_discussion_classroom -> {
                val bundle = bundleOf(
                    "classroomID" to args.classroomID
                )
                findNavController().navigate(R.id.action_openClassroomFragment_to_addClassroomDiscussionFragment,bundle)
                true
            }
            R.id.remove_classroom -> {
                showAlertForDeletion()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // show alert before deletion of all items
    private fun showAlertForDeletion() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                deleteClassroomIDs(args.classroomID)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .setTitle("Remove classroom?")
            .setMessage("Are you sure you want to remove '${args.classroomName}'?")
            .create().show()
    }

    // calling own menu for this fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_discussion_classroom_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}