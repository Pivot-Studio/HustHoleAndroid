package cn.pivotstudio.modulec.homescreen.network

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import cn.pivotstudio.husthole.moduleb.network.download.DownloadStateListener
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import kotlinx.coroutines.*
import okhttp3.internal.notify
import java.io.File


@SuppressLint("UnspecifiedImmutableFlag")
class DownloadService : Service() {
    private val path by lazy{
        this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    }
    private val mBinder = DownloadBinder()
    private var downloadFileTask: DownloadTask? = null
    private var downloadUrl: String = "https://static.pivotstudio.cn/husthole/download/husthole.apk"
    private val mCoroutineScope = CoroutineScope(Dispatchers.IO)

    private val notificationManager: NotificationManager by lazy {
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private var notification: NotificationCompat.Builder? = null

    private val listener: DownloadStateListener = object : DownloadStateListener {
        override fun onProgress(progress: Int) {
            Log.d(TAG, "onProgress: -------$progress")
            getNotification(getString(R.string.downloading), progress)
            notification!!.setAutoCancel(false) //设置通知被点击一次是否自动取消
            notificationManager.notify(1, notification!!.build())
        }

        override fun onSuccess() {
            Log.d(TAG, "onSuccess: -------------")
            downloadFileTask = null
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true)
            getNotification(getString(R.string.download_success), -1)
            notification!!.setAutoCancel(true)
            notificationManager.notify(1, notification!!.build())
            installApk("husthole.apk")
        }

        override fun onFailure() {
            Log.d(TAG, "onFailed: -------------")
            downloadFileTask = null
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true)
            getNotification(getString(R.string.download_failure), -1)
            notification!!.setAutoCancel(true)
            notificationManager.notify(1, notification!!.build())
            val intent = Intent("DONE")
            sendBroadcast(intent)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getNotification(
        title: String,
        progress: Int
    ){
        if(notification == null) {
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
            val channel = NotificationChannel(CHANNEL_ID, "待办消息", NotificationManager.IMPORTANCE_LOW)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(500)
            notificationManager.createNotificationChannel(channel)

            val intent = Intent(this, HomeScreenActivity::class.java)
            val pi = getActivity(this, 0, intent, FLAG_IMMUTABLE or FLAG_ONE_SHOT)
            notification!!.setContentIntent(pi)
                .setSmallIcon(R.drawable.icon) //设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.icon
                    )
                ) //设置通知的大图标
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setChannelId(CHANNEL_ID)
        }
        notification!!.setContentTitle(title)
        if(progress > 0) {
            notification!!.setContentText("下载进度: $progress%")
                .setProgress(100, progress, false)
        }
    }

    private fun installApk(
        fileName: String
    ) {
        val file = File(path, fileName)
        if (!file.exists()) {
            return
        }
        val uri =
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
        val broadcastIntent = Intent("DONE")
        runBlocking {
            delay(10000L)
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    // 创建 DownloadBinder 实例
    inner class DownloadBinder : Binder() {
        fun startDownload() {
            mCoroutineScope.launch {
                Log.d(TAG, "startDownload--------: 开始下载")
                if (downloadFileTask == null) {
                    downloadFileTask = DownloadTask(listener, downloadUrl)
                }
                getNotification("正在请求下载...", 0)
                notificationManager.notify(1, notification!!.build())
                path?.let { (downloadFileTask as DownloadTask).download(it) }
            }
        }
    }

    override fun onDestroy() {
        val fileName: String = downloadUrl.substring(downloadUrl.lastIndexOf("/")).drop(1)
        val file = File(path , fileName)
        if (file.exists()) {
            file.delete()
            Log.d("TAG", "delete file $fileName")
        }
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    companion object {
        const val TAG = "DownloadService"

        const val CHANNEL_ID = "DownloadTask"
    }
}