package cn.pivotstudio.moduleb.libbase.util.store

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.io.OutputStream


class SaveToShareFile : IDeviceWriter {
    override fun <T> saveToDevice(data: T, context: Context, parentFileName: String, fileName: String) {
        if(data is Bitmap) {
            val photo = data as Bitmap

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + parentFileName
                contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, imageSaveFilePath)
                val file = File(imageSaveFilePath)
                if (!file.exists()) {
                    file.mkdirs()
                }
            }else {
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }

            var uri: Uri? = null
            var fos: OutputStream? = null
            val localContentResolver = context.contentResolver
            try {
                uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = uri?.let { localContentResolver.openOutputStream(it) }

                photo.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show()
            }catch (e: IOException) {
                e.printStackTrace()
                uri?.let {
                    localContentResolver.delete(it, null, null)
                }
                Toast.makeText(context, "文件保存失败！", Toast.LENGTH_SHORT).show()
            }finally {
                photo.recycle()
                try {
                    fos?.let {
                        it.flush()
                        it.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}