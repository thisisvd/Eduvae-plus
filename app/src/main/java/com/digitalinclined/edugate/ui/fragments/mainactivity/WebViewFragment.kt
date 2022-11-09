package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentWebViewBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class WebViewFragment : Fragment() {

    // viewBinding
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    // nav-args
    private val args: WebViewFragmentArgs by navArgs()

    // url link
    private var urlMain = ""

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        // get url from intent
        urlMain = args.url

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "$urlMain..."

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
                    }

                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        webViewProgressBar.visibility = View.GONE
                    }

                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(urlMain)
            }

        }
    }

}