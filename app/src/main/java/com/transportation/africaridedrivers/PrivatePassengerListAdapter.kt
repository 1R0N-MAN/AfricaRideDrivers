package com.transportation.africaridedrivers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrivatePassengerListAdapter(
    private val context: Context,
    private val privatePassengerDetailsList: List<PrivatePassengerDetails>
): RecyclerView.Adapter<PrivatePassengerListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val passengerIdTextView: TextView = itemView.findViewById(R.id.passengerIdTextView)
        val passengerCountTextView: TextView = itemView.findViewById(R.id.passengerCountTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val pickupLocationTextView: TextView = itemView.findViewById(R.id.pickUpLocationTextView)
        val destinationLocationTextView: TextView = itemView.findViewById(R.id.destinationLocationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.private_passenger_list_item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val privatePassengerDetails = privatePassengerDetailsList[position]
        holder.passengerIdTextView.text = privatePassengerDetails.passengerId

        val passengerCount = privatePassengerDetails.passengerCount
        holder.passengerCountTextView.text = context.getString(R.string.passenger_count_placeholder, passengerCount)

        val passengerPhoneNumber = privatePassengerDetails.passengerPhoneNumber
        holder.phoneNumberTextView.text = context.getString(R.string.passenger_phone_no_placeholder, passengerPhoneNumber)

        val pickupLocation = privatePassengerDetails.pickUpLocation
        holder.pickupLocationTextView.text = context.getString(R.string.pickup_location_placeholder, pickupLocation)

        val destination = privatePassengerDetails.destination
        holder.destinationLocationTextView.text = context.getString(R.string.destination_placeholder, destination)
    }

    override fun getItemCount(): Int {
        return privatePassengerDetailsList.size
    }
}