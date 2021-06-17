package com.navigatorsguide.app.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.ui.inspection.InspectionFragment
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.OptionsBottomSheetFragment
import com.tapadoo.alerter.Alerter
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class SubmissionActivity : BaseActivity(), DatePickerDialog.OnDateSetListener,
    OptionsBottomSheetFragment.ItemClickListener {
    private val TAG: String = "SubmissionActivity"
    private lateinit var rootLayout: LinearLayoutCompat
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
    lateinit var browseEditText: EditText
    lateinit var commentEditText: EditText
    lateinit var submitButton: Button
    lateinit var deleteFileButton: ImageButton
    var subsId: Int? = null
    var sectionName: String? = null
    var sitedSelection: Int = 0
    var riskSelection: Int = -1
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var attachmentLink: String? = null
    private var FILE_REQUEST = 10001
    private var CAMERA_REQUEST = 10002
    private var fileUri: Uri? = null
    private var fileName: String? = null
    private var fileBitmap: Bitmap? = null

    private var mStorageReference: StorageReference? = null
    private var mFileReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)
        subsId = intent.getIntExtra(AppConstants.SUBS_ID, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = sectionName
        rootLayout = findViewById(R.id.root_layout)
        checkBoxHigh = findViewById(R.id.checkbox_high)
        checkBoxMedium = findViewById(R.id.checkbox_medium)
        checkBoxLow = findViewById(R.id.checkbox_low)
        riskGroup = findViewById(R.id.risk_group)
        observationGroup = findViewById(R.id.observation_group)
        observationYes = findViewById(R.id.radio_observation_yes)
        observationNo = findViewById(R.id.radio_observation_no)
        additionalLayout = findViewById(R.id.additional_layout)
        observationEditText = findViewById(R.id.observations_edittext)
        dateEditText = findViewById(R.id.date_edittext)
        browseEditText = findViewById(R.id.browse_edittext)
        commentEditText = findViewById(R.id.comments_edittext)
        submitButton = findViewById(R.id.submit_button)
        deleteFileButton = findViewById(R.id.delete_file_button)

        dateEditText.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this@SubmissionActivity, this@SubmissionActivity, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        browseEditText.setOnClickListener {
            if (fileUri != null && fileName != null) {
                AppUtils.openFilePreview(this, fileUri!!, fileName!!)
            } else if (fileBitmap != null) {
                Toast.makeText(this,
                    "Preview is not available for images taken from Camera.",
                    Toast.LENGTH_SHORT).show()
            } else {
                supportFragmentManager.let {
                    OptionsBottomSheetFragment.newInstance(Bundle()).apply {
                        show(it, tag)
                    }
                }
            }
        }

        mStorageReference = FirebaseStorage.getInstance().reference

//        isSubmissionEligible()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

//    override fun onUserInteraction() {
//        super.onUserInteraction()
//        isSubmissionEligible()
//    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (Alerter.isShowing) Alerter.hide()
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (!shipNameDisposable.isDisposed) shipNameDisposable.dispose()
//    }

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
        if (fileUri != null && fileName != null) {
            mFileReference =
                mStorageReference!!.child("/attachment_files/${
                    PreferenceManager.getRegistrationInfo(this)?.createdAt
                }/${fileName}")

            uploadFile(fileUri)
            showFullScreenProgress()
        } else if (fileBitmap != null) {
            mFileReference =
                mStorageReference!!.child("/attachment_files/${
                    PreferenceManager.getRegistrationInfo(this)?.createdAt
                }/Snapshot_${
                    AppUtils.getDate(System.currentTimeMillis().toString()).replace('/', '-')
                }.jpg")

            uploadBitmap(fileBitmap!!)
            showFullScreenProgress()
        } else {
            saveObservationFeedback()
        }
    }

    private fun saveObservationFeedback() {
        val observation = observationEditText.text.toString()
        var date = dateEditText.text.toString()
        val comment = commentEditText.text.toString()

        if (observation.isEmpty() && sitedSelection == 1) {
            observationEditText.error = "Please enter your observation."
            return
        }

        if (date.isEmpty()) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            date = simpleDateFormat.format(Date())
        }

        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@SubmissionActivity)
                    .getSubSectionDao().submitSectionStatus(
                        subsId,
                        sitedSelection,
                        observation,
                        date,
                        comment,
                        riskSelection,
                        AppConstants.SECTION_COMPLETE,
                        attachmentLink
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

    fun onFileDelete(view: View) {
        browseEditText.setText("")
        fileUri = null
        fileName = null
        fileBitmap = null
        deleteFileButton.visibility = View.GONE
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateEditText.setText("$dayOfMonth/${month + 1}/$year")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == FILE_REQUEST && resultCode == RESULT_OK) {
                fileUri = data?.data
                val cr = this.contentResolver
                val mime = cr.getType(fileUri!!)

                var type: String? = null
                if (mime != null) {
                    if (mime.contentEquals(AppConstants.FILE_PDF)) {
                        type = "pdf"
                    } else if (mime.contentEquals(AppConstants.FILE_SHEET)) {
                        type = "xlsx"
                    } else if (mime.contentEquals(AppConstants.FILE_EXCEL)) {
                        type = "xls"
                    } else if (mime.contentEquals(AppConstants.FILE_DOC)) {
                        type = "GODCS"
                    } else if (mime.contentEquals(AppConstants.FILE_SPREADSHEET)) {
                        type = "GSHEET"
                    } else if (mime.contentEquals(AppConstants.FILE_WORD)) {
                        type = "docx"
                    } else if (mime.contentEquals(AppConstants.FILE_IMAGE)) {
                        type = "jpeg"
                    } else if (mime.contentEquals(AppConstants.FILE_AUDIO)) {
                        type = "mp3"
                    } else {
                        Toast.makeText(this,
                            getString(R.string.err_msg_format_error),
                            Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    val cursor: Cursor? = contentResolver.query(fileUri!!, null, null, null, null)
                    val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor!!.moveToFirst()
                    fileName = cursor!!.getString(nameIndex)

                    browseEditText.setText(fileName)
                    deleteFileButton.visibility = View.VISIBLE
                }
            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                fileBitmap = data?.extras?.get("data") as Bitmap
                browseEditText.setText("Snapshot_${
                    AppUtils.getDate(System.currentTimeMillis().toString()).replace('/', '-')
                }.jpg")
                deleteFileButton.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            hideFullScreenProgress()
            Toast.makeText(this, getString(R.string.err_msg_format_error), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadFile(fileUri: Uri?) {
        if (fileUri != null) {
            mFileReference?.putFile(fileUri)
                ?.continueWithTask(Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> { task ->
                    if (!task.isSuccessful) {
                        hideFullScreenProgress()
                        throw task.exception!!
                    }
                    return@Continuation mFileReference!!.downloadUrl
                })?.addOnCompleteListener(OnCompleteListener<Uri?> { task ->
                    if (task.isSuccessful) {
                        val uri = task.result
                        Log.d(TAG, "File uploaded..$uri")
                        attachmentLink = uri.toString()
                        hideFullScreenProgress()
                        saveObservationFeedback()
                    } else {
                        Log.d(TAG, "File failed..")
                        hideFullScreenProgress()
                    }
                })
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        mFileReference?.putBytes(byteArray)
            ?.addOnFailureListener { Log.d(TAG, "handleUpload onFailure") }
            ?.addOnSuccessListener {
                Log.d(TAG, "handleUpload onSuccess")
                mFileReference?.downloadUrl?.addOnSuccessListener { uri ->
                    Log.d(TAG, "Download Photo successful URL: $uri")
                    attachmentLink = uri.toString()
                    hideFullScreenProgress()
                    saveObservationFeedback()
                }?.addOnFailureListener { e ->
                    Log.e(TAG, "downloadUrl: something went wrong: " + e.message)
                    hideFullScreenProgress()
                }
            }
    }

    private fun isSubmissionEligible() {
        if (PreferenceManager.getShipName(this).isNullOrEmpty()) {
            if (!Alerter.isShowing) {
                Alerter.create(this@SubmissionActivity)
                    .setTitle("Alert!")
                    .setText("You haven't updated Ship Name prior to submitting the report. Please proceed to Current Inspection section to fill.")
                    .enableInfiniteDuration(true)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setBackgroundColorRes(R.color.colorAccent)
                    .addButton("Proceed", R.style.ButtonStyle, View.OnClickListener {
                        shopInspectionDialog()
                    })
                    .show()
            }
            rootLayout.visibility = View.GONE
        } else {
            Alerter.hide()
            rootLayout.visibility = View.VISIBLE
        }
    }

    private fun shopInspectionDialog() {
        val dialogFragment = InspectionFragment()
        val bundle = Bundle()
        bundle.putBoolean("fragmentDialog", true)
        dialogFragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        dialogFragment.show(fragmentTransaction, "dialog")
    }

    override fun onItemClick(item: String) {
        when (item) {
            "FILE" -> {
                if (AppUtils.isInternetAvailable(this)) {
                    val fileIntent = Intent()
                    fileIntent.type = "*/*"
                    fileIntent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(fileIntent, "Select File"),
                        FILE_REQUEST)
                } else {
                    Toast.makeText(this,
                        getString(R.string.err_msg_message_network),
                        Toast.LENGTH_SHORT).show()
                }
            }
            "CAMERA" -> {
                val cameraIntent = Intent()
                cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            else -> {
                Toast.makeText(this, getString(R.string.err_msg_res_failed), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}