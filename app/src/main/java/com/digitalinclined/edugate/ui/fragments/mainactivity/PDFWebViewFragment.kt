package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentPDFWebViewBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class PDFWebViewFragment : Fragment() {

    // TAG
    private val TAG = "PDFWebViewFragment"

    // view binding
    private var _binding: FragmentPDFWebViewBinding? = null
    private val binding get() = _binding!!

    // view models
    private val viewModel: LocalViewModel by viewModels()

    // args
    private val args: PDFWebViewFragmentArgs by navArgs()

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // pdf vars
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var currentPageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPDFWebViewBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Pdf Viewer"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_baseline_arrow_back_ios_new_24
            )
        )
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (args.pdfLink.isNotEmpty()) {
                // loading a pdf view
                viewModel.getSelectedPdf(args.pdfLink).observe(viewLifecycleOwner) {
                    if (it != null) {
                        val getBytes = Base64.decode(it.fileData, Base64.NO_WRAP)
                        val tempPdf = createTempPdf(getBytes)
                        openPDF(tempPdf)
                    } else {
                        openPDF(copyAssetToCache("machine_learning.pdf"))
                    }
                }
            } else {
                Snackbar.make(binding.root, "Failed to load pdf!", Snackbar.LENGTH_SHORT).show()
                pdfProgressBar.visibility = View.GONE
            }

            nextButton.setOnClickListener { showPage(currentPageIndex + 1) }
            prevButton.setOnClickListener { showPage(currentPageIndex - 1) }
        }
    }

    private fun createTempPdf(pdfBytes: ByteArray): File {
        val tempFile = File(requireContext().cacheDir, "temp_pdf.pdf")
        FileOutputStream(tempFile).use { outputStream ->
            outputStream.write(pdfBytes)
        }
        return tempFile
    }

    private fun openPDF(file: File) {
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor!!)
        showPage(0)
    }

    private fun showPage(index: Int) {
        binding.apply {
            pdfRenderer?.let { renderer ->
                if (index < 0 || index >= renderer.pageCount) return

                if (index == 0) {
                    prevButton.visibility = View.GONE
                } else {
                    prevButton.visibility = View.VISIBLE
                }

                if (index == (renderer.pageCount - 1)) {
                    nextButton.visibility = View.GONE
                } else {
                    nextButton.visibility = View.VISIBLE
                }

                currentPage?.close()
                currentPage = renderer.openPage(index)

                val bitmap = Bitmap.createBitmap(
                    currentPage!!.width, currentPage!!.height, Bitmap.Config.ARGB_8888
                )
                currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                pdfImageView.setImageBitmap(bitmap)
                currentPageIndex = index
            }
        }
    }

    private fun copyAssetToCache(assetFileName: String): File {
        val file = File(requireContext().cacheDir, assetFileName)
        if (!file.exists()) {
            requireContext().assets.open(assetFileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
}