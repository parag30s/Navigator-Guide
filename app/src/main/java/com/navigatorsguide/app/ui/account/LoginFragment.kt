package com.navigatorsguide.app.ui.account

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils

class LoginFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val FRAGMENT_TAG: String = "LOGIN_FRAGMENT"
    }

    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var visibilityView: ImageView
    private lateinit var loginButton: Button
    private lateinit var registerView: TextView
    private lateinit var learnMoreButton: Button

    private var mFirebaseDatabase: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)
        loginEmail = view.findViewById(R.id.email_edittext)
        loginPassword = view.findViewById(R.id.password_edittext)
        visibilityView = view.findViewById(R.id.visibility_view)
        loginButton = view.findViewById(R.id.login_button)
        registerView = view.findViewById(R.id.register_textview)
        learnMoreButton = view.findViewById(R.id.learn_more_button)
        visibilityView.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        registerView.setOnClickListener(this)
        learnMoreButton.setOnClickListener(this)

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.visibility_view ->{
                if(loginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    visibilityView.setImageResource(R.drawable.ic_visibility_off);
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    visibilityView.setImageResource(R.drawable.ic_visibility);
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
            R.id.login_button -> {
                if (AppUtils.isInternetAvailable(activity)) {
                    if (AppUtils.validateLoginCredentials(
                            requireActivity(),
                            loginEmail,
                            loginPassword
                        )
                    ) {
                        submitLoginCredentials()
                        showFullScreenProgress()
                    }
                } else {
                    OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_message_network))
                }
            }
            R.id.register_textview -> {
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.beginTransaction().replace(
                    R.id.container,
                    RegisterFragment()
                ).addToBackStack(null).commit()
            }
            R.id.learn_more_button -> {
                AppUtils.showWebViewDialog(requireActivity(), "https://guide2inspections.com/")
            }
        }
    }

    private fun submitLoginCredentials() {
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()
        val query = FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
            .orderByChild(AppConstants.EMAIL).equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hideFullScreenProgress()
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val user: User? = snapshot.getValue(User::class.java)
                        if (email.contentEquals(user?.email.toString())
                            && password.contentEquals(user?.password.toString())
                        ) {
                            user?.let { navigateToDashboard(it) }
                        } else {
                            OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_login))
                        }
                    }
                } else OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_login))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                OnBoardingActivity.showSimpleSnackBar(getString(R.string.err_msg_res_failed))
            }
        })
    }
}