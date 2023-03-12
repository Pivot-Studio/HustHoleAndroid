package cn.pivotstudio.husthole.moduleb.network.download

interface DownloadStateListener {
    //通知下载进度
    fun onProgress(progress: Int)

    fun onSuccess()

    fun onFailure()
}