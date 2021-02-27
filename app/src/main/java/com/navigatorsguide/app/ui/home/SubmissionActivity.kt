package com.navigatorsguide.app.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class SubmissionActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var checkBoxHigh: CheckBox
    private lateinit var checkBoxMedium: CheckBox
    private lateinit var checkBoxLow: CheckBox
    lateinit var observationGroup: RadioGroup
    lateinit var observationYes: RadioButton
    lateinit var observationNo: RadioButton
    lateinit var riskGroup: RadioGroup
    lateinit var additionalLayout: LinearLayoutCompat
    lateinit var observationEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var commentEditText: EditText
    lateinit var submitButton: Button
    var subsId: Int? = null
    var sectionName: String? = null
    var sitedSelection: Int = 0
    var riskSelection: Int = -1
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var URI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)
        subsId = intent.getIntExtra(AppConstants.SUBS_ID, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setTitle(sectionName)
        checkBoxHigh = findViewById<CheckBox>(R.id.checkbox_high)
        checkBoxMedium = findViewById<CheckBox>(R.id.checkbox_medium)
        checkBoxLow = findViewById<CheckBox>(R.id.checkbox_low)
        riskGroup = findViewById<RadioGroup>(R.id.risk_group)
        observationGroup = findViewById<RadioGroup>(R.id.observation_group)
        observationYes = findViewById<RadioButton>(R.id.radio_observation_yes)
        observationNo = findViewById<RadioButton>(R.id.radio_observation_no)
        additionalLayout = findViewById<LinearLayoutCompat>(R.id.additional_layout)
        observationEditText = findViewById<EditText>(R.id.observations_edittext)
        dateEditText = findViewById<EditText>(R.id.date_edittext)
        commentEditText = findViewById<EditText>(R.id.comments_edittext)
        submitButton = findViewById<Button>(R.id.submit_button)

        dateEditText.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this@SubmissionActivity, this@SubmissionActivity, year, month, day)
            datePickerDialog.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    fun onRadioButtonClicked(view: View) {
        when (view.id) {
            R.id.radio_observation_yes -> {
                additionalLayout.visibility = View.VISIBLE
                sitedSelection = 1
            }
            R.id.radio_observation_no -> {
                additionalLayout.visibility = View.GONE
                sitedSelection = 0
            }
        }
    }

    fun onCheckboxClicked(view: View) {
        when (view.id) {
            R.id.checkbox_high -> {
                checkBoxMedium.isChecked = false
                checkBoxLow.isChecked = false
                riskSelection = 0
            }
            R.id.checkbox_medium -> {
                checkBoxHigh.isChecked = false
                checkBoxLow.isChecked = false
                riskSelection = 1
            }
            R.id.checkbox_low -> {
                checkBoxHigh.isChecked = false
                checkBoxMedium.isChecked = false
                riskSelection = 2
            }
        }
    }

    fun onSubmission(view: View) {
        val observation = observationEditText.text.toString()
        var date = dateEditText.text.toString()
        val comment = commentEditText.text.toString()

        if(date.isEmpty()) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            date = simpleDateFormat.format(Date())
        }

        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@SubmissionActivity)
                    .getSubSectionDao().SubmitSectionStatus(
                        subsId,
                        sitedSelection,
                        observation,
                        date,
                        comment,
                        riskSelection,
                        AppConstants.SECTION_COMPLETE
                    )

                var parentId =
                    (AppDatabase.invoke(context = this@SubmissionActivity).getSubSectionDao()
                        .getParentIdFromSubId(subsId)).subsparent

                var section = AppDatabase.invoke(context = this@SubmissionActivity).getSectionDao()
                    .getSectionInfo(parentId)

                val intent = Intent(this@SubmissionActivity, SubSectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(AppConstants.SECTION_ID, section.sectionid)
                intent.putExtra(AppConstants.SECTION_NAME, section.sectionName)
                startActivity(intent)
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateEditText.setText("$dayOfMonth/${month + 1}/$year")
    }
}