package com.navigatorsguide.app.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.FragmentActivity
import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.draw.DottedLineSeparator
import com.itextpdf.text.pdf.draw.LineSeparator
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.entities.Questions
import java.io.ByteArrayOutputStream


class ReportUtils {
    companion object {

        var basfontRegular: BaseFont =
            BaseFont.createFont("assets/fonts/Helvetica.otf", "UTF-8", BaseFont.EMBEDDED)
        var appFontRegular = Font(basfontRegular, 20.0F, Font.NORMAL, BaseColor.WHITE)

        fun addBrandLogoHeader(context: Context, document: Document, shipName: String?) {
            val headerTable = PdfPTable(1)
            headerTable.setWidths(
                floatArrayOf(1f)
            )
            headerTable.isLockedWidth = true
            headerTable.totalWidth = PageSize.A4.width // set content width to fill document

            val contactTable =
                PdfPTable(1) // new vertical table for contact details
            val phoneCell =
                PdfPCell(
                    Paragraph(
                        shipName,
                        appFontRegular
                    )
                )
            phoneCell.border = Rectangle.NO_BORDER
            phoneCell.paddingTop = 30f
            phoneCell.paddingBottom = 30f
            phoneCell.horizontalAlignment = Element.ALIGN_CENTER

            contactTable.addCell(phoneCell)

            val headCell = PdfPCell(contactTable)
            headCell.border = Rectangle.NO_BORDER
            headCell.horizontalAlignment = Element.ALIGN_CENTER
            headCell.verticalAlignment = Element.ALIGN_MIDDLE
            headCell.backgroundColor = BaseColor(13, 81, 152)
            headerTable.addCell(headCell)
            document.add(headerTable)
        }

        fun addReportDetails(
            context: FragmentActivity,
            document: Document,
            name: String,
            email: String,
            rank: String,
            shipType: String,
            section: String,
        ) {
            val reportTable = PdfPTable(3)
            reportTable.widthPercentage = 100f
            reportTable.addCell(getCell("Name: $name",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))
            reportTable.addCell(getCell("Email: $email",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))
            reportTable.addCell(getCell("Rank: $rank",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))

            reportTable.addCell(getCell("Section: $section",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))
            reportTable.addCell(getCell("Ship Type: $shipType",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))
            reportTable.addCell(getCell("Created By: ${context.getString(R.string.app_name)}",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 81, 152)))

            document.add(reportTable)
        }

        fun addReportHeader(document: Document, sectionName: String) {
            val reportName =
                BaseFont.createFont("assets/fonts/Helvetica.otf", "UTF-8", BaseFont.EMBEDDED)

            val headerFont = Font(reportName, 18.0F, Font.NORMAL, BaseColor.BLACK)
            val headerChunk = Chunk("$sectionName Report", headerFont)
            val headerParagraph = Paragraph(headerChunk)
            val dottedLineSeparator = DottedLineSeparator()
            dottedLineSeparator.lineColor = BaseColor.BLACK
            dottedLineSeparator.offset = -2f;
            dottedLineSeparator.gap = 2f;
            headerParagraph.alignment = Element.ALIGN_CENTER
//            headerParagraph.add(dottedLineSeparator)
            document.add(headerParagraph)
        }

        fun addLineSeparators(document: Document) {
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.BLACK
            document.add(Chunk(lineSeparator))
        }

        fun addBlankCell(document: Document) {
            val blankTable: PdfPTable = PdfPTable(1)
            val blankCell = PdfPCell(Phrase(Chunk.NEWLINE))
            blankCell.border = PdfPCell.NO_BORDER
            blankTable.addCell(blankCell)
            document.add(blankTable)
        }

