package com.transportation.africaridedrivers

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginPage : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var driverKeyEditText: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dialog: Dialog
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login_page, container, false)

        driverKeyEditText = view.findViewById(R.id.driverKeyEditText)
        rememberMeCheckBox = view.findViewById(R.id.rememberMeCheckBox)

        loginButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener { loginDriver() }

        navController = findNavController()

        sharedPreferences = requireContext().getSharedPreferences(
            AFRICA_RIDE_DRIVERS_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        auth = Firebase.auth
        db = Firebase.firestore

        getStoredDriverKey()
        return view
    }

    private fun loginDriver() {
        // check whether driverKey is located in database
        val driverKey = driverKeyEditText.text.toString().trim()
        val driverEmail = "$driverKey@driver.africaride"

        showLoadingDialog()

        auth.signInWithEmailAndPassword(driverEmail, driverKey).addOnCompleteListener { task ->
            if(task.isSuccessful){
                // check whether to save driver key depending on remember me checkbox
                saveDriverKey(driverKey)
                changeDriverIsActiveStatus(driverKey)
                // Move to Home Page
                val action = LoginPageDirections.actionLoginPageToHomePage(driverKey)
                navController.navigate(action)
                // close dialog
                dialog.dismiss()
            } else {
                Toast.makeText(context, "An error occurred while authenticating user, please try again", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }.addOnFailureListener { exception ->
            if (exception is FirebaseAuthInvalidUserException){
                // check if the error is from a wrong driver key
                Toast.makeText(context, "Driver Key is Invalid!", Toast.LENGTH_LONG).show()
            } else {
                // display error message
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
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

    private fun saveDriverKey(driverKey: String) {
        // check whether to save driver key depending on remember me checkbox
        if (rememberMeCheckBox.isChecked){
            sharedPreferences.edit().putString(STORED_DRIVER_KEY, driverKey).apply()
        }else {
            sharedPreferences.edit().remove(STORED_DRIVER_KEY).apply()
        }
    }

    private fun getStoredDriverKey() {
        val storedDriverKey = sharedPreferences.getString(STORED_DRIVER_KEY, "")
        if (storedDriverKey != ""){
            rememberMeCheckBox.isChecked = true
            driverKeyEditText.setText(storedDriverKey)
        }
    }

    private fun changeDriverIsActiveStatus(driverId: String, isActive: Boolean=true) {
        val selectedDriverRef = db.collection(DRIVERS_LIST_DATA_PATH).document(driverId)
        selectedDriverRef.update("isActive", isActive)

        Toast.makeText(context, "Driver Is Active Status Changed", Toast.LENGTH_SHORT).show()
    }
}