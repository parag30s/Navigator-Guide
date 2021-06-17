package com.navigatorsguide.app.ui.report

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.jaredrummler.materialspinner.MaterialSpinner
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.BuildConfig
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.ReportUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class ReportBuilderFragment : BaseFragment() {
    lateinit var sectionSpinner: MaterialSpinner
    lateinit var buildReport: Button
    lateinit var viewReport: TextView
    lateinit var emptyView: TextView
    lateinit var subSectionList: List<SubSection>
    private var questionList: List<Questions> = listOf()
    private var user: User? = null
    private lateinit var mName: String
    private lateinit var mEmail: String
    private lateinit var mRank: String
    private lateinit var mShipType: String
    private lateinit var mRisk: String
    private lateinit var mDate: String
    private lateinit var sectionName: String
    private var isViewOnly = false

    private val PADDING_EDGE = 40f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_report_builder, container, false)
        sectionSpinner = root.findViewById(R.id.spinner)
        buildReport = root.findViewById(R.id.create_report_button)
        viewReport = root.findViewById(R.id.view_report_label)
        emptyView = root.findViewById(R.id.empty_report_text_view)
        buildReport.setOnClickListener {
            isViewOnly = false
            onBuildReport()
        }
        viewReport.setOnClickListener {
            isViewOnly = true
            onBuildReport()
        }
        init()
        return root
    }

    private fun init() {
        user = PreferenceManager.getRegistrationInfo(requireActivity())
        if (user != null) {
            mName = user!!.getUserName()
            mEmail = user!!.getEmail()
            mRank = user!!.getPosition()
            mShipType = user!!.getShipType()
        }
        subSectionList = getEligibleSections()
        if (!subSectionList.isNullOrEmpty()) {
            setupSectionList()
        } else {
            sectionSpinner.visibility = View.GONE
            buildReport.visibility = View.GONE
            viewReport.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun getEligibleSections(): List<SubSection> {
        var dataItem: List<SubSection>
        runBlocking {
            val mJob = async {
                return@async withContext(Dispatchers.IO) {
                    AppDatabase.invoke(requireActivity())
                        .getSubSectionDao().getCompletedSections()
                }
            }
            runBlocking {
                dataItem = mJob.await()
            }
        }
        return dataItem
    }

    private fun setupSectionList() {
        val dataSet = mutableListOf<String>()
        for (i in subSectionList) {
            dataSet.add(i.subsname.toString())
        }
        sectionSpinner.setItems(dataSet)
    }

    private fun onBuildReport() {
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                    if (report.areAllPermissionsGranted()) {
                        gatherReportData()
                    } else {
                        Toast.makeText(requireActivity(),
                            "permissions missing :(",
                            Toast.LENGTH_LONG).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken,
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun gatherReportData() {
        val subSection: SubSection? =
            subSectionList.find { it.subsname == sectionSpinner.text }
        mRisk = AppUtils.getRiskValue(subSection?.risk!!).toString()
        mDate = subSection.closureDate.toString()

        launch {
            withContext(Dispatchers.Default) {
                questionList = AppDatabase.invoke(requireActivity())
                    .getQuestionsDao().getReportedQuestions(subSection.subsid)

                val section: Section? =
                    AppDatabase.invoke(requireActivity()).getSectionDao()
                        .getSectionInfo(subSection.subsparent)
                if (section != null) {
                    sectionName = section.sectionName.toString()
                    buildReport(sectionName, sectionSpinner.text as String)
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context,
                            context?.getString(R.string.err_msg_res_failed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun buildReport(sectionName: String, subSectionName: String) {
        val document = Document(PageSize.A4, 0f, 0f, 0f, 0f)
        val outPath =
            requireActivity().getExternalFilesDir(null)
                .toString() + "/${PreferenceManager.getShipName(requireActivity())} - $subSectionName Report.pdf"
        Log.d("loc", outPath)
        val writer = PdfWriter.getInstance(document, FileOutputStream(outPath))
        document.open()

        ReportUtils.addBrandLogoHeader(requireActivity(),
            document,
            PreferenceManager.getShipName(requireActivity()))
        ReportUtils.addReportDetails(requireActivity(), document,
            mName,
            mEmail,
            mRank,
            mShipType,
            mRisk,
            mDate,
            sectionName,
            subSectionName)

        document.setMargins(0f, 0f, PADDING_EDGE, PADDING_EDGE)

        ReportUtils.addBlankCell(document)
        ReportUtils.addReportHeader(document, subSectionName)
        ReportUtils.addLineSeparators(document)
        ReportUtils.addQuestionsData(document, questionList)
        document.close()

        val file = File(outPath)
        val path: Uri = FileProvider.getUriForFile(
            requireActivity(),
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        if (isViewOnly) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(path)
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } else {
                var intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(path, "application/pdf")
                intent = Intent.createChooser(intent, "Open File")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        } else {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_STREAM, path)
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "application/pdf"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "Navigator Guide - $subSectionName Report");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                    Html.fromHtml(getString(R.string.txt_email_body)));
                startActivity(Intent.createChooser(shareIntent, "share.."))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireActivity(), "There is no sharing app.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}