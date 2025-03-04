package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.BuildConfig
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.APP_SHARE_URL
import com.digitalinclined.edugate.databinding.FragmentAboutUsBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import java.util.Calendar

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    // view binding
    private lateinit var binding: FragmentAboutUsBinding

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutUsBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "About App"

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        binding.apply {

            // on click listener
            onClickListeners()

        }
    }

    // on click listener
    private fun onClickListeners() {
        binding.apply {

            // version number
            versionNumber.text = "Version (${BuildConfig.VERSION_NAME}) - ${
                Calendar.getInstance().get(
                    Calendar.YEAR
                )
            }"

            copyrightTv.text = "Copyright © ${
                Calendar.getInstance().get(Calendar.YEAR)
            } Educate. All rights reserved."

            // vd link
            vdLink.setOnClickListener {
                openProfileLinks("https://github.com/thisisvd", "Vimal's Github")
            }

            // flat-icons link
            flatIconsLink.setOnClickListener {
                openProfileLinks("https://www.flaticon.com", "Flaticon")
            }

            // icons8 link
            icons8Link.setOnClickListener {
                openProfileLinks("https://icons8.com", "icons8")
            }

            // unDraw
            undrawLink.setOnClickListener {
                openProfileLinks("https://undraw.co", "unDraw")
            }

            // share app
            aboutUsShareOurApp.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody =
                    "Share, discuss, learn, post and do many more exciting things only in one app.\n\nDownload the app now via link.\n\n${
                        getString(R.string.app_name)
                    } : $APP_SHARE_URL"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(sharingIntent)
            }

        }
    }

    // open link
    private fun openProfileLinks(link: String, name: String) {
        val bundle = bundleOf(
            "url" to link, "urlSiteName" to name
        )
        findNavController().navigate(R.id.action_aboutUsFragment_to_webViewFragment, bundle)
    }
}