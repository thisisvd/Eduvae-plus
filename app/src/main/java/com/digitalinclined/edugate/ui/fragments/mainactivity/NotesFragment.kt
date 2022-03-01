package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.NotesRecyclerAdapter
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentNotesBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.restapi.models.notes.Note
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class NotesFragment : Fragment(R.layout.fragment_notes) {

    // TAG
    private val TAG = "NotesFragment"

    // viewBinding
    private lateinit var binding: FragmentNotesBinding

    // Adapters
    lateinit var recyclerAdapter: NotesRecyclerAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    // view model
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Notes"

        // calling notes
        viewModel.getNotes("btech",5)

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // getting the name
        binding.name.text = (requireActivity() as MainActivity).sharedPreferences.getString(Constants.USER_NAME,"")

        // set up recycler view
        setupRecyclerView()

        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener { notes ->
                val bundle = bundleOf(
                    "previousFragment" to "NotesFragment",
                    "Notes" to notes
                )
                findNavController().navigate(R.id.PDFFragment,bundle)
            }
        }

        // viewModels
        viewModelObservers()

    }

    // viewModel observers
    private fun viewModelObservers() {
        binding.apply {

            // notes observer
            viewModel.getNotesDetail.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        response.data?.let { notesDetails ->
                            if (notesDetails.status == 200) {
                                if (notesDetails.notes.isNotEmpty()) {
                                    Log.d(TAG, "${notesDetails.notes.size}")
                                    recyclerAdapter.differ.submitList(notesDetails.notes)
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "Error occurred while loading data! ${response.message}")
                    }
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "Loading!")
                    }
                }
            }

        }
    }


//    // saving file to external storage
//    fun saveToExternalStorage(note: Note) {
//        val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Edugate/notes"
//
//        var byteArray: ByteArray = Base64.decode(note.notesPDF, Base64.NO_WRAP)
//
//        Log.d("TAGU", Environment.getExternalStorageDirectory().absolutePath.toString())
//
//        // Date formatter
//        var format = SimpleDateFormat("dd_mm_yyyy_hh_mm_")
//        Log.d(TAG,"${format.format(Date())}${note.notesName}")
//
//        try {
//            var dir = File(fullPath);
//            if (!dir.exists()) {
//                dir.mkdirs()
//            }
//            var file = File(fullPath, "${format.format(Date())}${note.notesName}")
//            if (file.exists()) {
//                file.delete()
//            }
//            file.createNewFile()
//            var fOut = FileOutputStream(file)
//            fOut.write(byteArray)
//            fOut.flush()
//            fOut.close()
//        } catch (e: Exception) {
//            Log.e("saveToExternalStorage()", e.message.toString())
//        }
//    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = NotesRecyclerAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}