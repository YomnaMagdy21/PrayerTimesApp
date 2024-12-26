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

    class DayViewHolder(var binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION // Tracks the selected position

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)
        holder.binding.textView2.text = current.date.gregorian.day
        holder.binding.textView3.text = current.date.hijri.weekday?.ar

        val currentDay = current.date.gregorian.day.toInt()
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)


        // Apply styles based on selection, current day, and default
        if (position == selectedPosition) {
            holder.binding.dayCardView.setBackgroundColor(Color.rgb(145, 186, 214)) // Selected color
          //  holder.binding.dayCardView.strokeColor = Color.rgb(46, 89, 132)
        } else if (currentDay == today && position != selectedPosition) {
            holder.binding.dayCardView.setBackgroundColor(Color.rgb(188, 210, 232)) // Reset today's color if deselected
            holder.binding.dayCardView.strokeColor =  Color.rgb(46, 89, 132)
        } else {
            holder.binding.dayCardView.setBackgroundColor(Color.rgb(188, 210, 232)) // Default color
            holder.binding.dayCardView.strokeColor = Color.TRANSPARENT
        }

        // Handle click event
        holder.binding.dayCardView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Notify the adapter to redraw the affected items
            notifyItemChanged(previousSelectedPosition) // Redraw the previously selected item
            notifyItemChanged(selectedPosition) // Redraw the newly selected item

            listener.changeDay(currentDay)
        }

        Log.i("TAG", "onBindViewHolder: ${current.date.hijri.weekday?.ar}")
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