package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus

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
    val holesLoadState: LiveData<LoadStatus?> = repository.holeState
    val headerLoadState: LiveData<LoadStatus> = repository.headerLoadState

    private var _shouldRefreshHoles = false
    val shouldRefreshHoles = _shouldRefreshHoles

    private var _shouldRefreshHeader = false
    val shouldRefreshHeader = _shouldRefreshHeader

    val tip: MutableLiveData<String?> = repository.tip

    init {
        loadHolesAndHeads()
    }

    fun loadHolesLater() {
        _shouldRefreshHoles = true
    }

    fun loadHeaderLater() {
        _shouldRefreshHeader = true
    }

    /**
     * 从其他页面回来想要刷新
     *  1. loadxxxLater()
     *  2. tryLoadNewxxx() in View
     */
    fun tryLoadNewHoles() {
        if (_shouldRefreshHoles) {
            loadHoles()
            _shouldRefreshHoles = false
        }
    }

    fun tryLoadNewHeader() {
        if (_shouldRefreshHeader) {
            loadHeader()
            _shouldRefreshHeader = false
        }
    }

    fun loadHolesAndHeads() {
        repository.run {
            loadForestHoles()
            loadForestHeader()
        }
    }

    fun loadHeader() = repository.loadForestHeader()

    fun loadHoles() = repository.loadForestHoles()

    fun loadMoreForestHoles() {
        repository.loadMoreForestHoles()
    }

    fun giveALikeToTheHole(hole: ForestHole) =
        repository.giveALikeToTheHole(hole)

    fun followTheHole(hole: ForestHole) {
        repository.followTheHole(hole)
    }

    fun deleteTheHole(hole: ForestHole) {
        repository.deleteTheHole(hole)
    }

    fun doneShowingTip() {
        tip.value = null
    }

    fun showPlaceHolder() {
        repository.holeState.value = null
    }

}