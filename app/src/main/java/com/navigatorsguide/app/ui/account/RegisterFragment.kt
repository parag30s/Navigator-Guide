package com.navigatorsguide.app.ui.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Rank
import com.navigatorsguide.app.database.entities.ShipType
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment(), View.OnClickListener {

    companion object {
        var FRAGMENT_TAG = "REGISTER_FRAGMENT"
    }

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var positionEditText: EditText
    private lateinit var shipTypeEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginView: TextView

    private var mFirebaseReference: DatabaseReference? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null

    private lateinit var mRankList: List<Rank>
    private lateinit var mShipTypeList: List<ShipType>
    private lateinit var mKey: String
    private lateinit var mToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_register, container, false)
        nameEditText = view.findViewById(R.id.name_edittext)
        emailEditText = view.findViewById(R.id.email_edittext)
        passwordEditText = view.findViewById(R.id.password_edittext)
        positionEditText = view.findViewById(R.id.position_edittext)
        shipTypeEditText = view.findViewById(R.id.shiptype_edittext)
        registerButton = view.findViewById(R.id.register_button)
        loginView = view.findViewById(R.id.login_view)

        positionEditText.setOnClickListener(this)
        shipTypeEditText.setOnClickListener(this)
        registerButton.setOnClickListener(this)
        loginView.setOnClickListener(this)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseReference = mFirebaseDatabase!!.getReference(AppConstants.USER_REFERENCE)
        mFirebaseDatabase!!.getReference(AppConstants.APP_TITLE)
            .setValue(getString(R.string.app_name))

        launch {
            context?.let {
                val db: AppDatabase = AppDatabase.invoke(requireActivity())
                mRankList = db.getRankDao().getAllRanks()
                mShipTypeList = db.getShipTypeDao().getAllShipType()
            }
        }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                mToken = task.result?.token.toString()
            })
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.position_edittext -> {
                val rankMenu = PopupMenu(positionEditText.context, positionEditText)
                for (i in 0 until mRankList.size) {
                    rankMenu.menu.add(i, Menu.FIRST, i, mRankList.get(i).rankName)
                }
                rankMenu.setOnMenuItemClickListener { item ->
                    positionEditText.setText(item.title)
                    false
                }
                rankMenu.show()
            }
            R.id.shiptype_edittext -> {
                val shipTypeMenu = PopupMenu(shipTypeEditText.context, shipTypeEditText)
                for (i in 0 until mShipTypeList.size) {
                    shipTypeMenu.menu.add(i, Menu.FIRST, i, mShipTypeList.get(i).typeName)
                }
                shipTypeMenu.setOnMenuItemClickListener { item ->
                    shipTypeEditText.setText(item.title)
                    false
                }
                shipTypeMenu.show()
            }
            R.id.register_button -> {
                if (AppUtils.isInternetAvailable(activity)) {
                    if (AppUtils.validateRegistrationCredentials(
                            requireActivity(),
                            nameEditText,
                            emailEditText,
                            passwordEditText,
                            positionEditText,
                            shipTypeEditText
                        )
                    ) {
                        submitRegistrationCredentials()
                        showFullScreenProgress()
                    }
                } else {
                    OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_message_network))
                }
            }
            R.id.login_view -> {
                getActivity()?.getSupportFragmentManager()?.popBackStack()
                getActivity()?.getSupportFragmentManager()?.beginTransaction()?.replace(
                    R.id.container,
                    LoginFragment()
                )?.addToBackStack(null)?.commit()
            }
        }
    }

    private fun submitRegistrationCredentials() {
        val name: String = nameEditText.getText().toString()
        val email: String = emailEditText.getText().toString()
        val password: String = passwordEditText.getText().toString()
        val position: Int? =
            mRankList.find { it.rankName == positionEditText.text.toString() }?.rankid
        val shipType: Int? =
            mShipTypeList.find { it.typeName == shipTypeEditText.text.toString() }?.typeId

        val userId: String = AppUtils.checkUserId(email.split("@").component1())

        val query = FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
            .orderByChild(AppConstants.EMAIL).equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hideFullScreenProgress()
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val user: User? = snapshot.getValue(User::class.java)
                        if (email.contentEquals(user?.email.toString())) {
                            hideFullScreenProgress()
                            OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_account_exist))
                            return
                        }
                    }
                } else {
                    createUser(userId, name, email, password, position, shipType)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                hideFullScreenProgress()
                OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_res_failed))
            }
        })
    }

    private fun createUser(
        userId: String,
        name: String,
        email: String,
        password: String,
        position: Int?,
        shipType: Int?,
    ) {
        mKey = mFirebaseReference!!.push().key.toString()
        val user = User(
            userId,
            name,
            email,
            password,
            position!!,
            shipType!!,
            mToken,
            System.currentTimeMillis().toString()
        )
        mFirebaseReference!!.child(AppUtils.checkUserId(userId)).setValue(user)

        mFirebaseReference!!.child(mKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hideFullScreenProgress()
                navigateToDashboard(user)
            }

            override fun onCancelled(error: DatabaseError) {
                hideFullScreenProgress()
                OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_res_failed))
            }
        })
    }
}