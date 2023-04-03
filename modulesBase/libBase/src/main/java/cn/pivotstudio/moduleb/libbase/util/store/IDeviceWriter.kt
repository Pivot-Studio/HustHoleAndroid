package cn.pivotstudio.moduleb.libbase.util.store

import android.content.Context

interface IDeviceWriter : java.io.Serializable {
    /**
     * save the specific file to the device, such as image file, etc.
     *
     * @param data the data to store
     * @param context current Fragment/Activity context
     * @param parentFileName the name of the parent file
     * @param fileName the name of the file needed to be stored
     */
    fun <T> saveToDevice(
        data: T,
        context: Context,
        parentFileName: String,
        fileName: String = System.currentTimeMillis().toString()
    )
}