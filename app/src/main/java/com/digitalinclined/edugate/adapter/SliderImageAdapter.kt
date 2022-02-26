package com.digitalinclined.edugate.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import com.digitalinclined.edugate.databinding.SliderImageViewBinding
import com.digitalinclined.edugate.restapi.models.banner.Banner
import com.smarteist.autoimageslider.SliderViewAdapter
import java.io.ByteArrayInputStream
import java.io.InputStream

class SliderImageAdapter: SliderViewAdapter<SliderImageAdapter.SliderAdapterViewHolder>() {
  
    private var mBanner : MutableList<Banner> = ArrayList()

    fun addItem(Banner : Banner) {
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

            val stringBlobData = data.banner
            val decodedString: ByteArray = Base64.decode(stringBlobData, Base64.NO_WRAP)
            val inputStream: InputStream = ByteArrayInputStream(decodedString)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            ivAutoImageSlider.setImageBitmap(bitmap)

        }

    }

    // inner class
    inner class SliderAdapterViewHolder(val binding: SliderImageViewBinding): SliderViewAdapter.ViewHolder(binding.root)

}