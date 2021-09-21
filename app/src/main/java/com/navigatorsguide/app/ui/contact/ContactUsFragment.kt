package com.navigatorsguide.app.ui.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R


class ContactUsFragment : BaseFragment(), View.OnClickListener {
    private lateinit var emailLayout: LinearLayout
    private lateinit var phoneLayout: LinearLayout
    private lateinit var whatsAppLayout: LinearLayout
    private lateinit var linkedInLayout: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contactus, container, false)
        emailLayout = root.findViewById(R.id.email_layout)
        phoneLayout = root.findViewById(R.id.phone_layout)
        whatsAppLayout = root.findViewById(R.id.whats_app_layout)
        linkedInLayout = root.findViewById(R.id.linked_in_layout)

        emailLayout.setOnClickListener(this)
        phoneLayout.setOnClickListener(this)
        whatsAppLayout.setOnClickListener(this)
        linkedInLayout.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.email_layout -> {
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                val urlString =
                    "mailto:" + Uri.encode("captain@thenavigatorsguide.com") + "?subject=" + Uri.encode(
                        "") + "&body=" + Uri.encode("")
                selectorIntent.data = Uri.parse(urlString)
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("captain@thenavigatorsguide.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "")
                emailIntent.selector = selectorIntent
                startActivity(Intent.createChooser(emailIntent, "Send email"))
            }
            R.id.phone_layout -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919830284868"))
                startActivity(intent)
            }
            R.id.whats_app_layout -> {
                val uri =
                    Uri.parse("https://api.whatsapp.com/send?phone=91919830284868&text=Hi%20%0aI%20got%20your%20reference%20from%20the%20App%20called%20Guide2Inspections.%0aI%20would%20like%20to%20clarify%20certain%20questions")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            R.id.linked_in_layout -> {
                val uri = Uri.parse("https://www.linkedin.com/in/debashis-basu")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }
}