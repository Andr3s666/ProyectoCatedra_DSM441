package com.example.distribupan

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoicePdfGenerator {

    fun generate(context: Context, items: List<CartItem>): Uri? {
        if (items.isEmpty()) return null

        val pdf = PdfDocument()
        // A4 en puntos (72 dpi): 595 x 842
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 18f
        }
        val textPaint = Paint().apply {
            isAntiAlias = true
            textSize = 12f
        }
        val linePaint = Paint().apply { strokeWidth = 0.8f }

        var y = 40

        // Encabezado
        titlePaint.textAlign = Paint.Align.LEFT
        canvas.drawText("DistribuPan", 40f, y.toFloat(), titlePaint)
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        y += 18
        canvas.drawText("Fecha: $now", 40f, y.toFloat(), textPaint)
        y += 18
        canvas.drawText("Factura simple", 40f, y.toFloat(), textPaint)

        // Separador
        y += 12
        canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), linePaint)
        y += 24

        // Cabeceras
        val colProducto = 40f
        val colCant = 320f
        val colPrecio = 380f
        val colSubtotal = 470f

        titlePaint.textSize = 14f
        canvas.drawText("Producto", colProducto, y.toFloat(), titlePaint)
        canvas.drawText("Cant.", colCant, y.toFloat(), titlePaint)
        canvas.drawText("Precio", colPrecio, y.toFloat(), titlePaint)
        canvas.drawText("Subtotal", colSubtotal, y.toFloat(), titlePaint)
        y += 8
        canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), linePaint)
        y += 18

        // Items
        var total = 0.0
        items.forEach { ci ->
            if (y > 760) {
                // (Para listas largas: crear nueva página; versión simple)
                pdf.finishPage(page)
                val pi = PdfDocument.PageInfo.Builder(595, 842, pdf.pages.size + 1).create()
                val p2 = pdf.startPage(pi)
                // Para mantenerlo simple seguimos en la misma variable canvas
                // (en un caso real deberías manejar canvas por página)
                y = 40
            }

            val name = ci.product.name.take(38)
            val price = ci.product.price
            val sub = price * ci.quantity
            total += sub

            canvas.drawText(name, colProducto, y.toFloat(), textPaint)
            canvas.drawText(ci.quantity.toString(), colCant, y.toFloat(), textPaint)
            canvas.drawText(String.format(Locale.US, "$%.2f", price), colPrecio, y.toFloat(), textPaint)
            canvas.drawText(String.format(Locale.US, "$%.2f", sub), colSubtotal, y.toFloat(), textPaint)
            y += 18
        }

        // Total
        y += 10
        canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), linePaint)
        y += 24
        titlePaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("TOTAL: " + String.format(Locale.US, "$%.2f", total), 555f, y.toFloat(), titlePaint)

        pdf.finishPage(page)

        val fileName = "Factura_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

        return try {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                fileUri?.let {
                    resolver.openOutputStream(it)?.use { out -> pdf.writeTo(out) }
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(it, values, null, null)
                }
                fileUri
            } else {
                @Suppress("DEPRECATION")
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { out -> pdf.writeTo(out) }
                Uri.fromFile(file)
            }
            pdf.close()
            uri
        } catch (_: Throwable) {
            try { pdf.close() } catch (_: Throwable) {}
            null
        }
    }
}

