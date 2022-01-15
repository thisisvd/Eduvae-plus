package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentHomeBinding
import com.digitalinclined.edugate.ui.fragments.SetupActivity
import com.google.firebase.auth.FirebaseAuth
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class HomeFragment: Fragment(R.layout.fragment_home) {

    // viewBinding
    private lateinit var binding: FragmentHomeBinding

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // calling carousel method
        carouselImageView()

        binding.apply {



        }

    }

    // Carousel image view container
    private fun carouselImageView() {

        binding.carousel.registerLifecycle(lifecycle)

        val list = mutableListOf<CarouselItem>()

        list.add(
            CarouselItem(
//                imageUrl = "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080"
                imageDrawable = R.drawable.digens_img
            )
        )

        list.add(
            CarouselItem(
//                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
                imageDrawable = R.drawable.digens_img
                )
        )

        list.add(
            CarouselItem(
                imageDrawable = R.drawable.digens_img
            )
        )

        binding.carousel.setData(list)
    }

}