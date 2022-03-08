package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.utils.Permissions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentPDFBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class PDFFragment : Fragment() {

    // TAG
    private val TAG = "PDFFragment"

    // viewBinding
    private var _binding: FragmentPDFBinding? = null
    private val binding get() = _binding!!

    // nav args
    private val args: PDFFragmentArgs by navArgs()

    // previous Fragment
    var previousFragment: String? = null
    var fragmentPdfName = ""
    var pdfUri = ""

    // PDF BASIS VARIABLES
    var pdfName: String? = null
    var stringBase64: String? = null
    var fragmentName: String? = null

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPDFBinding.inflate(inflater, container, false)

        // getting values from args
        previousFragment = args.previousFragment
        fragmentPdfName = args.fragmentPdfName
        pdfUri = args.pdfUri

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "PDF Viewer"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // pdf view
            if(previousFragment != null) {
                when (previousFragment) {
                    "SyllabusFragment" -> {
                        Toast.makeText(requireContext(),"This is vd!",Toast.LENGTH_SHORT).show()
                    }
                    "NotesFragment" -> {
                        // change the title bar
                        val note = args.notes
                        if(note != null) {
                            if (note.notesPDF != null) {
                                // setting toolbar name
                                (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text =
                                    note.notesName

                                // set up PDF BASE VARIABLES INIT
                                pdfName = note.notesName
                                stringBase64 = note.notesPDF
                                fragmentName = "Notes"

                                // string base 64 to byte array
                                var base64StringAsByteArray =
                                    Base64.decode(note.notesPDF, Base64.NO_WRAP)

                                // loading a pdf view
                                pdfView.fromBytes(base64StringAsByteArray)
                                    .load()
                            }
                        }
                    }
                    "PreviousYearPapersFragment" -> {
                        if (fragmentPdfName == "paper") {
                            pdfView.fromAsset("history_paper.pdf").load()
                        }else {
                            pdfView.fromAsset("history_syllabus.pdf").load()
                        }
                    }
                    "AddDiscussionFragment" -> {
                        pdfView.fromUri(pdfUri.toUri())
                            .defaultPage(0)
                            .spacing(10)
                            .load()
                    }
                }
            }
        }
    }

    // saving file to external storage
    private fun saveToExternalStorage(pdfName: String, stringBase64: String, fragmentName: String) {
        // full storage path
        val fullPath = requireContext().getExternalFilesDir(null)!!.path+ "/Edugate/${fragmentName}"

        // decode base64 string to byte array
        var byteArray: ByteArray = Base64.decode(stringBase64, Base64.NO_WRAP)

        // Date formatter
        var format = SimpleDateFormat("dd_MM_yyyy_")
        Log.d(TAG,"${format.format(Date(System.currentTimeMillis()))}${pdfName}")

        try {
            var dir = File(fullPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            var file = File(fullPath, "${format.format(Date())}${pdfName}")
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            var fOut = FileOutputStream(file)
            fOut.write(byteArray)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.download -> {
                if(pdfName != null && fragmentPdfName != null && stringBase64 != null) {
                    if(Permissions().hasStoragePermissions(requireContext(),requireActivity())){
                        saveToExternalStorage(pdfName!!,stringBase64!!,fragmentName!!)
                        Toast.makeText(requireContext(),"Pdf Downloaded Successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),"Storage permission denied!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // calling own menu for this fragment // (not required any more but not deleted because testing is not done)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pdf_menu_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}