package com.example.traveltogether

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [post_edit.newInstance] factory method to
 * create an instance of this fragment.
 */
class post_edit : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val args : post_editArgs by navArgs()
    private val myFormat = "dd/MM/yyyy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var titleEdit : EditText
    private lateinit var numberPeopleText : EditText
    private lateinit var startingDateText : EditText
    private lateinit var descriptionText : EditText
    private lateinit var destinationText : EditText
    private lateinit var endDateText : EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_post_edit, container, false)

        val myFormat = "dd/MM/yyyy"
        var calendar: Calendar = Calendar.getInstance()
        val activity = activity as Context
        var firebase = FirebaseDatabase.getInstance()
        val pid = args.PID
        var firebaseReference = firebase.reference.child("posts").child(pid)
        titleEdit = view.findViewById<View>(R.id.title_field) as EditText
        numberPeopleText = view.findViewById<View>(R.id.number_people_field) as EditText
        startingDateText = view.findViewById<View>(R.id.starting_date_field) as EditText
        descriptionText = view.findViewById<View>(R.id.description_field) as EditText
        destinationText = view.findViewById<View>(R.id.destination_field) as EditText
        endDateText = view.findViewById<View>(R.id.ending_date_field) as EditText

        val startDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(myFormat, Locale.GERMANY)
                startingDateText.setText(sdf.format(calendar.time))
            }

        startingDateText.setOnClickListener {
            DatePickerDialog(activity,
                startDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val endDateSetListener =
            DatePickerDialog.OnDateSetListener {_ , year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(myFormat, Locale.GERMANY)
                endDateText.setText(sdf.format(calendar.time))
            }

        endDateText.setOnClickListener {
            DatePickerDialog(activity,
                endDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        firebaseReference.child("destination").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val destination = dataSnapshot.value.toString()
                if (destination != "null")
                    destinationText.setText(destination)
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })
        firebaseReference.child("title").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val title = dataSnapshot.value.toString()
                if (title != "null")
                    titleEdit.setText(title)
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })
        firebaseReference.child("numOfPeople").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val people = dataSnapshot.value.toString()
                if (people != "null")
                    numberPeopleText.setText(people)
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })
        firebaseReference.child("startDate").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val startDate = dataSnapshot.value.toString()
                if (startDate != "null")
                    startingDateText.setText(getDate(startDate.toLong()))
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })
        firebaseReference.child("endDate").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val endDate = dataSnapshot.value.toString()
                if (endDate != "null")
                    endDateText.setText(getDate(endDate.toLong()))
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })
        firebaseReference.child("description").addValueEventListener (object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val description = dataSnapshot.value.toString()
                if (description != "null")
                    descriptionText.setText(description)
            }
            override fun onCancelled (databaseError: DatabaseError) { }
        })

        val buttonSave : Button = view.findViewById(R.id.save_button)
        buttonSave.setOnClickListener {
            if (checkFields()) {
                val sdf = SimpleDateFormat(myFormat, Locale.GERMANY)
                val startDate: Long = sdf.parse(startingDateText.text.toString())?.time!!
                val endDate: Long = sdf.parse(endDateText.text.toString())?.time!!
                val numberOfPerson =
                    if (numberPeopleText.text.isNotEmpty()) numberPeopleText.text.toString()
                        .toLong() else 0
                firebase = FirebaseDatabase.getInstance()
                firebaseReference = firebase.reference.child("posts").child(pid)
                firebaseReference.child("destination").setValue(destinationText.text.toString())
                firebaseReference.child("startDate").setValue(startDate)
                firebaseReference.child("numOfPeople").setValue(numberOfPerson)
                firebaseReference.child("title").setValue(titleEdit.text.toString())
                firebaseReference.child("description").setValue(descriptionText.text.toString())
                firebaseReference.child("endDate").setValue(endDate)
                findNavController().navigate(R.id.action_post_edit_to_saved_post_fragment)
            }
        }

        val buttonDelete : Button = view.findViewById(R.id.delete_button)
        buttonDelete.setOnClickListener {
            val userPost = UserPost(FirebaseAuth.getInstance().currentUser.uid, pid, 0,
                "", "", 0, 0, 0, "", null)
            userPost.delete()
            findNavController().navigate(R.id.action_post_edit_to_saved_post_fragment)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment post_edit.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                post_edit().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
    private fun getDate(l: Long): String? {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(l)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun checkFields(): Boolean {
        if(titleEdit.text.isEmpty() || destinationText.text.isEmpty() || startingDateText.text.isEmpty()
            || endDateText.text.isEmpty() || descriptionText.text.isEmpty()) {

            Toast.makeText(activity, "Please fill in all fields!", Toast.LENGTH_LONG).show()
            return false
        }
        val sdf = SimpleDateFormat(myFormat, Locale.GERMANY)
        val startDate: Long = sdf.parse(startingDateText.text.toString())?.time!!
        val endDate: Long = sdf.parse(endDateText.text.toString())?.time!!

        if(startDate > endDate) {
            Toast.makeText(activity, "You messed up the dates!", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}