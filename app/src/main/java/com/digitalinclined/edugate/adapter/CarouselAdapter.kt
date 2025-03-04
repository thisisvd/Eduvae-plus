package com.digitalinclined.edugate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.CarouselContainerBinding

class CarouselAdapter(var context: Context) : RecyclerView.Adapter<CarouselAdapter.HomeModel>() {

    val data = arrayListOf(
        ContextCompat.getDrawable(context, R.drawable.banner_1),
        ContextCompat.getDrawable(context, R.drawable.banner_2),
        ContextCompat.getDrawable(context, R.drawable.banner_3)
    )

    inner class HomeModel(val binding: CarouselContainerBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeModel {
        return HomeModel(
            CarouselContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeModel, position: Int) {
        val data = data[position]
        holder.binding.apply {
            carouselItem.setImageDrawable(data)
        }
    }
}