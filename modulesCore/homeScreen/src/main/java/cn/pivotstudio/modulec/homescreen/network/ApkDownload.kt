package cn.pivotstudio.modulec.homescreen.network

import android.content.Context
import androidx.work.WorkerParameters
import cn.pivotstudio.moduleb.rebase.network.DownloadApi
import cn.pivotstudio.moduleb.rebase.lib.util.download.DownloadService
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile


class ApkDownload(context: Context, workerParams: WorkerParameters) :
    DownloadService(context, workerParams) {
    private val baseUrl: String
    private val apkName: String
    override val downloadApi: DownloadApi

    init {
        val baseUrl = inputData.getString("baseUrl")!!
        this.apkName = baseUrl.let { it.substring(it.lastIndexOf("/") + 1, it.length) }
        this.baseUrl = baseUrl.let { it.substring(0, it.lastIndexOf("/") + 1) }
        this.downloadApi = DownloadApi(this.baseUrl)
    }

    @Throws(Exception::class)
    override suspend fun download(): Result {
        // 获取已经下载的安装包的大小
        var downloadedLength = 0L
        // 判断是否已经有对应的文件
        val file = File(path, apkName).also {
            // 存在就更新大小
            if (it.exists()) downloadedLength = it.length()
        }
        // 断点下载定位文件尾

        val fos = RandomAccessFile(file, "rwd")
        fos.seek(downloadedLength)

        // 初始化下载服务
        val service = downloadApi.init(downloadedLength)
        val response: ResponseBody
        try {
            response = service.download(apkName)
        } catch (e: HttpException) {
            e.printStackTrace()
            // 返回此码说明已经下载完了安装包
            return if (e.code() == 416) {
                Result.success()
            } else {
                Result.failure()
            }
        }
        var mByteStream: InputStream? = null
        // 上版本这里是会根据下载在通知显示更新进度条，现改为后台静默下载
        response.let { body ->
            try {
                mByteStream = body.byteStream()
                var len: Int
                val buf = ByteArray(2048)
                // 每次往文件中写入一个缓冲区大小的数据量
                while (mByteStream!!.read(buf).also { len = it } != -1) {
                    fos.write(buf, 0, len)
                }
                return Result.success()
            } finally {
                body.close()
                mByteStream?.close()
                fos.close()
            }
        }
    }
}