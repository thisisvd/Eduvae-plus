//package com.digitalinclined.edugate.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ProgressBar
//import androidx.core.content.ContextCompat
//import com.digitalinclined.edugate.R
//import com.digitalinclined.edugate.databinding.SliderImageViewBinding
//import com.smarteist.autoimageslider.SliderViewAdapter
//
//class SliderImageAdapter(var context: Context, var webViewProgressBar: ProgressBar) :
//    SliderViewAdapter<SliderImageAdapter.SliderAdapterViewHolder>() {
////
//    private var mBanner = arrayListOf(
//        ContextCompat.getDrawable(context, R.drawable.banner_1),
//        ContextCompat.getDrawable(context, R.drawable.banner_2),
//        ContextCompat.getDrawable(context, R.drawable.banner_3)
//    )
//
//    override fun getCount() = mBanner.size
//
//    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
//        val binding =
//            SliderImageViewBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
//        return SliderAdapterViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SliderAdapterViewHolder?, position: Int) {
//
//        // data from dataclasses
//        val data = mBanner[position]
//
//        holder!!.binding.apply {
//            ivAutoImageSlider.setImageDrawable(data)
//            webViewProgressBar.visibility = View.GONE
//        }
//    }
//
//    // inner class
//    inner class SliderAdapterViewHolder(val binding: SliderImageViewBinding) :
//        ViewHolder(binding.root)
//}