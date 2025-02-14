package com.iyam.mycash.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.view.View
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun createBitmapFromView(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    return bitmap
}

fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): File? {
    val storageDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "MyCash"
    )

    if (!storageDir.exists()) {
        if (!storageDir.mkdirs()) {
            Log.e("SaveImage", "Gagal membuat folder")
            return null
        }
    }

    val imageFile = File(storageDir, "$fileName.png")

    return try {
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        MediaScannerConnection.scanFile(
            context,
            arrayOf(imageFile.absolutePath),
            arrayOf("image/png"),
            null
        )

        imageFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun createQRCode(content: String, width: Int, height: Int): Bitmap {
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}
