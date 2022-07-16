package cn.pivotstudio.modulec.homescreen.network

import cn.pivotstudio.husthole.moduleb.network.NetworkApi

object HomeScreenNetworkApi {
    val retrofitService: HSRequestInterface by lazy {
        NetworkApi.createService(HSRequestInterface::class.java)
    }
}