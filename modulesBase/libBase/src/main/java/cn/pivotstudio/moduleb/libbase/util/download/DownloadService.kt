package cn.pivotstudio.moduleb.libbase.util.download

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.pivotstudio.husthole.moduleb.network.DownloadApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



abstract class DownloadService(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    // 网络请求配置
    protected abstract val downloadApi: DownloadApi

    // 获取内部存储文件地址
    protected val path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath

    /**
     * 限制doWork能做的工作，当然现在写的比较水，只是尝试写一种规范哈哈哈
     * 将要执行的操作继承download方法实现
     */
    final override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            return@withContext download()
        } catch (e: Exception) {
            Log.e(TAG, "error in doWork")
            e.printStackTrace()
            return@withContext Result.failure()
        }
    }

    @Throws(Exception::class)
    abstract suspend fun download(): Result

    companion object {
        // tag
        protected val TAG: String = this::class.java.name
    }
}
