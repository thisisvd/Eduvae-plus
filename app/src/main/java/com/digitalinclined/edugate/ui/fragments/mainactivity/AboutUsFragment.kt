package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentAboutUsBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    // viewBinding
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

            // profile click listeners
            akashLink.setOnClickListener {
                openProfileLinks("https://github.com/Akash1308")
            }

            vdLink.setOnClickListener {
                openProfileLinks("https://github.com/thisisvd")
            }

        }
    }

    // open link
    private fun openProfileLinks(link: String) {
        val bundle = bundleOf(
            "url" to link,
        )
        findNavController().navigate(R.id.action_aboutUsFragment_to_webViewFragment,bundle)
    }

}