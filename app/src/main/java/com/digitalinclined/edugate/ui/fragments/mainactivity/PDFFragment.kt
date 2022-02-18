package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentPDFBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class PDFFragment : Fragment() {

    // viewBinding
    private var _binding: FragmentPDFBinding? = null
    private val binding get() = _binding!!

    // nav args
    private val args: PDFFragmentArgs by navArgs()

    // previous Fragment
    var previousFragment: String? = null
    var fragmentPdfName = ""
    var pdfUri = ""

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
                        pdfView.fromAsset("Eng_Core_pdf.pdf").load()
                    }
                    "NotesFragment" -> {
                        pdfView.fromAsset("machine_learning.pdf").load()
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
                            .load()                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}