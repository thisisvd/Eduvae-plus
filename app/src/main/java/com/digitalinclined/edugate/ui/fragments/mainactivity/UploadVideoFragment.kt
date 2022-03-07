package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.databinding.FragmentUploadVideoBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class UploadVideoFragment : Fragment(R.layout.fragment_upload_video) {

    // TAG
    private val TAG = "UploadVideoFragment"

    // viewBinding
    private lateinit var binding: FragmentUploadVideoBinding

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    // arrayList
    private val uploadList = arrayListOf("Question Paper", "Notes", "Sample Paper")

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Upload A Video"

            // toggle btn toolbar setup
            toggle = (activity as MainActivity).toggle
            toggle.isDrawerIndicatorEnabled = false
            val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
            toggle.setHomeAsUpIndicator(drawable)
            IS_BACK_TOOLBAR_BTN_ACTIVE = true

            // adapter init
            adapterForSpinners()

            // upload
            upload.setOnClickListener {
                Toast.makeText(requireContext(),"Will open gallery and select pdf to upload",Toast.LENGTH_SHORT).show()
                if(upload.text == "Upload!") {

                } else {
                    selectPDFFromStorage()
                }
            }

        }
    }

    // select pdf from storage
    private fun selectPDFFromStorage() {
        val openStorageIntent = Intent(Intent.ACTION_GET_CONTENT)
        openStorageIntent.apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(
            Intent.createChooser(openStorageIntent, "Select PDF"),
            PDF_SELECTION_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfFromStorage = data.data

            // file name from uri
            val fileName = queryName(requireContext(),selectedPdfFromStorage!!)
            Toast.makeText(requireContext(),fileName.toString(),Toast.LENGTH_SHORT).show()

            // get file
            try {
                // getting input stream and convert it into bytes
                val iStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedPdfFromStorage)
                val inputData: ByteArray = getBytes(iStream!!)

                // encoding file to base64 string
                var getBase64String = Base64.encodeToString(inputData, Base64.NO_WRAP)

                Log.d(TAG,"$fileName : $getBase64String")

            } catch (e : Exception) {
                e.printStackTrace()
            }

        }
    }


    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    // uri file name
    private fun queryName(context: Context, uri: Uri): String? {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
        binding.apply {

            // adapter for course list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                uploadList
            )

            // course spinner adapter
            chooseAutoTextView.apply {
                setAdapter(
                    adapter
                )
            }

            // text view listener
            chooseAutoTextView.setOnItemClickListener { parent, view, position, id ->
                Log.d(TAG,uploadList[position])

                if(uploadList[position] == "Notes") {
                    selectPDFFromStorage()
                }

            }

        }
    }

}