package com.example.prayertimesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimesapp.databinding.DayItemBinding
import com.example.prayertimesapp.model.DateInfo

class DaysAdapter(): ListAdapter<DateInfo, DaysAdapter.DayViewHolder>(DaysDiffUtil()) {
//var context: Context

    lateinit var binding: DayItemBinding
    class DayViewHolder( var binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.textView2.text = current.gregorian.day
        holder.binding.textView3.text = current.hijri.weekday.ar


    }
}

class DaysDiffUtil : DiffUtil.ItemCallback<DateInfo>() {
    override fun areItemsTheSame(oldItem: DateInfo, newItem: DateInfo): Boolean {
        return oldItem.gregorian == newItem.gregorian
    }

    override fun areContentsTheSame(oldItem: DateInfo, newItem: DateInfo): Boolean {
        return newItem == oldItem
    }

}