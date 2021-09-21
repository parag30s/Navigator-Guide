package com.navigatorsguide.app.ui.report

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.BuildConfig
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.InspectedSubSectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.Eligibility
import com.navigatorsguide.app.utils.ReportUtils
import com.navigatorsguide.app.utils.SpacesItemDecoration
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class ReportBuilderFragment : BaseFragment(), View.OnClickListener {
    private var sectionSpinner: SmartMaterialSpinner<String>? = null
    private lateinit var subSectionRecyclerView: RecyclerView
    private lateinit var showAllTextView: TextView
    private lateinit var resetTextView: TextView
    private lateinit var completeReportTextView: TextView
    private lateinit var recyclerViewAdapter: InspectedSubSectionAdapter
    lateinit var listActionLayout: RelativeLayout
    lateinit var buildReport: Button
    lateinit var viewReport: TextView
    lateinit var emptyView: TextView
    lateinit var sectionList: List<Section>
    lateinit var subSectionList: List<SubSection>
    lateinit var eligibleList: List<Int>
    var allowedSections: List<Int> = mutableListOf()
    private var questionList: List<Questions> = listOf()
    private var user: User? = null
    private var selectedSectionId: Int = -1
    private lateinit var mName: String
    private lateinit var mEmail: String
    private lateinit var mRank: String
    private lateinit var mShipType: String
    private var sectionName: String = "All Sections"
    private var rankId: Int = 0
    private var isViewOnly = false

    private val PADDING_EDGE = 40f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_report_builder, container, false)
        sectionSpinner = root.findViewById(R.id.spinner)
        subSectionRecyclerView = root.findViewById(R.id.subsection_recyclerview)
        listActionLayout = root.findViewById(R.id.list_action_layout)
        showAllTextView = root.findViewById(R.id.show_all_text_view)
        resetTextView = root.findViewById(R.id.reset_text_view)
        completeReportTextView = root.findViewById(R.id.view_complete_report)
        buildReport = root.findViewById(R.id.create_report_button)
        viewReport = root.findViewById(R.id.view_report_label)
        emptyView = root.findViewById(R.id.empty_report_text_view)
        showAllTextView.setOnClickListener(this)
        resetTextView.setOnClickListener(this)
        buildReport.setOnClickListener(this)
        viewReport.setOnClickListener(this)
        init()
        return root
    }

    private fun init() {
        user = PreferenceManager.getRegistrationInfo(requireActivity())
        if (user != null) {
            mName = user!!.getUserName()
            mEmail = user!!.getEmail()
            mRank = PreferenceManager.getPositionName(requireActivity()).toString()
            mShipType = PreferenceManager.getShipTypeName(requireActivity()).toString()
        }
        sectionList = getEligibleSections()
        if (!sectionList.isNullOrEmpty()) {

            sectionList.forEach {
                if (AppUtils.getSectionEligibleStatus(System.currentTimeMillis()
                        .toString(),
                        it.lastUnlockDate.toString())
                ) {
                    allowedSections = allowedSections + it.sectionid
                }
            }

            eligibleList = allowedSections.intersect(Eligibility.isEligibleSection(sectionList,
                user?.position!!, user?.shipType!!)).toList()

            setupSectionList()
        } else {
            sectionSpinner!!.visibility = View.GONE
            buildReport.visibility = View.GONE
            viewReport.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        setupClickableTextView()
    }

    private fun setupClickableTextView() {
        val downloadClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                launch {
                    withContext(Dispatchers.Default) {
                        subSectionList = AppDatabase.invoke(requireActivity())
                            .getSubSectionDao().getAllSubSections()
                    }
                    if (!subSectionList.isNullOrEmpty()) {
                        isViewOnly = false
                        onBuildReport()
                    }
                }
            }
        }

        val viewClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                launch {
                    withContext(Dispatchers.Default) {
                        subSectionList = AppDatabase.invoke(requireActivity())
                            .getSubSectionDao().getAllSubSections()
                    }
                    if (!subSectionList.isNullOrEmpty()) {
                        isViewOnly = true
                        onBuildReport()
                    }
                }
            }
        }

        makeLinks(completeReportTextView,
            arrayOf("Download", "View"),
            arrayOf(downloadClick, viewClick))
    }

    private fun makeLinks(
        textView: TextView,
        links: Array<String>,
        clickableSpans: Array<ClickableSpan>,
    ) {
        val spannableString = SpannableString(textView.text)

        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.indexOf(link)

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        completeReportTextView.movementMethod = LinkMovementMethod.getInstance()
        completeReportTextView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.show_all_text_view -> {
                if (showAllTextView.text == getString(R.string.show_all)) {
                    setSubSectionAdapter(false)
                    showAllTextView.text = getString(R.string.hide)
                } else {
                    setSubSectionAdapter(true)
                    showAllTextView.text = getString(R.string.show_all)
                }
            }

            R.id.reset_text_view -> {
                launch {
                    withContext(Dispatchers.Default) {
                        subSectionList = AppDatabase.invoke(requireActivity())
                            .getSubSectionDao().getSelectedSubSections(selectedSectionId)
                    }
                    if (!subSectionList.isNullOrEmpty()) {
                        setSubSectionAdapter(true)
                    }
                }
            }

            R.id.create_report_button -> {
                isViewOnly = false
                onBuildReport()
            }

            R.id.view_report_label -> {
                isViewOnly = true
                onBuildReport()
            }
        }
    }

    private fun getEligibleSections(): List<Section> {
        var dataItem: List<Section>
        runBlocking {
            val mJob = async {
                return@async withContext(Dispatchers.IO) {
                    AppDatabase.invoke(requireActivity()).getSectionDao().getAllSections()
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
        for (i in sectionList) {
            if (eligibleList.contains(i.sectionid)) {
                dataSet.add(i.sectionName.toString())
            }
        }
        sectionSpinner?.item = dataSet

        sectionSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                for (s in sectionList) {
                    if (s.sectionName.equals(
                            sectionSpinner?.selectedItem.toString(),
                            ignoreCase = true
                        )
                    ) {
                        selectedSectionId = s.sectionid
                        sectionName = s.sectionName.toString()
                        launch {
                            withContext(Dispatchers.Default) {
                                subSectionList = AppDatabase.invoke(requireActivity())
                                    .getSubSectionDao().getSelectedSubSections(s.sectionid)
                            }
                            setSubSectionAdapter(true)
                        }
                    }
                }
                showAllTextView.text = getString(R.string.show_all)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setSubSectionAdapter(fixedSize: Boolean) {
        val comparator = compareBy<SubSection> { it.status }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            subSectionList = subSectionList.sortedWith(comparator.reversed())
        }

        recyclerViewAdapter =
            InspectedSubSectionAdapter(requireActivity(),
                this@ReportBuilderFragment,
                subSectionList,
                fixedSize)

        subSectionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        subSectionRecyclerView.itemAnimator = DefaultItemAnimator()
        subSectionRecyclerView.addItemDecoration(SpacesItemDecoration(0))
        subSectionRecyclerView.adapter = recyclerViewAdapter

        listActionLayout.visibility = View.VISIBLE
        val spannable = SpannableString(getString(R.string.view_report_text))
        if (subSectionList.find { it.status == 1 } != null) {
            buildReport.isEnabled = true
            viewReport.isEnabled = true
            resetTextView.visibility = View.VISIBLE

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                26, 30,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            viewReport.text = spannable
        } else {
            buildReport.isEnabled = false
            viewReport.isEnabled = false
            resetTextView.visibility = View.GONE

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireActivity(),
                    R.color.colorDisabledButton)),
                26, 30,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            viewReport.text = spannable
        }
    }

    fun checkBoxOnClick(v: Boolean, position: Int, subName: String?) {
        subSectionList.find { it.subsname == subName }?.status = if (v) 1 else 0
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun onBuildReport() {
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                    if (report.areAllPermissionsGranted()) {
                        launch {
                            getReport()
                        }
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

    private fun getEligibleQuestions(subId: Int): List<Questions> {
        var item: List<Questions>
        runBlocking {
            val mJob = async {
                return@async withContext(Dispatchers.IO) {
                    AppDatabase.invoke(requireActivity())
                        .getQuestionsDao().getReportedQuestions(subId)
                }
            }
            runBlocking {
                item = mJob.await()
            }
        }
        return item
    }

    private suspend fun getReport() {
        showFullScreenProgress()
        val resultPath = buildReport()
        withContext(Dispatchers.Main) {
            hideFullScreenProgress()
            if (isViewOnly) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(resultPath)
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                } else {
                    var intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(resultPath, "application/pdf")
                    intent = Intent.createChooser(intent, "Open File")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, resultPath)
                    shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    shareIntent.type = "application/pdf"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Navigator Guide - $sectionName Report");
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

    private suspend fun buildReport(): Uri {
        return withContext(Dispatchers.Default) {
            val document = Document(PageSize.A4, 0f, 0f, 0f, 0f)

            val outPath =
                requireActivity().getExternalFilesDir(null)
                    .toString() + "/${
                    if (PreferenceManager.getShipName(requireActivity()) == null) "Ship" else PreferenceManager.getShipName(
                        requireActivity())
                } - $sectionName Report.pdf"
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
                sectionName)

            document.setMargins(0f, 0f, PADDING_EDGE, PADDING_EDGE)

            ReportUtils.addBlankCell(document)
            ReportUtils.addReportHeader(document, sectionName)
            ReportUtils.addLineSeparators(document)

            for (i in subSectionList) {
                if (i.status == 1) {
                    questionList = getEligibleQuestions(i.subsid)
                    ReportUtils.addReportSubSectionTitle(document, i.subsname.toString())
                    ReportUtils.addBlankCell(document)
                    ReportUtils.addSubsectionDetails(document,
                        if (i.observations.toString()
                                .isNotEmpty()
                        ) i.observations.toString() else "NA",
                        if (i.risk.toString() != "-1") AppUtils.getRiskValue(i.risk!!)
                            .toString() else "NA",
                        i.closureDate.toString(),
                        if (i.attachment_link.toString()
                                .isNotEmpty() && !i.attachment_link.toString()
                                .contentEquals("null")
                        ) i.attachment_link.toString() else "NA",
                        if (i.evidence.toString().isNotEmpty() && !i.evidence.toString()
                                .contentEquals("null")
                        ) i.evidence.toString() else "NA",
                        if (i.comments.toString().isNotEmpty()) i.comments.toString() else "NA")
                    ReportUtils.addBlankCell(document)
                    ReportUtils.addQuestionsData(document, questionList)
                }
            }

            document.close()

            val file = File(outPath)
            return@withContext FileProvider.getUriForFile(
                requireActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        }
    }
}