package cn.pivotstudio.modulec.homescreen.ui.custom_view

import android.graphics.Bitmap
import android.view.View

interface PicGenerator {
    suspend fun generate(view: View): Bitmap?

    suspend fun save(photo: Bitmap, fileName: String)

    suspend fun generateQRCode(url: String): Bitmap?
}