package com.digitalinclined.edugate.adapter

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.digitalinclined.edugate.R

class ProgressButton(view: View) {

    // vars
    private var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    private var checkImage: ImageView = view.findViewById(R.id.checkImage)
    private var textView: TextView = view.findViewById(R.id.btnName)

    // set button original  name
    fun setBtnOriginalName(btnMessage: String) {
        textView.text = btnMessage
    }

    // on button activated / start
    fun buttonActivated(btnMessage: String) {
        progressBar.visibility = View.VISIBLE
        textView.text = btnMessage
    }

    // on button finished successfully
    fun buttonSuccessfullyFinished(btnMessage: String) {
        progressBar.visibility = View.GONE
        checkImage.visibility = View.VISIBLE
        textView.text = btnMessage
    }

    // on button failed
    fun buttonFailed(btnMessage: String) {
        progressBar.visibility = View.GONE
        textView.text = btnMessage
    }
}