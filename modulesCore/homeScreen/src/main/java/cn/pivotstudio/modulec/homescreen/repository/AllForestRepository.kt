package cn.pivotstudio.modulec.homescreen.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.model.ForestTypes
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import okhttp3.internal.wait

@SuppressLint("CheckResult")
class AllForestRepository {

    private var _forestTypes = MutableLiveData<ForestTypes>()
    private var _forestCardWithOneType = MutableLiveData<Pair<String, ForestCardList>>()
    private var _forestCards = HashSet<Pair<String, ForestCardList>>()

    val forestTypes = _forestTypes
    val forestCardWithOneType = _forestCardWithOneType
    val forestCards = _forestCards


    fun loadAllTypeOfForestCards() {
        HomeScreenNetworkApi.retrofitService
            .searchForestTypes(STARTING_ID, TYPE_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestTypes>() {
                override fun onSuccess(forestTypes: ForestTypes) {
                    Log.d(TAG, "loadAllType onSuccess: ${forestTypes.types.size}")
                    _forestTypes.value = forestTypes
                    forestTypes.types.forEach { type ->
                        Log.d(TAG, "load type of $type")
                        loadForestCardsBy(type)
                    }
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                    Log.d(TAG, "onFailure: 请求失败")
                }
            }))
    }

   private fun loadForestCardsBy(type: String) {
        HomeScreenNetworkApi.retrofitService
            .searchForestByType(type, STARTING_ID, CARD_LIST_SIZE)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestCardList>() {
                override fun onSuccess(items: ForestCardList) {
                    _forestCards.add(Pair(type, items))
                    _forestCardWithOneType.value = Pair(type, items)
                    Log.d(TAG, "loadForestCards by $type onSuccess: ${items.forests.size}")
                }

                override fun onFailure(e: Throwable?) {
                    e?.printStackTrace()
                    Log.d(TAG, "loadForestCards by $type onFailure: 请求失败")
                }

            }))
    }

    companion object {
        const val STARTING_ID = 0
        const val TYPE_LIST_SIZE = 10
        const val CARD_LIST_SIZE = 10
        const val TAG = "AllForestRepository"
    }
}