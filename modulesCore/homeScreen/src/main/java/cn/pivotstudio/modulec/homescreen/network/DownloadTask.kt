package cn.pivotstudio.modulec.homescreen.network

import cn.pivotstudio.husthole.moduleb.network.download.DownloadStateListener
import cn.pivotstudio.modulec.homescreen.repository.DownloadRepository
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile


class DownloadTask(
    private val listener: DownloadStateListener,
    downloadUrl: String,
) {
    private var lastProgress = 0
    private val fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")).drop(1)
    private val repository: DownloadRepository = DownloadRepository(downloadUrl.replace(fileName, ""))

    fun download(parentFilePath: String) {
        when (downloadApk(parentFilePath)) {
            TYPE_SUCCESS -> listener.onSuccess()
            TYPE_FAILED -> listener.onFailure()
            else -> {}
        }
    }


    private fun downloadApk(parentFilePath: String): Int?{
        // 记录已下载的文件长度
        var downloadedLength: Long = 0
        val file = File(parentFilePath, fileName).also {
            if (it.exists()) downloadedLength = it.length()
        }
        val fos = RandomAccessFile(file, "rwd")
        fos.seek(downloadedLength)
        var mByteStream: InputStream? = null
        val service = repository.getDownloadRetrofitService(downloadedLength)

        val contentLength = getContentLength()
        if (contentLength == 0L) {
            return TYPE_FAILED
        } else if (contentLength == downloadedLength) {
            return TYPE_SUCCESS
        }
        try {
            var response: ResponseBody? = null
            runBlocking {
                response = service.getApk(fileName)
            }
            response?.let { body ->
                mByteStream = body.byteStream()
                var len: Int
                val buf = ByteArray(2048)
                while (mByteStream!!.read(buf).also { len = it } != -1) {
                    fos.write(buf, 0, len)
                    val progress =
                        (fos.length() * 100 / body.contentLength()).toInt()
                    if (progress in (lastProgress + 1)..100) {
                        listener.onProgress(progress)
                        lastProgress = progress
                    }
                }
                fos.close()
                mByteStream!!.close()
                body.close()
                return TYPE_SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                mByteStream?.close()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun getContentLength(): Long {
        val service = repository.getDownloadRetrofitService()

        var response: ResponseBody? = null
        runBlocking {
            try {
                response = service.getApk(fileName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        response?.let {
            if(it.contentLength() > 0) {
                it.close()
                return it.contentLength()
            }
        }
        return 0L
    }

    companion object {
        // 下载成功
        const val TYPE_SUCCESS = 0
        // 下载失败
        const val TYPE_FAILED = 1
    }
}