package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi

@SuppressLint("CheckResult")
class AllForestRepository {

    private var _forestTypes = MutableLiveData<List<String>>()
    private var _forestCardWithOneType = MutableLiveData<List<ForestCard>>()
    private var _forestCards = MutableLiveData<List<List<ForestCard>>>()

    val forestTypes = _forestTypes
    val forestCards = _forestCards

    fun loadAllTypeOfForestCards() {
        HomeScreenNetworkApi.retrofitService
            .searchForestTypes(STARTING_ID, TYPE_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<String>>() {
                override fun onSuccess(items: List<String>?) {
                    forestTypes.value = items
                    items?.forEach { type ->
                        loadForestCardsBy(type)
                    }
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                }
            }))
    }

    fun loadForestCardsBy(type: String) {
        HomeScreenNetworkApi.retrofitService
            .searchForestByType(type, STARTING_ID, CARD_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<List<ForestCard>>() {
                override fun onSuccess(items: List<ForestCard>) {
                    _forestCardWithOneType.value = items
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                }

            }))
    }

    companion object {
        const val STARTING_ID = 0
        const val TYPE_LIST_SIZE = 10
        const val CARD_LIST_SIZE = 10
    }
}