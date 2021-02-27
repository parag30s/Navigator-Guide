package com.navigatorsguide.app.ui.slideshow

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
/*import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator*/
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.ui.home.SubSectionActivity
import com.navigatorsguide.app.ui.report.ReportBuilderActivity
import com.navigatorsguide.app.utils.AppConstants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class SlideshowFragment : BaseFragment() {
    var btnCreate: Button? = null
    lateinit var bitmap: Bitmap
    lateinit var scaledBitmap: Bitmap
    var pageWidth: Int = 800

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        btnCreate = root.findViewById(R.id.create) as Button
        btnCreate!!.setOnClickListener {
//            createPdf()
            val intent = Intent(requireActivity(), ReportBuilderActivity::class.java)
            startActivity(intent)
        }
        return root
    }

//    @SuppressLint("NewApi")
//    private fun createPdf() {
//        //create object of Document class
//        val mDoc = Document()
//
//        //pdf file path
//        val mFilePath: String =
//            File(requireActivity().getExternalFilesDir(null), "/NG_PDF.pdf").toString()
//
//        try {
//            //create instance of PdfWriter class
//            val pdfWriter: PdfWriter = PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
//            //open the document for writing
//            mDoc.open()
//            mDoc.setPageSize(PageSize.A4);
//            mDoc.addCreationDate();
//            mDoc.addAuthor("Android School");
//            mDoc.addCreator("Parag Sharma");
//
//
//            val d = resources.getDrawable(R.drawable.ic_report_logo)
//            val bitDw = d as BitmapDrawable
//            val bmp = bitDw.bitmap
//            val stream = ByteArrayOutputStream()
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            val logo: Image = Image.getInstance(stream.toByteArray())
////            logo.scaleAbsolute(100F, 100F)
//
//            val table = PdfPTable(2)
//            table.setWidthPercentage(100F);
//            table.setWidths(intArrayOf(60, 400))
//            val iconCell = PdfPCell(logo, true)
//            iconCell.backgroundColor = BaseColor.PINK
//            iconCell.setPadding(10F)
//            table.addCell(iconCell)
//
//            val tFont = Font(Font.FontFamily.TIMES_ROMAN, 20.0F, Font.NORMAL, BaseColor.WHITE)
//            val phase = Phrase("Navigator Guide Pvt. Ltd.", tFont)
//            val titleCell = PdfPCell(phase)
//            titleCell.backgroundColor = BaseColor.PINK
//            titleCell.paddingTop = 10F
//            titleCell.paddingLeft = 50F
//            table.addCell(titleCell)
//            mDoc.add(table)
//
//            val table1 = PdfPTable(3)
//            table1.widthPercentage = 100f
//            table1.addCell(getCell("Text to the left", PdfPCell.ALIGN_LEFT))
//            table1.addCell(getCell("Text in the middle", PdfPCell.ALIGN_CENTER))
//            table1.addCell(getCell("Text to the right", PdfPCell.ALIGN_RIGHT))
//            mDoc.add(table1)
//
//
//            val urName =
//                BaseFont.createFont("assets/fonts/Helvetica.otf", "UTF-8", BaseFont.EMBEDDED)
//
//
//            val mOrderIdFont = Font(urName, 24.0F, Font.NORMAL, BaseColor.BLUE)
//            val mOrderIdChunk = Chunk("Navigator Guide : Engine Report", mOrderIdFont)
//            val mOrderIdParagraph = Paragraph(mOrderIdChunk)
//            mOrderIdParagraph.setAlignment(Element.ALIGN_CENTER);
//            mDoc.add(mOrderIdParagraph)
//
//            // LINE SEPARATOR
//
//            // LINE SEPARATOR
//            val lineSeparator = LineSeparator()
//            lineSeparator.setLineColor(BaseColor(0, 0, 0, 68))
//
//            mDoc.top(20F);
//            mDoc.add(Chunk(lineSeparator));
//            mDoc.add(Paragraph("xdfcgvhbndfghb"))
//            mDoc.top(20F);
//            mDoc.add(Chunk(lineSeparator));
//            mDoc.add(Paragraph("asdfghjkl"))
//            mDoc.top(20F);
//
//            //get text from EditText i.e. mTextEt
//            val mText: String =
//                "Generate a PDF document is a well mastered task by Java developers on desktop application or on web applications. On Android applications, it’s a little bit different and only a few developers add that kind of feature in their app. However, users really enjoy having some reports for monitoring applications for example. A solution can be to use iText library in your application. iText is a library created for Java applications and can be added as a Jar dependency to your Android project. Main issue is the size of the library. On Android applications, each byte is counted and so, a lightweight solution must be preferred. A lightweight and still powerful solution can be to use the Android PDF Writer (APW) implementation that is offered under BSD license and is available here : http://coderesearchlabs.com/androidpdfwriter .\n" +
//                        "Easiest solution to use APW Library in your Android project is to download the library and add the project in your build.gradle file as a dependency like that :\n" +
//                        "compile project(':apwlibrary')\n" +
//                        "Then, you have to create an instance of PDFWriter object with a given size defined thanks to constants in APW Library :\n" +
//                        "PDFWriter writer = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);\n" +
//                        "You can now define a specific Font for your PDF :\n" +
//                        "writer.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_BOLD, StandardFonts.WIN_ANSI_ENCODING);\n" +
//                        "And add some text :\n" +
//                        "writer.addText(x, y, size, \"I like SSaurel’s Tutorials !\");\n" +
//                        "Obviously, APW Library lets you to add images on your PDF :\n" +
//                        "Bitmap logo = BitmapFactory.decodeStream(assetManager.open(\"cpuinfo_logo.png\"));\n" +
//                        "Bitmap qrcode = BitmapFactory.decodeStream(assetManager.open(\"qrcode.png\"));\n" +
//                        "// center images ...\n" +
//                        "int left = (PaperSize.FOLIO_WIDTH - logo.getWidth()) / 2;\n" +
//                        "writer.addImage(left, PaperSize.FOLIO_HEIGHT - 490, logo);\n" +
//                        "left = (PaperSize.FOLIO_WIDTH - qrcode.getWidth()) / 2;\n" +
//                        "writer.addImage(left, PaperSize.FOLIO_HEIGHT - 720, qrcode);\n" +
//                        "May be now, you wanna star a new page for your PDF report ? Simply use the newPage() dedicated method :\n" +
//                        "writer.newPage();\n" +
//                        "Note that you can change font when you want in your PDF. APW Library has a lot more options. For example, you can rotate your texts or add some shapes like a rectangle.\n" +
//                        "Last step is to generate your PDF report. For that, you must get your PDF content as a string thanks to method asString() from PDFWriter instance object. Then, create a file on Android with that content as Bytes :\n" +
//                        "public void outputToFile(String fileName, String pdfContent, String encoding) {\n" +
//                        " File newFile = new File(Environment.getExternalStorageDirectory() + \"/\" + fileName);\n" +
//                        " try {\n" +
//                        "  newFile.createNewFile();\n" +
//                        "  try {\n" +
//                        "    FileOutputStream pdfFile = new FileOutputStream(newFile);\n" +
//                        "    pdfFile.write(pdfContent.getBytes(encoding));\n" +
//                        "    pdfFile.close();\n" +
//                        "  } catch(FileNotFoundException e) {\n" +
//                        "   // ...\n" +
//                        "  }\n" +
//                        " } catch(IOException e) {\n" +
//                        "  // ...\n" +
//                        " }\n" +
//                        "}\n" +
//                        "Then, call the method :\n" +
//                        "outputToFile(\"MyFirstReport.pdf\", writer.asString(), \"ISO-8859-1\");"
//
//            //add paragraph to the document
//            mDoc.add(Paragraph(mText))
//
//
//
//
//            try {
//                val table: PdfPTable = PdfPTable(3); // 3 columns.
//                table.setWidthPercentage(100F); //Width 100%
//                table.setSpacingBefore(10f); //Space before table
//                table.setSpacingAfter(10f); //Space after table
//
//                //Set Column widths
//                val columnWidths = floatArrayOf(1f, 1f, .5f)
//                table.setWidths(columnWidths);
//
//                val cell1: PdfPCell =
//                    PdfPCell(Paragraph("I found a solution for your issue. If you want to get image from your drawable folder and put it into a PDF file using iText use this code."));
//                cell1.setBorderColor(BaseColor.BLUE);
//                cell1.setPaddingLeft(10F);
//                cell1.horizontalAlignment
//                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//
//                val cell2: PdfPCell = PdfPCell(Paragraph("Cell 2"));
//                cell2.setBorderColor(BaseColor.GREEN);
//                cell2.setPaddingLeft(10F);
//                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
//
//                val cell3: PdfPCell = PdfPCell(Paragraph("Cell 3"));
//                cell3.setBorderColor(BaseColor.RED);
//                cell3.setPaddingLeft(10F);
//                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
//
//                //To avoid having the cell border and the content overlap, if you are having thick cell borders
//                //cell1.setUserBorderPadding(true);
//                //cell2.setUserBorderPadding(true);
//                //cell3.setUserBorderPadding(true);
//
//                for (i in 1..10) {
//                    table.addCell(cell1);
//                    table.addCell(cell2);
//                    table.addCell(cell3);
//                    table.addCell(cell3);
//                    table.addCell(cell3);
//                    table.addCell(cell3);
//                    table.addCell(cell1);
//                    table.addCell(cell1);
//                    table.addCell(cell1);
//                }
//
//                mDoc.add(table);
//
//
//                try {
//                    val d = resources.getDrawable(R.drawable.ic_navigation_background)
//                    val bitDw = d as BitmapDrawable
//                    val bmp = bitDw.bitmap
//                    val stream = ByteArrayOutputStream()
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                    val image: Image = Image.getInstance(stream.toByteArray())
////                    image.scaleToFit(100F, 100F)
//                    image.scaleAbsolute(100F, 100F)
//                    mDoc.add(image)
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//
//
//                val chunk = Chunk("fjdshfksd fkjsdfh dfhsd")
//
//                val para1 = Paragraph(chunk)
//                para1.alignment = Paragraph.ALIGN_RIGHT
//
//                val para2 = Paragraph(chunk)
//                para2.alignment = Paragraph.ALIGN_LEFT
//
//                mDoc.add(para1)
//                mDoc.add(para2)
//
//
//                val table1 = PdfPTable(3)
//                table1.widthPercentage = 100f
//                table1.addCell(getCell("Text to the left", PdfPCell.ALIGN_LEFT))
//                table1.addCell(getCell("Text in the middle", PdfPCell.ALIGN_CENTER))
//                table1.addCell(getCell("Text to the right", PdfPCell.ALIGN_RIGHT))
//                mDoc.add(table1)
//
//            } catch (e: Exception) {
//                e.printStackTrace();
//            }
//
//
//            //close the document
//            mDoc.close()
//            pdfWriter.close()
//            //show message that file is saved, it will show file name and file path too
//            Toast.makeText(requireActivity(),
//                "File saved to\n$mFilePath",
//                Toast.LENGTH_SHORT)
//                .show()
//        } catch (e: Exception) {
//            //if any thing goes wrong causing exception, get and show exception message
//            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
//        }
//
//
//    }
//
//    fun getCell(text: String?, alignment: Int): PdfPCell? {
//        val cell = PdfPCell(Phrase(text))
////        cell.setPadding(0f)
//        cell.paddingTop = 10f
//        cell.backgroundColor = BaseColor.GRAY
//        cell.horizontalAlignment = alignment
//        cell.border = PdfPCell.NO_BORDER
//        return cell
//    }
}