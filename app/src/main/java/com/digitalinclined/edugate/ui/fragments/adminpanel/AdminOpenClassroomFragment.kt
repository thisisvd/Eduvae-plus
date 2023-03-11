package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomDiscussionRecyclerAdapter
import com.digitalinclined.edugate.databinding.FragmentOpenClassroomBinding
import com.digitalinclined.edugate.models.ClassroomObjectsDataClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminOpenClassroomFragment : Fragment() {

    // TAG
    private val TAG = "AdminOpenClassroomFragment"

    // binding
    private var _binding: FragmentOpenClassroomBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: ClassroomDiscussionRecyclerAdapter

    // args
    private val args: AdminOpenClassroomFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOpenClassroomBinding.inflate(layoutInflater,container,false)

        // view visible
        binding.onlyForAdminLayout.visibility = View.VISIBLE
        binding.view.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // back
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            // add btn
            adminAddData.setOnClickListener {
                val bundle = bundleOf(
                    "classroomID" to args.classroomDetailsClass.classroomID
                )
                findNavController().navigate(R.id.action_adminOpenClassroomFragment_to_adminAddClassroomDiscussionFragment,bundle)
            }

            // delete classroom permanent
            adminDeleteRoom.setOnClickListener {
                showAlertForDeletion()
            }

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
            Firebase.firestore.collection("classroom/${args.classroomDetailsClass.classroomID}/discussionsmaterial").get()
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

            // copy classroom link
            classroomIdLink.setOnClickListener {
                val clipboard: ClipboardManager =
                    requireContext().getSystemService<ClipboardManager>() as ClipboardManager
                val clip: ClipData =
                    ClipData.newPlainText("Classroom id", args.classroomDetailsClass.classroomID)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(binding.root, "Classroom link copied!", Snackbar.LENGTH_SHORT).show()
            }


            // color
            constraintLayout2.setBackgroundColor(Color.parseColor(args.classColor))

            // classroom icon
            imageView1.apply {
                val requestOptions = RequestOptions()
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                requestOptions.centerCrop()
                if(!args.classroomDetailsClass.imageInt!!.isNullOrEmpty()) {
                    Glide.with(root)
                        .load(args.classroomDetailsClass.imageInt!!)
                        .apply(requestOptions)
                        .into(this)
                }

                DrawableCompat.setTint(
                    DrawableCompat.wrap(this.drawable),
                    Color.parseColor(args.iconColor)
                )
            }

            // class room name
            classroomNameTv.text = args.classroomDetailsClass.classroomName

            // due date
            classroomLastUpdateTv.text = "Last updated on - ${DateTimeFormatFetcher().getDateTime(args.classroomDetailsClass.classDueDate!!.toLong())}"
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

    // show alert before deletion
    private fun showAlertForDeletion() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                deleteClassroom(args.classroomDetailsClass.classroomID.toString())
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .setTitle("Delete classroom permanently?")
            .setMessage("Are you sure you want to remove '${args.classroomDetailsClass.classroomName} permanently'?")
            .create().show()
    }

    // delete classroom
    private fun deleteClassroom(classId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom").document(classId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Class removed Successfully!")
                    findNavController().popBackStack()
                    binding.progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error in removing class", e)
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}