package com.digitalinclined.edugate.ui.fragments.supportactivity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentPDFBinding
import com.digitalinclined.edugate.databinding.FragmentWebViewBinding
import com.digitalinclined.edugate.ui.fragments.SupportActivity

class WebViewFragment : Fragment() {

    // viewBinding
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    // url link
    private var URL = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        // get url from intent
        URL = (activity as SupportActivity).URL

        // change the title bar
        (activity as SupportActivity).findViewById<TextView>(R.id.toolbarTitle).text = "$URL..."

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // web view init
            webView.apply {
                webViewClient = object : WebViewClient(){

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        webViewProgressBar.apply {
                            visibility = View.VISIBLE
                        }
                    }

                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        webViewProgressBar.apply {
                            visibility = View.GONE
                        }
                    }

                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(URL)
            }

        }
    }

}