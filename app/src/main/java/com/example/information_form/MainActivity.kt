package com.example.information_form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private lateinit var addressHelper: AddressHelper
    private lateinit var spProvince: Spinner
    private lateinit var spDistrict: Spinner
    private lateinit var spWard: Spinner

    private lateinit var calendarContainer: LinearLayout
    private lateinit var calendarView: CalendarView
    private lateinit var btnToggleCalendar: Button
    private lateinit var tvBirthdate: TextView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AddressHelper with resources
        addressHelper = AddressHelper(resources)
        spProvince = findViewById(R.id.spProvince)
        spDistrict = findViewById(R.id.spDistrict)
        spWard = findViewById(R.id.spWard)

        // Load provinces
        loadProvinces()

        // Initialize other views
        val etMSSV = findViewById<EditText>(R.id.etMSSV)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val cbSport = findViewById<CheckBox>(R.id.cbSport)
        val cbPhotography = findViewById<CheckBox>(R.id.cbPhotography)
        val cbMusic = findViewById<CheckBox>(R.id.cbMusic)
        val cbAgree = findViewById<CheckBox>(R.id.cbAgree)
        val rgSex = findViewById<RadioGroup>(R.id.rgSex)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Set calendar listener
        calendarContainer = findViewById(R.id.calendarContainer)
        calendarView = findViewById(R.id.calendarView)
        btnToggleCalendar = findViewById(R.id.btnToggleCalendar)
        tvBirthdate = findViewById(R.id.tvBirthdate)

        btnToggleCalendar.text = "Show Calendar"

        btnToggleCalendar.setOnClickListener {
            if (calendarContainer.visibility == View.GONE) {
                calendarContainer.visibility = View.VISIBLE
                btnToggleCalendar.text = "Hide Calendar"
            } else {
                calendarContainer.visibility = View.GONE
                btnToggleCalendar.text = "Show Calendar"
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            tvBirthdate.text = date
            calendarContainer.visibility = View.GONE
            btnToggleCalendar.text = "Show Calendar"
        }

        // Set button click listener
        btnSubmit.setOnClickListener {
            val isProvinceSelected = spProvince.selectedItem.toString() != "Select Province"
            val isDistrictSelected = spDistrict.selectedItem.toString() != "Select District"
            val isWardSelected = spWard.selectedItem.toString() != "Select Ward"
            // Check if all fields are filled
            if (etMSSV.text.isEmpty() || etName.text.isEmpty() || etEmail.text.isEmpty() ||
                etPhone.text.isEmpty() || tvBirthdate.text.isEmpty() ||
                rgSex.checkedRadioButtonId == -1 || !cbAgree.isChecked ||
                !(cbSport.isChecked || cbPhotography.isChecked || cbMusic.isChecked) ||
                !isProvinceSelected || !isDistrictSelected || !isWardSelected
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Form Submitted Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProvinces() {
        val provinces = mutableListOf("Select Province") + addressHelper.getProvinces()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProvince.adapter = adapter

        spProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                if (position > 0) { // Skip placeholder
                    val selectedProvince = provinces[position]
                    loadDistricts(selectedProvince)
                } else {
                    // Reset Districts and Wards if Province placeholder is selected
                    resetSpinner(spDistrict, "Select District")
                    resetSpinner(spWard, "Select Ward")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadDistricts(province: String) {
        val districts = mutableListOf("Select District") + addressHelper.getDistricts(province)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDistrict.adapter = adapter

        spDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                if (position > 0) { // Skip placeholder
                    val selectedDistrict = districts[position]
                    loadWards(province, selectedDistrict)
                } else {
                    // Reset Wards if District placeholder is selected
                    resetSpinner(spWard, "Select Ward")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadWards(province: String, district: String) {
        val wards = mutableListOf("Select Ward") + addressHelper.getWards(province, district)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wards)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spWard.adapter = adapter
    }

    private fun resetSpinner(spinner: Spinner, placeholder: String) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(placeholder))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
