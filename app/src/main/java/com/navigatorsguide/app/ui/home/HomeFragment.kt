package com.navigatorsguide.app.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.SectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.Eligibility
import com.navigatorsguide.app.utils.SpacesItemDecoration
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment(), SectionAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var homeViewModel: HomeViewModel

    lateinit var sectionList: List<Section>
    lateinit var eligibleList: List<Int>
    private var rankId: Int = 0
    private var shipId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = root.findViewById(R.id.section_recyclerview)
        launch {
            context?.let {
                sectionList = AppDatabase.invoke(requireActivity()).getSectionDao().getAllSections()
                rankId = AppDatabase.invoke(requireActivity()).getRankDao()
                    .getIdFromRank(PreferenceManager.getRegistrationInfo(requireActivity())!!.position)
                shipId = AppDatabase.invoke(requireActivity()).getShipTypeDao()
                    .getIdFromShip(PreferenceManager.getRegistrationInfo(requireActivity())!!.shipType)

                PreferenceManager.savePositionId(requireActivity(), rankId)
                PreferenceManager.saveShipTypeId(requireActivity(), shipId)
                setSectionAdapter()
            }
        }
        return root
    }

    private fun setSectionAdapter() {
        eligibleList = Eligibility.isEligibleSection(sectionList, rankId)
        sectionAdapter =
            SectionAdapter(activity, sectionList, eligibleList, AppUtils.getScreenWidth(), this)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(SpacesItemDecoration(0));
        recyclerView.adapter = sectionAdapter
    }

    override fun onItemClick(item: Section?) {
        if (eligibleList.contains(item?.sectionid)) {
            activity?.let {
                val intent = Intent(it, SubSectionActivity::class.java)
                intent.putExtra(AppConstants.SECTION_ID, item?.sectionid)
                intent.putExtra(AppConstants.SECTION_NAME, item?.sectionName)
                it.startActivity(intent)
            }
        } else {
            val bottomSheet = layoutInflater.inflate(R.layout.dialog_locked_section, null)
            val dialog = BottomSheetDialog(requireActivity())
            dialog.setContentView(bottomSheet)
            val mTitle: TextView? = dialog.findViewById(R.id.bottom_title_text)
            val mMessage: TextView? = dialog.findViewById(R.id.bottom_message_text)
            val mButtonYes: Button? = dialog.findViewById(R.id.bottom_yes_button)
            val mButtonNo: Button? = dialog.findViewById(R.id.bottom_no_button)

            mButtonYes?.setOnClickListener {
                dialog.dismiss()
                val email = "navguide@gmail.com"
                val subject = "Regarding access of ${item?.sectionName} section"
                val body = Html.fromHtml(String.format(getString(R.string.txt_email_body_for_access),
                    item?.sectionName,
                    PreferenceManager.getRegistrationInfo(requireActivity())!!.position,
                    PreferenceManager.getRegistrationInfo(requireActivity())!!.shipType,
                    item?.sectionName,
                    PreferenceManager.getRegistrationInfo(requireActivity())!!.userName))

                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                val urlString =
                    "mailto:" + Uri.encode(email) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
                        body.toString())
                selectorIntent.data = Uri.parse(urlString)

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(Intent.EXTRA_TEXT, body)
                emailIntent.selector = selectorIntent

                startActivity(Intent.createChooser(emailIntent, "Send email"))
            }

            mButtonNo?.setOnClickListener {
                dialog.dismiss()
            }

            mTitle?.text = "Unlock section!"
            mMessage?.text = getString(R.string.err_msg_content_locked)
            mButtonYes?.text = "Yes, please"
            mButtonNo?.text = "No"
            dialog.show()
        }
    }
}