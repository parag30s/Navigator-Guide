package com.navigatorsguide.app.ui.report
//
//import android.content.ActivityNotFoundException
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.Menu
//import android.view.View
//import android.widget.Button
//import android.widget.Toast
//import com.itextpdf.io.IOException
//import com.itextpdf.kernel.colors.Color
//import com.itextpdf.kernel.colors.DeviceRgb
//import com.itextpdf.kernel.font.PdfFont
//import com.itextpdf.kernel.font.PdfFontFactory
//import com.itextpdf.kernel.geom.PageSize
//import com.itextpdf.kernel.pdf.PdfDocument
//import com.itextpdf.kernel.pdf.PdfDocumentInfo
//import com.itextpdf.kernel.pdf.PdfWriter
//import com.itextpdf.kernel.pdf.canvas.draw.DottedLine
//import com.itextpdf.layout.Document
//import com.itextpdf.layout.element.LineSeparator
//import com.itextpdf.layout.element.Paragraph
//import com.itextpdf.layout.element.Text
//import com.itextpdf.layout.property.TextAlignment
import com.navigatorsguide.app.BaseActivity
//import com.navigatorsguide.app.R
//import com.navigatorsguide.app.permission.PermissionsActivity
//import com.navigatorsguide.app.permission.PermissionsChecker
//import com.navigatorsguide.app.utils.FileUtils
//import com.navigatorsguide.app.utils.LogUtils
//import java.io.File
//import java.io.FileOutputStream
//
//
class ReportBuilderActivity : BaseActivity() {
//    var mContext: Context? = null
//    var checker: PermissionsChecker? = null
//    var dest: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_slideshow)
//        mContext = applicationContext
//
//        checker = PermissionsChecker(this)
//
//        dest = FileUtils.getAppPath(mContext).toString() + "123.pdf"
//
//        val btnCreate = findViewById(R.id.create) as Button
//        btnCreate!!.setOnClickListener {
//          createPDF()
//        }
//
//        val btnView = findViewById(R.id.view) as Button
//        btnView!!.setOnClickListener {
//            openPDF()
//        }
//    }
//
//    fun createPdf(dest: String?) {
//        if (File(dest).exists()) {
//            File(dest).delete()
//        }
//        try {
//            /**
//             * Creating Document
//             */
//            val fileoutputStream = FileOutputStream(dest)
//            val pdfWriter = PdfWriter(fileoutputStream)
//            val pdfDocument = PdfDocument(pdfWriter)
//            val info: PdfDocumentInfo = pdfDocument.getDocumentInfo()
//            info.setTitle("Example of iText7 by Pratik Butani")
//            info.setAuthor("Pratik Butani")
//            info.setSubject("iText7 PDF Demo")
//            info.setKeywords("iText, PDF, Pratik Butani")
//            info.setCreator("A simple tutorial example")
//            val document = Document(pdfDocument, PageSize.A4, true)
//
//            /***
//             * Variables for further use....
//             */
//            val mColorAccent: Color = DeviceRgb(153, 204, 255)
//            val mColorBlack: Color = DeviceRgb(0, 0, 0)
//            val mHeadingFontSize = 20.0f
//            val mValueFontSize = 26.0f
//
//            /**
//             * How to USE FONT....
//             */
//            val font: PdfFont =
//                PdfFontFactory.createFont("assets/fonts/Helvetica.otf", "UTF-8", true)
//
//            // LINE SEPARATOR
//            val lineSeparator = LineSeparator(DottedLine())
//            lineSeparator.setStrokeColor(DeviceRgb(0, 0, 68))
//
//            // Title Order Details...
//            // Adding Title....
//            val mOrderDetailsTitleChunk: Text =
//                Text("Order Details").setFont(font).setFontSize(36.0f).setFontColor(mColorBlack)
//            val mOrderDetailsTitleParagraph: Paragraph = Paragraph(mOrderDetailsTitleChunk)
//                .setTextAlignment(TextAlignment.CENTER)
//            document.add(mOrderDetailsTitleParagraph)
//
//            // Fields of Order Details...
//            // Adding Chunks for Title and value
//            val mOrderIdChunk: Text = Text("Order No:").setFont(font).setFontSize(mHeadingFontSize)
//                .setFontColor(mColorAccent)
//            val mOrderIdParagraph = Paragraph(mOrderIdChunk)
//            document.add(mOrderIdParagraph)
//            val mOrderIdValueChunk: Text =
//                Text("#123123").setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack)
//            val mOrderIdValueParagraph = Paragraph(mOrderIdValueChunk)
//            document.add(mOrderIdValueParagraph)
//
//            // Adding Line Breakable Space....
//            document.add(Paragraph(""))
//            // Adding Horizontal Line...
//            document.add(lineSeparator)
//            // Adding Line Breakable Space....
//            document.add(Paragraph(""))
//
//            // Fields of Order Details...
//            val mOrderDateChunk: Text =
//                Text("Order Date:").setFont(font).setFontSize(mHeadingFontSize)
//                    .setFontColor(mColorAccent)
//            val mOrderDateParagraph = Paragraph(mOrderDateChunk)
//            document.add(mOrderDateParagraph)
//            val mOrderDateValueChunk: Text =
//                Text("06/07/2017").setFont(font).setFontSize(mValueFontSize)
//                    .setFontColor(mColorBlack)
//            val mOrderDateValueParagraph = Paragraph(mOrderDateValueChunk)
//            document.add(mOrderDateValueParagraph)
//            document.add(Paragraph(""))
//            document.add(lineSeparator)
//            document.add(Paragraph(""))
//
//            // Fields of Order Details...
//            val mOrderAcNameChunk: Text =
//                Text("Account Name:").setFont(font).setFontSize(mHeadingFontSize)
//                    .setFontColor(mColorAccent)
//            val mOrderAcNameParagraph = Paragraph(mOrderAcNameChunk)
//            document.add(mOrderAcNameParagraph)
//            val mOrderAcNameValueChunk: Text =
//                Text("Pratik Butani").setFont(font).setFontSize(mValueFontSize)
//                    .setFontColor(mColorBlack)
//            val mOrderAcNameValueParagraph = Paragraph(mOrderAcNameValueChunk)
//            document.add(mOrderAcNameValueParagraph)
//            document.add(Paragraph(""))
//            document.add(lineSeparator)
//            document.add(Paragraph(""))
//            document.close()
//            Toast.makeText(mContext, "Created... :)", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            LogUtils.LOGE("createPdf: Error " + e.getLocalizedMessage())
//        } catch (ae: ActivityNotFoundException) {
//            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
//            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//
////    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        menuInflater.inflate(R.menu.menu_main, menu)
////        return true
////    }
////
////    fun onOptionsItemSelected(item: MenuItem): Boolean {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        val id: Int = item.getItemId()
////        return if (id == R.id.action_settings) {
////            true
////        } else super.onOptionsItemSelected(item)
////    }
//
//    fun createPDF() {
//        if (checker!!.lacksPermissions(*PermissionsChecker.REQUIRED_PERMISSION)) {
//            PermissionsActivity.startActivityForResult(this@ReportBuilderActivity,
//                PermissionsActivity.PERMISSION_REQUEST_CODE,
//                *PermissionsChecker.REQUIRED_PERMISSION)
//        } else {
//            createPdf(dest)
//        }
//    }
//
//    fun openPDF() {
//        Handler().postDelayed(Runnable {
//            try {
//                FileUtils.openFile(mContext, File(dest))
//            } catch (e: Exception) {
//                Log.d("TAG", "run: ERror")
//            }
//        }, 1000)
//    }
}