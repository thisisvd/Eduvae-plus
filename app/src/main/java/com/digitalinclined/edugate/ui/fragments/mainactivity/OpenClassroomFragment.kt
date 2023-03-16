package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomDiscussionRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.JOINED_CLASSROOM_LIST
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.constants.Constants.USER_PROFILE_PHOTO_LINK
import com.digitalinclined.edugate.constants.Constants.USER_SEMESTER
import com.digitalinclined.edugate.constants.Constants.quizSubmissionObserver
import com.digitalinclined.edugate.databinding.FragmentOpenClassroomBinding
import com.digitalinclined.edugate.models.ClassroomObjectsDataClass
import com.digitalinclined.edugate.models.quizzesmodel.QuizQuestion
import com.digitalinclined.edugate.models.quizzesmodel.QuizSubmissionDataClass
import com.digitalinclined.edugate.models.quizzesmodel.Quizze
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

    // shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    // quiz data if exists
    private lateinit var quizze: Quizze

    // alert progress dialog
    private lateinit var dialog: Dialog

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
        _binding = FragmentOpenClassroomBinding.inflate(layoutInflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Open Classroom"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // sharedPreferences init
        sharedPreferences = (requireActivity() as MainActivity).sharedPreferences

        // init Loading Dialog
        dialog = Dialog(requireContext())
        dialog.apply {
            setContentView(R.layout.custom_dialog)
            setCancelable(false)
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(0))
            }
        }

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

            // observer
            quizSubmissionObserver.observe(viewLifecycleOwner) {
                if (it.isTaken) {
                    submitClassWorkToServer(it.userMarks!!, it.totalMarks!!)
                    quizSubmissionObserver.postValue(QuizSubmissionDataClass())
                }
            }
        }
    }

    // fetch list data from firebase
    private fun fetchClassroomFromFirebase() {
        var discussionsList = ArrayList<ClassroomObjectsDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom/${args.classroomDetailsClass.classroomID}/discussionsmaterial")
                .get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass =
                                document.toObject(ClassroomObjectsDataClass::class.java)!!
                            discussionsList.add(dataClass)
                        }
                        Log.d(TAG, "List size : ${discussionsList.size}")
                        discussionsList.reverse()
                        if (discussionsList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(discussionsList)
                        } else {
                            Snackbar.make(
                                binding.root,
                                "No discussions posted!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root, "Error occurred!", Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // fetch classroom work from firebase
    private fun fetchClassroomWorkFromFirebase() {
        var quizList = ArrayList<QuizQuestion>()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom/${args.classroomDetailsClass.classroomID}/classwork")
                .get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(QuizQuestion::class.java)!!
                            quizList.add(dataClass)
                        }
                        if (quizList.isNotEmpty()) {
                            Log.d("FOOL", "quiz size : ${quizList.size}")
                            quizze = Quizze(
                                args.classroomDetailsClass.classroomName.toString(),
                                quizList,
                                quizList.size
                            )
                        } else {
                            Snackbar.make(binding.root, "No quiz posted!", Snackbar.LENGTH_LONG)
                                .show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root, "Error occurred!", Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // set up UI
    private fun setUpUi() {
        binding.apply {

            // has any pending work
            if (args.classroomDetailsClass.hasClassWork) {
                if (args.classroomDetailsClass.classworkStudentList != null) {
                    if (!args.classroomDetailsClass.classworkStudentList!!.contains(Firebase.auth.currentUser!!.uid)) {
                        // fetch classroom data
                        fetchClassroomWorkFromFirebase()

                        submitWork.visibility = View.VISIBLE
                        submitWork.setOnClickListener {
                            if (quizze != null) {
                                val bundle = bundleOf(
                                    "quizze" to quizze,
                                    "fromFragment" to "openClassroom"
                                )
                                findNavController().navigate(R.id.quizPerformingFragment, bundle)
                            }
                        }
                    }
                }
            }

            // classroom_id_link
            classroomIdLink.setOnClickListener {
                copyClassroomId()
            }

            // color
            constraintLayout2.setBackgroundColor(Color.parseColor(args.classColor))

            // classroom icon
            imageView1.apply {
                val requestOptions = RequestOptions()
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                requestOptions.centerCrop()
                if (!args.classroomDetailsClass.imageInt!!.isNullOrEmpty()) {
                    Glide.with(root)
                        .load(args.classroomDetailsClass.imageInt!!)
                        .apply(requestOptions)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // log exception
                                Log.d("Glide", "Error loading image")
                                return false // important to return false so the error placeholder can be placed
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d("Glide", "Loaded image")
                                DrawableCompat.setTint(
                                    DrawableCompat.wrap(resource!!),
                                    Color.parseColor(args.iconColor)
                                )
                                return false
                            }
                        })
                        .into(this)
                }
            }

            // class room name
            classroomNameTv.text = args.classroomDetailsClass.classroomName

            // due date
            classroomLastUpdateTv.text =
                "Last updated on - ${DateTimeFormatFetcher().getDateTime(args.classroomDetailsClass.classDueDate!!.toLong())}"
        }
    }

    // submit classroom work
    private fun submitClassWork() {
        dialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom")
                .document(args.classroomDetailsClass.classroomID.toString())
                .update(
                    "classworkStudentList",
                    FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
                )
                .addOnSuccessListener {
                    binding.submitWork.visibility = View.GONE
                    Snackbar.make(binding.root, "Classwork submitted!", Snackbar.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error in submit work to id", e)
                    Snackbar.make(binding.root, "Error in submitting work!", Snackbar.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
        }
    }

    // add to server
    private fun submitClassWorkToServer(userMarks: Int, totalMarks: Int) {
        binding.apply {

            val data = System.currentTimeMillis() * userMarks

            val classworkData = hashMapOf(
                "userCourse" to sharedPreferences.getString(USER_COURSE, ""),
                "userImage" to sharedPreferences.getString(USER_PROFILE_PHOTO_LINK, ""),
                "userMarks" to "${userMarks}/${totalMarks}",
                "userName" to sharedPreferences.getString(USER_NAME, ""),
                "userSemester" to sharedPreferences.getString(USER_SEMESTER, ""),
                "userTimeStamp" to data
            )

            // create db in fireStore
            Firebase.firestore.collection("classroom")
                .document(args.classroomDetailsClass.classroomID.toString())
                .collection("classworkSubmissions")
                .document(Firebase.auth.currentUser!!.uid)
                .set(classworkData)
                .addOnSuccessListener {
                    Log.d(TAG, "Classwork submitted to classroom!")
                    submitClassWork()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Classwork not submitted to classroom", e)
                    dialog.dismiss()
                }
        }
    }

    // delete classroom id's
    private fun deleteClassroomIDs(classId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .update("joinedClassrooms", FieldValue.arrayRemove(classId))
                .addOnSuccessListener {
                    Log.d(TAG, "Class removed Successfully!")
                    if (JOINED_CLASSROOM_LIST.contains(classId)) {
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
    private fun setupRecyclerView() {
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
                findNavController().navigate(
                    R.id.action_openClassroomFragment_to_PDFWebViewFragment,
                    bundle
                )
            }
        }
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_discussion_classroom -> {
                val bundle = bundleOf(
                    "classroomID" to args.classroomDetailsClass.classroomID
                )
                findNavController().navigate(
                    R.id.action_openClassroomFragment_to_addClassroomDiscussionFragment,
                    bundle
                )
                true
            }
            R.id.remove_classroom -> {
                showAlertForDeletion()
                true
            }
            R.id.copy_class_id -> {
                copyClassroomId()
                true
            }
            R.id.score_board -> {
                if (args.classroomDetailsClass.classworkStudentList != null && args.classroomDetailsClass.classworkStudentList!!.isNotEmpty()) {
                    val bundle = bundleOf(
                        "classroomID" to args.classroomDetailsClass.classroomID
                    )
                    findNavController().navigate(
                        R.id.action_openClassroomFragment_to_scoreBoardFragment,
                        bundle
                    )
                    Toast.makeText(requireContext(), "View Score board", Toast.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "No Score board present", Snackbar.LENGTH_SHORT)
                        .show()
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // copy classroom link
    private fun copyClassroomId() {
        val clipboard: ClipboardManager =
            requireContext().getSystemService<ClipboardManager>() as ClipboardManager
        val clip: ClipData =
            ClipData.newPlainText("Classroom id", args.classroomDetailsClass.classroomID)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(binding.root, "Classroom link copied!", Snackbar.LENGTH_SHORT).show()
    }

    // show alert before deletion of all items
    private fun showAlertForDeletion() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE
                deleteClassroomIDs(args.classroomDetailsClass.classroomID.toString())
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .setTitle("Leave classroom?")
            .setMessage("Are you sure you want to leave '${args.classroomDetailsClass.classroomName}'?")
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