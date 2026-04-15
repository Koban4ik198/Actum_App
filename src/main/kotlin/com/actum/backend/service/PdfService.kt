package com.actum.backend.service

import com.actum.backend.model.Report
import com.actum.backend.model.Task
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class PdfService {

    fun generateTaskReportPdf(task: Task, report: Report): ByteArray {
        val outputStream = ByteArrayOutputStream()

        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        val font = loadRussianFont()
        val boldFont = loadRussianFont()

        val mapper = jacksonObjectMapper()
        val json: Map<String, Any?> = try {
            mapper.readValue(report.data, Map::class.java) as Map<String, Any?>
        } catch (_: Exception) {
            emptyMap()
        }

        val workDone = json["workDone"]?.toString() ?: "-"
        val client = json["client"]?.toString() ?: task.clientName
        val result = json["result"]?.toString() ?: "-"
        val cancelReason = json["cancelReason"]?.toString()

        document.add(
            Paragraph("ACTUM — ОТЧЁТ ПО ЗАЯВКЕ")
                .setFont(boldFont)
                .setFontSize(16f)
        )

        document.add(
            Paragraph(" ")
        )

        document.add(
            Paragraph("Данные заявки")
                .setFont(boldFont)
                .setFontSize(13f)
        )
        document.add(Paragraph("ID заявки: ${task.id}").setFont(font))
        document.add(Paragraph("Название: ${task.title}").setFont(font))
        document.add(Paragraph("Адрес: ${task.address}").setFont(font))
        document.add(Paragraph("Клиент: ${task.clientName}").setFont(font))
        document.add(Paragraph("Статус: ${task.status}").setFont(font))

        document.add(
            Paragraph(" ")
        )

        document.add(
            Paragraph("Ответственные")
                .setFont(boldFont)
                .setFontSize(13f)
        )
        document.add(Paragraph("Менеджер ID: ${task.manager.id}").setFont(font))
        document.add(Paragraph("Специалист ID: ${task.specialist?.id ?: "-"}").setFont(font))

        document.add(
            Paragraph(" ")
        )

        document.add(
            Paragraph("Информация по отчёту")
                .setFont(boldFont)
                .setFontSize(13f)
        )
        document.add(Paragraph("Дата создания: ${report.createdAt}").setFont(font))

        document.add(
            Paragraph(" ")
        )

        if (task.status.name == "CANCELLED") {
            document.add(
                Paragraph("Причина отмены")
                    .setFont(boldFont)
                    .setFontSize(13f)
            )
            document.add(Paragraph(cancelReason ?: "-").setFont(font))
        } else {
            document.add(
                Paragraph("Результат выполнения")
                    .setFont(boldFont)
                    .setFontSize(13f)
            )
            document.add(Paragraph("Что сделано: $workDone").setFont(font))
            document.add(Paragraph("Клиент: $client").setFont(font))
            document.add(Paragraph("Итог: $result").setFont(font))
        }

        document.close()

        return outputStream.toByteArray()
    }

    private fun loadRussianFont(): PdfFont {
        val possibleFonts = listOf(
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/calibri.ttf",
            "C:/Windows/Fonts/tahoma.ttf"
        )

        for (fontPath in possibleFonts) {
            try {
                return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H)
            } catch (_: Exception) {
            }
        }

        throw RuntimeException("Не найден шрифт с поддержкой кириллицы")
    }
}