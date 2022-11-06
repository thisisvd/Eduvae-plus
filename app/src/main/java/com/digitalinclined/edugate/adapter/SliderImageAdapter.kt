package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import coil.load
import com.digitalinclined.edugate.databinding.SliderImageViewBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderImageAdapter(var webViewProgressBar: ProgressBar): SliderViewAdapter<SliderImageAdapter.SliderAdapterViewHolder>() {
  
    private var mBanner : MutableList<String> = ArrayList()

    fun addItem(Banner : String) {
        mBanner.add(Banner)
        notifyDataSetChanged()
    }

    override fun getCount() = mBanner .size

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        val binding = SliderImageViewBinding.inflate(LayoutInflater.from(parent!!.context),parent,false)
        return SliderAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderAdapterViewHolder?, position: Int) {

        // data from dataclasses
        val data = mBanner[position]

        holder!!.binding.apply {

            ivAutoImageSlider.load(data)
            webViewProgressBar.visibility = View.GONE

        }

    }

    // inner class
    inner class SliderAdapterViewHolder(val binding: SliderImageViewBinding): SliderViewAdapter.ViewHolder(binding.root)

}