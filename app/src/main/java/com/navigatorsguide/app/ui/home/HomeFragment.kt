package com.navigatorsguide.app.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
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
import com.navigatorsguide.app.rx.RxBus
import com.navigatorsguide.app.rx.RxEvent
import com.navigatorsguide.app.ui.inspection.InspectionFragment
import com.navigatorsguide.app.utils.*
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : BaseFragment(), SectionAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var homeViewModel: HomeViewModel
    lateinit var sectionList: List<Section>
    lateinit var eligibleList: List<Int>
    private lateinit var selectedItem: Section
    private lateinit var shipNameDisposable: Disposable

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
                setSectionAdapter()
            }
        }

        try {
            shipNameDisposable = RxBus.listen(RxEvent.EventAddShipName::class.java).subscribe {
                setSectionAdapter()
                if (eligibleList.contains(selectedItem?.sectionid) && AppUtils.getSectionEligibleStatus(
                        System.currentTimeMillis()
                            .toString(),
                        selectedItem?.lastUnlockDate.toString())
                ) {
                    inspectionAllowed(selectedItem)
                } else {
                    Toast.makeText(requireActivity(),
                        getString(R.string.txt_access_message),
                        Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
        }
        return root
    }

    override fun onDetach() {
        super.onDetach()
        if (!shipNameDisposable.isDisposed) shipNameDisposable.dispose()
    }

    private fun setSectionAdapter() {
        eligibleList = Eligibility.isEligibleSection(sectionList,
            PreferenceManager.getRegistrationInfo(requireActivity())?.position!!,
            PreferenceManager.getRegistrationInfo(requireActivity())?.shipType!!)
        sectionAdapter =
            SectionAdapter(activity, sectionList, eligibleList, AppUtils.getScreenWidth(), this)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(SpacesItemDecoration(0));
        recyclerView.adapter = sectionAdapter
    }

    override fun onItemClick(item: Section?) {
        if (item != null) {
            selectedItem = item
        }
        if (eligibleList.contains(item?.sectionid) && AppUtils.getSectionEligibleStatus(System.currentTimeMillis()
                .toString(),
                item?.lastUnlockDate.toString())
        ) {
            val dialog: DialogUtil =
                if (PreferenceManager.getShipName(requireActivity()).isNullOrEmpty()) {
                    DialogUtil(
                        getString(R.string.txt_inspection_title),
                        getString(R.string.txt_inspection_message),
                        getString(R.string.txt_inspection_yes),
                        getString(R.string.txt_inspection_no))
                } else {
                    DialogUtil(
                        getString(R.string.txt_inspection_title),
                        String.format(getString(R.string.txt_inspecting_message),
                            PreferenceManager.getShipName(requireActivity())),
                        getString(R.string.txt_inspecting_yes),
                        getString(R.string.txt_inspecting_no))
                }
            dialog.setOnDialogClickListener(object : DialogUtil.OnDialogButtonClickListener {
                override fun onPositiveButtonClicked() {
                    if (PreferenceManager.getShipName(requireActivity()).isNullOrEmpty()) {
                        showInspectionDialog()
                    } else {
                        inspectionAllowed(item)
                    }
                }

                override fun onNegativeButtonClicked() {
                    if (!PreferenceManager.getShipName(requireActivity()).isNullOrEmpty()) {
                        showInspectionDialog()
                    }
                }
            })
            fragmentManager?.let { dialog.show(it, "dialog") }
        } else {
            val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
            val dialog = BottomSheetDialog(requireActivity())
            dialog.setContentView(bottomSheet)
            val mTitle: TextView? = dialog.findViewById(R.id.bottom_title_text)
            val mMessage: TextView? = dialog.findViewById(R.id.bottom_message_text)
            val mButtonYes: Button? = dialog.findViewById(R.id.bottom_yes_button)
            val mButtonNo: Button? = dialog.findViewById(R.id.bottom_no_button)
            val mDatePicker: DatePicker? = dialog.findViewById(R.id.date_picker_view)
            mDatePicker?.visibility = View.VISIBLE

            mTitle?.text = getString(R.string.txt_access_title)
            mMessage?.text = getString(R.string.txt_access_message)
            mButtonYes?.text = getString(R.string.txt_access_yes)
            mButtonNo?.text = getString(R.string.txt_access_no)

            val today = Calendar.getInstance()
            var date: String? =
                "${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH) + 1}/${
                    today.get(Calendar.YEAR)
                }"

            mDatePicker?.minDate = today.timeInMillis
            mDatePicker?.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)

            ) { view, year, month, day ->
                val month = month + 1
                date = "$day/$month/$year"
            }

            mButtonYes?.setOnClickListener {
                dialog.dismiss()
                sendEmail(date, item)
            }

            mButtonNo?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun sendEmail(date: String?, item: Section?) {
        val toEmail = "captain@thenavigatorsguide.com"
        val ccEmail = "support@thenavigatorsguide.com"
        val subject = "Regarding access of ${item?.sectionName} section"
        val body = Html.fromHtml(String.format(getString(R.string.txt_email_body_for_access),
            item?.sectionName,
            PreferenceManager.getPositionName(requireActivity()),
            PreferenceManager.getShipTypeName(requireActivity()),
            item?.sectionName, date,
            PreferenceManager.getRegistrationInfo(requireActivity())!!.userName))

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        val urlString =
            "mailto:" + Uri.encode(toEmail) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
                body.toString())
        selectorIntent.data = Uri.parse(urlString)

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
        emailIntent.putExtra(Intent.EXTRA_CC, arrayOf(ccEmail))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        emailIntent.selector = selectorIntent

        startActivity(Intent.createChooser(emailIntent, "Send email"))
    }

    private fun showInspectionDialog() {
        val dialogFragment = InspectionFragment()
        val bundle = Bundle()
        bundle.putBoolean("fragmentDialog", true)
        dialogFragment.arguments = bundle
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        dialogFragment.show(fragmentTransaction, "dialog")
    }

    private fun inspectionAllowed(item: Section?) {
        activity?.let {
            val intent = Intent(it, SubSectionActivity::class.java)
            intent.putExtra(AppConstants.SECTION_ID, item?.sectionid)
            intent.putExtra(AppConstants.SECTION_NAME, item?.sectionName)
            it.startActivity(intent)
        }
    }
}