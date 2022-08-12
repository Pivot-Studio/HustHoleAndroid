package cn.pivotstudio.modulec.loginandregister.network

import cn.pivotstudio.husthole.moduleb.network.NetworkApi

object LoginAndRegisterNetworkApi {
    val retrofitService: LRRequestInterface by lazy {
        NetworkApi.createService(LRRequestInterface::class.java)
    }
}