package com.transportation.africaridedrivers

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomePage : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var passengerListRecyclerView: RecyclerView
    private lateinit var navController: NavController
    private lateinit var dialog: Dialog
    private lateinit var db: FirebaseFirestore
    private lateinit var completeRideButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        passengerListRecyclerView = view.findViewById(R.id.passengerListRecyclerView)

        completeRideButton = view.findViewById(R.id.completeRideButton)
        completeRideButton.setOnClickListener { completeRide() }

        // check whether driver is public or private
        val driverKey = arguments?.getString("driverKey")
        Toast.makeText(context, "Driver Key in Home Page: $driverKey", Toast.LENGTH_LONG).show()

        checkDriverType(driverKey)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener { checkDriverType(driverKey) }

        return view
    }

    private fun completeRide() {
        //TODO: Implement Complete Ride Button
    }

    private fun checkDriverType(driverKey: String?) {
        if (driverKey == null){
            Toast.makeText(context, "Driver Details not found! Please try again later", Toast.LENGTH_LONG).show()
        } else {
            showLoadingDialog()

            val driverData = db.collection(DRIVERS_LIST_DATA_PATH).document(driverKey)
            driverData.get()
                .addOnSuccessListener { result ->
                    // inflate recyclerview depending on driver type
                    when (result["driverType"].toString()) {
                        "private" -> {
                            getPrivatePassengerList(driverKey)
                        }
                        "public" -> {
                            getPublicPassengerList(driverKey)
                        }
                        else -> {
                            Toast.makeText(context, "Error getting driver data", Toast.LENGTH_LONG).show()
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(tag, "Error getting driver data", exception)
                    Toast.makeText(context, "Error getting driver data", Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
        }
    }

    private fun showLoadingDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun getPublicPassengerList(driverKey: String) {
        val passengerDataRef = db.collection(DRIVERS_LIST_DATA_PATH)
            .document(driverKey)
            .collection(PASSENGER_DATA_PATH)
            .whereEqualTo("rideCompleted", false)

        passengerDataRef.get()
            .addOnSuccessListener { passengers ->

                val publicPassengerDetailsList = mutableListOf<PublicPassengerDetails>()

                for (passenger in passengers){
                    val passengerData = passenger.data
                    val passengerId = passengerData["passengerId"].toString()
                    val passengerCount = passengerData["passengerCount"].toString().toInt()


                    val publicPassengerDetails = PublicPassengerDetails(passengerId, passengerCount)
                    publicPassengerDetailsList.add(publicPassengerDetails)
                }

                val publicPassengerListAdapter = PublicPassengerListAdapter(requireContext(), publicPassengerDetailsList)
                passengerListRecyclerView.adapter = publicPassengerListAdapter
                dialog.dismiss()

                Toast.makeText(context, "Public Passengers Loaded", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting driver data", exception)
                Toast.makeText(context, "Error getting driver data", Toast.LENGTH_LONG).show()
                dialog.dismiss()
                swipeRefreshLayout.isRefreshing = false
            }
    }

    private fun getPrivatePassengerList(driverKey: String) {
        val passengerDataRef = db.collection(DRIVERS_LIST_DATA_PATH)
            .document(driverKey)
            .collection(PASSENGER_DATA_PATH)
            .whereEqualTo("rideCompleted", false)

        passengerDataRef.get()
            .addOnSuccessListener { passengers ->

                val privatePassengerDetailsList = mutableListOf<PrivatePassengerDetails>()
                for (passenger in passengers){
                    val passengerData = passenger.data
                    val passengerId = passengerData["passengerId"].toString()
                    val passengerCount = passengerData["passengerCount"].toString().toInt()
                    val phoneNumber = passengerData["phoneNumber"].toString()
                    val pickupLocation = passengerData["pickupLocation"].toString()
                    val destination = passengerData["destination"].toString()

                    val privatePassengerDetails = PrivatePassengerDetails(
                        passengerId, passengerCount, phoneNumber, pickupLocation, destination)

                    privatePassengerDetailsList.add(privatePassengerDetails)
                }

                val privatePassengerListAdapter = PrivatePassengerListAdapter(requireContext(), privatePassengerDetailsList)
                passengerListRecyclerView.adapter = privatePassengerListAdapter
                dialog.dismiss()

                Toast.makeText(context, "Private Passengers Loaded", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting driver data", exception)
                Toast.makeText(context, "Error getting driver data", Toast.LENGTH_LONG).show()
                dialog.dismiss()
                swipeRefreshLayout.isRefreshing = false
            }
    }
}