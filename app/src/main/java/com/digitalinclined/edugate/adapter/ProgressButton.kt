package com.digitalinclined.edugate.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.digitalinclined.edugate.R

class ProgressButton(
    context: Context,
    view: View
) {

    private var progressBar: ProgressBar= view.findViewById(R.id.progressBar)
    private var checkImage: ImageView = view.findViewById(R.id.checkImage)
    private var textView: TextView = view.findViewById(R.id.btnName)

    fun buttonActivated() {
        progressBar.visibility = View.VISIBLE
        textView.text = "Verifying..."
    }

    fun buttonSuccessfullyFinished() {
        progressBar.visibility = View.GONE
        checkImage.visibility = View.VISIBLE
        textView.text = "Verified!"
    }

    fun buttonFailed() {
        progressBar.visibility = View.GONE
        textView.text = "Verify"
    }

}