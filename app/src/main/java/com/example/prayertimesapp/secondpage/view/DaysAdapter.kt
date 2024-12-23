package com.example.prayertimesapp

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimesapp.databinding.DayItemBinding
import com.example.prayertimesapp.model.DateInfo
import com.example.prayertimesapp.model.PrayerData
import com.example.prayertimesapp.model.PrayerTimes
import com.example.prayertimesapp.secondpage.view.OnDayClickListener
import java.util.Calendar

class DaysAdapter(var listener:OnDayClickListener): ListAdapter<PrayerData, DaysAdapter.DayViewHolder>(DaysDiffUtil()) {
//var context: Context

    lateinit var binding: DayItemBinding
    class DayViewHolder( var binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.textView2.text = current.date.gregorian.day
        holder.binding.textView3.text = current.date.hijri.weekday?.ar
        val currentDay = current.date.gregorian.day.toInt()
        val calender = Calendar.getInstance()
        val day = calender.get(Calendar.DAY_OF_MONTH)
        if (currentDay == day){
            holder.binding.dayCardView.setBackgroundColor(Color.YELLOW)
        } else {
            holder.binding.dayCardView.setBackgroundColor(Color.TRANSPARENT) // Reset color for other days
        }

        Log.i("TAG", "onBindViewHolder: ${current.date.hijri.weekday?.ar}")

        holder.binding.dayCardView.setOnClickListener {
            listener.changeDay(currentDay+1)
        }


    }
}

class DaysDiffUtil : DiffUtil.ItemCallback<PrayerData>() {
    override fun areItemsTheSame(oldItem: PrayerData, newItem: PrayerData): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: PrayerData, newItem: PrayerData): Boolean {
        return newItem == oldItem
    }

}