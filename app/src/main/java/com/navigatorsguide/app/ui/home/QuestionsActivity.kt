package com.navigatorsguide.app.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.QuestionsListAdapter
import com.navigatorsguide.app.adapters.SubSectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.utils.*
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class QuestionsActivity : BaseActivity(), SubSectionAdapter.OnItemClickListener,
    QuestionsListAdapter.OnCommentPostClickListener, OptionsBottomSheetFragment.ItemClickListener {
    internal var expandableListView: ExpandableListView? = null
    internal var adapter: QuestionsListAdapter? = null

    lateinit var listOutPackHeader: List<Questions>
    var listOutPackChild: HashMap<Questions, List<String>> = HashMap()

    lateinit var contentList: List<Questions>
    lateinit var questionnaireLayout: RelativeLayout
    lateinit var proceedButton: Button
    lateinit var emptyTextView: TextView
    lateinit var hiddenFooterView: View

    var subsId: Int? = null
    var rankId: Int? = null
    var shipId: Int? = null
    var sectionName: String? = null
    var sectionLink: String? = null
    var sectionNote: String? = null
    var tempQid: Int = -1
    private var lastExpandedPosition = -1
    private val GALLERY_REQUEST = 0x1
    private val CAMERA_REQUEST = 0x2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        subsId = intent.getIntExtra(AppConstants.SUBS_ID, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)
        sectionLink = intent.getStringExtra(AppConstants.SECTION_LINK)
        sectionNote = intent.getStringExtra(AppConstants.SECTION_NOTE)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setTitle(sectionName);

        questionnaireLayout = findViewById(R.id.questionnaire_layout)
        expandableListView = findViewById(R.id.questions_exp_list)
        proceedButton = findViewById(R.id.proceed_button)
        emptyTextView = findViewById(R.id.empty_text_view)
        hiddenFooterView = findViewById(R.id.hidden_footer_view)

        rankId = PreferenceManager.getPositionId(this)
        shipId = PreferenceManager.getShipTypeId(this)

        launch {
            withContext(Dispatchers.Default) {
                listOutPackHeader = AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().getSelectedQuestions(subsId!!, rankId!!, shipId!!)
            }
            if (listOutPackHeader.isNullOrEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                proceedButton.visibility = View.GONE
            } else {
                questionnaireLayout.setBackgroundResource(R.mipmap.background)
                setQuestionsListAdapter()
            }
        }

        proceedButton.setOnClickListener {
            val intent = Intent(this, SubmissionActivity::class.java)
            intent.putExtra(AppConstants.SUBS_ID, subsId)
            intent.putExtra(AppConstants.SECTION_NAME, sectionName)
            startActivity(intent)
        }

        if (!sectionNote.isNullOrEmpty()) {
            val bundle = Bundle()
            bundle.putString("NOTE", sectionNote)
            val bottomSheet = NoteSheetDialogFragment()
            bottomSheet.isCancelable = false
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager,
                "ModalBottomSheet")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_question, menu)
        val videoLink = menu.findItem(R.id.action_video_link)
        videoLink.isVisible = (sectionLink != null && sectionLink!!.isNotEmpty())
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_video_link) {
            AppUtils.showWebViewDialog(this, sectionLink.toString())
        }
        if (item.itemId == R.id.action_reset) {
            val dialog: DialogUtil = DialogUtil(
                getString(R.string.txt_reset_title),
                getString(R.string.txt_reset_message),
                getString(R.string.txt_reset_yes),
                getString(R.string.txt_reset_no))
            dialog.setOnDialogClickListener(object : DialogUtil.OnDialogButtonClickListener {
                override fun onPositiveButtonClicked() {
                    resetSubSection()
                }

                override fun onNegativeButtonClicked() {

                }
            })
            dialog.show(supportFragmentManager, "dialog")
        }
        return super.onOptionsItemSelected(item)
    }

    fun setQuestionsListAdapter() {
        for (i in listOutPackHeader.indices) {
            val childData: MutableList<String> = ArrayList()
            val questionModel: Questions = listOutPackHeader[i]
            childData.add(questionModel.qtext)
            listOutPackChild[listOutPackHeader[i]] = childData
        }

        adapter = QuestionsListAdapter(this, listOutPackHeader, listOutPackChild, this)
        expandableListView!!.setAdapter(adapter)
        expandableListView!!.setOnGroupExpandListener { groupPosition ->
            if (lastExpandedPosition != -1
                && groupPosition != lastExpandedPosition
            ) {
                expandableListView!!.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition
        }
        expandableListView!!.setOnGroupCollapseListener { groupPosition -> }
        expandableListView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            false
        }

        expandableListView!!.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

            override fun onScroll(
                view: AbsListView?, firstVisibleItem: Int,
                visibleItemCount: Int, totalItemCount: Int,
            ) {
                if (expandableListView!!.lastVisiblePosition ==
                    expandableListView!!.adapter.count - 1 &&
                    expandableListView!!.getChildAt(expandableListView!!.childCount - 1)
                        .bottom <= expandableListView!!.height
                ) {
                    hiddenFooterView.visibility = View.VISIBLE
                } else {
                    hiddenFooterView.visibility = View.GONE
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onItemClick(item: SubSection?) {

    }

    fun updateQuestionResponse(qid: String, selection: String) {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().updateRadioSelection(qid.toInt(), selection)
            }
        }
        adapter?.notifyDataSetChanged()
    }

    fun getUserQuestionResponse(qid: Int): List<Questions> {
        var item: List<Questions>
        runBlocking {
            val mJob = async {
                return@async withContext(Dispatchers.IO) {
                    AppDatabase.invoke(context = this@QuestionsActivity)
                        .getQuestionsDao().getUserResponse(qid)
                }
            }
            runBlocking {
                item = mJob.await()
            }
        }
        return item
    }

    override fun onCommentPostItemClick(qid: String, comment: String) {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().updateComment(qid.toInt(), comment)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            adapter?.notifyDataSetChanged()
        }, 500)
    }

    fun addAttachments(qid: Int?) {
        tempQid = qid!!
        if (ActivityCompat.checkSelfPermission(
                this@QuestionsActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@QuestionsActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
            return
        }

        supportFragmentManager.let {
            OptionsBottomSheetFragment.newInstance(Bundle()).apply {
                show(it, tag)
            }
        }
    }

    override fun onItemClick(item: String) {
        when (item) {
            "FILE" -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_REQUEST)
            }
            "CAMERA" -> {
                val cameraIntent = Intent()
                cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri!!);
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                var imageSource: String? = ImageBitmapUtils.convertBitmapToString(decoded)
                if (imageSource != null) {
                    saveImageinDB(tempQid, imageSource)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            var bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            var imageSource: String? = ImageBitmapUtils.convertBitmapToString(bitmap)
            if (imageSource != null) {
                saveImageinDB(tempQid, imageSource)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            adapter?.notifyDataSetChanged()
        }, 500)
    }

    private fun saveImageinDB(qid: Int, image: String) {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().updateAttachment(qid, image)
            }
        }
    }

    private fun resetSubSection() {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().resetSubSection(subsId)

                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getSubSectionDao().resetSubSectionStatus(subsId)
            }
        }
        adapter?.notifyDataSetChanged()
    }

    fun deleteAttachment(qid: Int) {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().deleteAttachment(qid)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            adapter?.notifyDataSetChanged()
        }, 500)
    }
}