package cn.pivotstudio.husthole.moduleb.network.errorhandler

object ErrorCodeHandlerV2 {
    //请求错误
    private const val BAD_REQUEST = 400

    //未授权
    private const val UNAUTHORIZED = 401

    //禁止的
    private const val FORBIDDEN = 403

    //未找到
    private const val NOT_FOUND = 404

    //请求超时
    private const val REQUEST_TIMEOUT = 408

    //内部服务器错误
    private const val INTERNAL_SERVER_ERROR = 500

    //错误网关
    private const val BAD_GATEWAY = 502

    //暂停服务
    private const val SERVICE_UNAVAILABLE = 503

    //网关超时
    private const val GATEWAY_TIMEOUT = 504

    fun handleErrorCode2String(code: Int): String {
       return when (code) {
           BAD_REQUEST -> "请求错误"
           UNAUTHORIZED -> "未授权"
           FORBIDDEN -> "禁止的"
           NOT_FOUND -> "404"
           REQUEST_TIMEOUT -> "请求超时"
           INTERNAL_SERVER_ERROR -> "服务器错误"
           BAD_GATEWAY -> "错误网关"
           SERVICE_UNAVAILABLE -> "暂停服务"
           GATEWAY_TIMEOUT -> "网关超时"
           else -> "未知错误"
        }
    }

}