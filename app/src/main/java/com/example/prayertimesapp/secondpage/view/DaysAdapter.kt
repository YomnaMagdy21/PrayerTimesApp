package com.example.prayertimesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimesapp.databinding.DayItemBinding
import com.example.prayertimesapp.model.DateInfo
import com.example.prayertimesapp.model.PrayerTimes

class DaysAdapter(): ListAdapter<PrayerTimes, DaysAdapter.DayViewHolder>(DaysDiffUtil()) {
//var context: Context

    lateinit var binding: DayItemBinding
    class DayViewHolder( var binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.textView2.text = current.data.get(0).date.gregorian.day
        holder.binding.textView3.text = current.data.get(0).date.hijri.weekday.ar


    }
}

class DaysDiffUtil : DiffUtil.ItemCallback<PrayerTimes>() {
    override fun areItemsTheSame(oldItem: PrayerTimes, newItem: PrayerTimes): Boolean {
        return oldItem.data == newItem.data
    }

    override fun areContentsTheSame(oldItem: PrayerTimes, newItem: PrayerTimes): Boolean {
        return newItem == oldItem
    }

}