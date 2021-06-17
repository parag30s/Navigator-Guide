package com.navigatorsguide.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.SubSectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.DialogUtil
import com.navigatorsguide.app.utils.SpacesItemDecoration
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubSectionActivity : BaseActivity(), SubSectionAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var subSectionAdapter: SubSectionAdapter
    lateinit var subSectionList: List<SubSection>
    var sectionId: Int? = null
    var sectionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_section)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setTitle(sectionName);

        recyclerView = findViewById(R.id.sub_section_recyclerview)

        init()
    }

    private fun init() {
        launch {
            withContext(Dispatchers.Default) {
                subSectionList = AppDatabase.invoke(context = this@SubSectionActivity)
                    .getSubSectionDao().getSelectedSubSections(sectionId!!)
            }
            setSubSectionAdapter()
        }

//        if (PreferenceManager.getShipName(this).isNullOrEmpty()) {
//            Alerter.create(this@SubSectionActivity)
//                .setTitle("Alert!")
//                .setText("Before you proceed, please confirm you have the ship's name updated under the Current Inspection Section.")
//                .setDuration(3000)
//                .setIcon(android.R.drawable.stat_sys_warning)
//                .setBackgroundColorRes(R.color.colorAccent)
//                .show()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_reset, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_reset) {
            val dialog: DialogUtil = DialogUtil(
                getString(R.string.txt_reset_title),
                getString(R.string.txt_reset_message),
                getString(R.string.txt_reset_yes),
                getString(R.string.txt_reset_no))
            dialog.setOnDialogClickListener(object : DialogUtil.OnDialogButtonClickListener {
                override fun onPositiveButtonClicked() {
                    resetSection()
                }

                override fun onNegativeButtonClicked() {

                }
            })
            dialog.show(supportFragmentManager, "dialog")
        }
        return super.onOptionsItemSelected(item)
    }

    fun setSubSectionAdapter() {
        subSectionAdapter = SubSectionAdapter(this, subSectionList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(SpacesItemDecoration(0));
        recyclerView.adapter = subSectionAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onItemClick(item: SubSection?) {
        if (Alerter.isShowing) Alerter.hide()

        val intent = Intent(this, QuestionsActivity::class.java)
        intent.putExtra(AppConstants.SUBS_ID, item?.subsid)
        intent.putExtra(AppConstants.SECTION_NAME, item?.subsname)
        intent.putExtra(AppConstants.SECTION_LINK, item?.subLink)
        intent.putExtra(AppConstants.SECTION_NOTE, item?.note)
        startActivity(intent)
    }

    private fun resetSection() {
        launch {
            withContext(Dispatchers.Default) {
                AppDatabase.invoke(context = this@SubSectionActivity)
                    .getQuestionsDao().resetSection(sectionId)

                AppDatabase.invoke(context = this@SubSectionActivity)
                    .getSubSectionDao().resetSectionStatus(sectionId)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            init()
        }, 1000)
    }
}