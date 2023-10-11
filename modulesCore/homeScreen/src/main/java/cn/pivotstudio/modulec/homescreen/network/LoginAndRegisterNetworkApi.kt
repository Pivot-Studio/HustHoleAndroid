package cn.pivotstudio.modulec.homescreen.network

import cn.pivotstudio.moduleb.rebase.network.NetworkApi

object LoginAndRegisterNetworkApi {
    val retrofitService: LRRequestInterface by lazy {
        NetworkApi.createService(LRRequestInterface::class.java)
    }
}