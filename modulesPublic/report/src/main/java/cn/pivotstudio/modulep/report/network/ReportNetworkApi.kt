package cn.pivotstudio.modulep.report.network

import cn.pivotstudio.husthole.moduleb.network.NetworkApi

object ReportNetworkApi {
    val retrofitService: ReportRequestInterface by lazy {
        NetworkApi.createService(ReportRequestInterface::class.java)
    }
}