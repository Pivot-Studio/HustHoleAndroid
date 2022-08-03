package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val forestHoles: LiveData<List<ForestHole>> = repository.forestHoles
    val forestHeads: LiveData<ForestHeads> = repository.forestHeads

    val holesLoadState: LiveData<LoadStatus> = repository.holeState
    val headerLoadState: LiveData<LoadStatus> = repository.headerLoadState

    val toastMsg = repository.loadToast.asSharedFlow()
    val changedHoleSharedFlow = repository.changedHoleSharedFlow

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

}