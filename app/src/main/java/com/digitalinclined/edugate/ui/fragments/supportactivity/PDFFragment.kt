package com.digitalinclined.edugate.ui.fragments.supportactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentPDFBinding
import com.digitalinclined.edugate.ui.fragments.SupportActivity

class PDFFragment : Fragment() {

    // viewBinding
    private var _binding: FragmentPDFBinding? = null
    private val binding get() = _binding!!

    // previous Fragment
    var previousFragment: String? = null
    var fragmentPdfName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPDFBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as SupportActivity).findViewById<TextView>(R.id.toolbarTitle).text = "PDF Viewer"

        // get fragment
        previousFragment = (activity as SupportActivity).previousFragment
        fragmentPdfName = (activity as SupportActivity).fragmentPdfName

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

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}