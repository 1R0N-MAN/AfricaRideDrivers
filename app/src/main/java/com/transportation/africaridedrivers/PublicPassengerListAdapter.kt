package com.transportation.africaridedrivers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PublicPassengerListAdapter(
    private val context: Context,
    private val publicPassengerDetailsList: List<PublicPassengerDetails>
): RecyclerView.Adapter<PublicPassengerListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val passengerIdTextView: TextView = itemView.findViewById(R.id.passengerIdTextView)
        val passengerCountTextView: TextView = itemView.findViewById(R.id.passengerCountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.public_passenger_list_item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val publicPassengerDetails = publicPassengerDetailsList[position]
        holder.passengerIdTextView.text = publicPassengerDetails.passengerId

        val passengerCount = publicPassengerDetails.passengerCount
        holder.passengerCountTextView.text = context.getString(R.string.passenger_count_placeholder, passengerCount)
    }

    override fun getItemCount(): Int {
        return publicPassengerDetailsList.size
    }
}