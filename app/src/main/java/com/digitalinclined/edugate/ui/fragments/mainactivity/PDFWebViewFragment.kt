package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.data.viewmodel.MainViewModel
import com.digitalinclined.edugate.databinding.FragmentPDFWebViewBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar

class PDFWebViewFragment : Fragment() {

    // TAG
    private val TAG = "PDFWebViewFragment"

    // view binding
    private var _binding: FragmentPDFWebViewBinding? = null
    private val binding get() = _binding!!

    // view models
    private val viewModel: MainViewModel by viewModels()

    // args
    private val args: PDFWebViewFragmentArgs by navArgs()

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPDFWebViewBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Pdf Viewer"
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

            if (args.pdfLink.isNotEmpty()) {
                // loading a pdf view
                viewModel.getSelectedPdf(args.pdfLink).observe(viewLifecycleOwner) {
                    var getBytes = Base64.decode(it.fileData, Base64.NO_WRAP)
                    Log.d(TAG,"$it : $getBytes")
                    pdfView.fromBytes(getBytes).load()
                }
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