package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.databinding.FragmentPDFWebViewBinding
import com.google.android.material.snackbar.Snackbar

class PDFWebViewFragment : Fragment() {

    // TAG
    private val TAG = "PDFWebViewFragment"

    // view binding
    private var _binding: FragmentPDFWebViewBinding? = null
    private val binding get() = _binding!!

    // args
    private val args: PDFWebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPDFWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (args.pdfLink.isNotEmpty()) {
                // loading a pdf view
                Snackbar.make(binding.root, "Coming Soon!", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Failed to load pdf!", Snackbar.LENGTH_SHORT).show()
                pdfProgressBar.visibility = View.GONE
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}