package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
class ForestViewModel : ViewModel() {
    private val repository = ForestRepository()

    val forestHoles: LiveData<List<ForestHole>> = repository.holes
    val forestHeads: LiveData<ForestHeads> = repository.headers
    val holesLoadState: LiveData<LoadStatus> = repository.holeState
    val headerLoadState: LiveData<LoadStatus> = repository.headerLoadState

    val tip: MutableLiveData<String?> = repository.tip

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

    fun giveALikeToTheHole(hole: ForestHole) {
        repository.giveALikeToTheHole(hole)
    }

    fun followTheHole(hole: ForestHole) {
        repository.followTheHole(hole)
    }

    fun deleteTheHole(hole: ForestHole) {
        repository.deleteTheHole(hole)
    }

    fun doneShowingTip() {
        tip.value = null
    }

}