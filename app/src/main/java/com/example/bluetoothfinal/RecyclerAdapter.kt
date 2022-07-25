package com.example.bluetoothfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val mList: List<MotorData>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.motor_positions_layout, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadPositionText.text = mList[position].loadMotorData.toString()
        holder.travelPositionText.text = mList[position].loadMotorData.toString()
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val loadPositionText: TextView = itemView.findViewById(R.id.loadPositionTextView)
        val travelPositionText: TextView = itemView.findViewById(R.id.travelPositionTetView)
    }

}
