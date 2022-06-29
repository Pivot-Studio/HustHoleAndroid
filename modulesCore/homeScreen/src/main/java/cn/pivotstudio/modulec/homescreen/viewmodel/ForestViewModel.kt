package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.*

import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.network.MsgResponse
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
class ForestViewModel : ViewModel() {
    private val repository = ForestRepository()

    val forestHoles: LiveData<List<ForestHole>> = repository.forestHoles
    val forestHeads: LiveData<ForestHeads> = repository.forestHeads

    init {
        loadHolesAndHeads()
    }

    fun loadHolesAndHeads() {
        repository.run {
            loadForestHoles()
            loadForestHeads()
        }
    }

    fun loadMoreForestHoles() {
        repository.loadMoreForestHoles()
    }

    fun giveALikeToTheHole(holeId: Int) {
        val hole = repository.forestHoles.value?.first {
            it.holeId == holeId
        }

        hole?.run {
            repository.giveALikeToTheHole(this)
            if (liked)
                likeNum -= 1
            else
                likeNum += 1
            liked = !liked
        }
    }
}