        fun addQuestionsData(document: Document, questionList: List<Questions>) {
            for ((index, question) in questionList.withIndex()) {
                val questionTable: PdfPTable = PdfPTable(2)
                questionTable.widthPercentage = 90f

                val paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
                //Set Column widths
                val columnWidths = floatArrayOf(.2f, 1f)
                questionTable.setWidths(columnWidths)

                val indexCell: PdfPCell =
                    PdfPCell(Paragraph("Question ${index + 1}", paragraphFont))
                indexCell.verticalAlignment = Element.ALIGN_TOP
                indexCell.backgroundColor = BaseColor(214, 228, 243)
                indexCell.setPadding(5f)

                val questionCell: PdfPCell =
                    PdfPCell(Paragraph(question.qtext, paragraphFont))
//                cell1.borderWidth = 0f
                questionCell.verticalAlignment = Element.ALIGN_TOP
                questionCell.backgroundColor = BaseColor(214, 228, 243)
                questionCell.setPadding(5f)
                questionTable.addCell(indexCell)
                questionTable.addCell(questionCell)

                if (!question.answer.isNullOrBlank()) {
                    val selectionCell: PdfPCell =
                        PdfPCell(Paragraph("Selected", paragraphFont))
                    selectionCell.verticalAlignment = Element.ALIGN_MIDDLE
                    selectionCell.backgroundColor = BaseColor(241, 246, 249)
                    selectionCell.setPadding(5f)

                    val answerCell: PdfPCell =
                        PdfPCell(Paragraph(question.answer, paragraphFont))
//                selectionCell.borderWidth = 0f
                    answerCell.verticalAlignment = Element.ALIGN_MIDDLE
                    answerCell.backgroundColor = BaseColor(241, 246, 249)
                    answerCell.setPadding(5f)

                    questionTable.addCell(selectionCell)
                    questionTable.addCell(answerCell)
                }

                if (!question.comment.isNullOrBlank()) {
                    val indexCell: PdfPCell =
                        PdfPCell(Paragraph("Comment", paragraphFont))
                    indexCell.verticalAlignment = Element.ALIGN_MIDDLE
                    indexCell.backgroundColor = BaseColor(241, 246, 249)
                    indexCell.setPadding(5f)

                    val commentCell: PdfPCell =
                        PdfPCell(Paragraph(question.comment, paragraphFont))
//                    commentCell.borderWidth = 0f
                    commentCell.verticalAlignment = Element.ALIGN_MIDDLE
                    commentCell.backgroundColor = BaseColor(241, 246, 249)
                    commentCell.setPadding(5f)

                    questionTable.addCell(indexCell)
                    questionTable.addCell(commentCell)
                }

                if (question.attachment != null && question.attachment!!.isNotEmpty()) {
                    val indexCell: PdfPCell =
                        PdfPCell(Paragraph("Attachment", paragraphFont))
                    indexCell.backgroundColor = BaseColor.WHITE
                    indexCell.setPadding(5F)
//                    iconCell.borderWidth = 0f
                    indexCell.fixedHeight = 300f
                    indexCell.verticalAlignment = Element.ALIGN_TOP


                    val bmp = ImageBitmapUtils.convertStringToBitmap(question.attachment!!)
                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val logo: Image = Image.getInstance(stream.toByteArray())
//            logo.scaleAbsolute(100F, 100F)

                    val iconCell = PdfPCell(logo, true)
                    iconCell.backgroundColor = BaseColor.WHITE
                    iconCell.setPadding(5F)
//                    iconCell.borderWidth = 0f
                    iconCell.fixedHeight = 300f
                    iconCell.horizontalAlignment = Element.ALIGN_CENTER

                    questionTable.addCell(indexCell)
                    questionTable.addCell(iconCell)
                }

                val blankCell = PdfPCell(Phrase(Chunk.NEWLINE))
                blankCell.border = PdfPCell.NO_BORDER
                questionTable.addCell(blankCell)
                questionTable.addCell(blankCell)

                document.add(questionTable)
            }
        }

        fun addReportSubSectionTitle(document: Document, title: String) {
            var appFontRegular = Font(basfontRegular, 16.0F, Font.NORMAL, BaseColor.BLACK)
            val footerTable = PdfPTable(1)
            footerTable.totalWidth = PageSize.A4.width
            footerTable.isLockedWidth = true
            val thankYouCell =
                PdfPCell(Phrase(title, appFontRegular))
            thankYouCell.border = Rectangle.NO_BORDER
            thankYouCell.paddingLeft = 40f
            thankYouCell.paddingTop = 40f
            thankYouCell.horizontalAlignment = Rectangle.ALIGN_CENTER
            footerTable.addCell(thankYouCell)
            document.add(footerTable)
        }

        fun addSubsectionDetails(
            document: Document,
            observation: String,
            risk: String,
            date: String,
            attachmentLink: String?,
            evidence: String?,
            comments: String?,
        ) {
            val reportTable = PdfPTable(2)
            reportTable.widthPercentage = 90f
            reportTable.addCell(getCell("Observation: $observation",
                PdfPCell.ALIGN_LEFT,
                BaseColor(20, 106, 169)))
            reportTable.addCell(getCell("Additional Comment: $comments",
                PdfPCell.ALIGN_LEFT,
                BaseColor(20, 106, 169)))
            reportTable.addCell(getCell("Risk : $risk",
                PdfPCell.ALIGN_LEFT,
                BaseColor(28, 106, 169)))
            reportTable.addCell(getCell("Closure Date: $date",
                PdfPCell.ALIGN_LEFT,
                BaseColor(13, 106, 169)))

            val linkTable = PdfPTable(2)
            linkTable.widthPercentage = 90f
            val attachmentCell =
                getCell("Attachment Link: ", PdfPCell.ALIGN_LEFT, BaseColor(13, 106, 169))
            val attachmentChunk = Chunk("$attachmentLink")
            attachmentChunk.setAnchor("$attachmentLink")
            attachmentCell?.phrase?.add(attachmentChunk)
            linkTable.addCell(attachmentCell)

            val evidenceCell =
                getCell("Evidence Link: ", PdfPCell.ALIGN_LEFT, BaseColor(13, 106, 169))
            val evidenceChunk = Chunk("$evidence")
            evidenceChunk.setAnchor("$evidence")
            evidenceCell?.phrase?.add(evidenceChunk)
            linkTable.addCell(evidenceCell)

            document.add(reportTable)
            document.add(linkTable)
        }

        private fun getCell(text: String?, alignment: Int, color: BaseColor): PdfPCell? {
            val cell = PdfPCell(Phrase(text))
            cell.setPadding(10f)
            cell.backgroundColor = color
            cell.horizontalAlignment = alignment
            cell.verticalAlignment = Element.ALIGN_CENTER
            cell.borderColor = BaseColor.WHITE

            val font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)
            font.color = BaseColor.WHITE
            cell.phrase = Phrase(text, font)
            return cell
        }
    }
}