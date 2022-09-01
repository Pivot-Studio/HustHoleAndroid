package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.husthole.moduleb.network.model.ForestHole
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

    private var _shouldRefreshHeader = false

    private var _loadLaterHoleId = -1

    val tip: MutableLiveData<String?> = repository.tip

    init {
        loadHolesAndHeads()
    }

    fun loadHeaderLater() {
        _shouldRefreshHeader = true
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

    fun loadHoleLater(holeId: Int) {
        _loadLaterHoleId = holeId
    }

    fun refreshLoadLaterHole(
        isThumb: Boolean?,
        replied: Boolean?,
        followed: Boolean?,
        thumbNum: Int?,
        replyNum: Int?,
        followNum: Int?
    ) {
        if (isThumb == null) return
        if (replied == null) return
        if (followed == null) return
        if (thumbNum == null) return
        if (replyNum == null) return
        if (followNum == null) return

        repository.refreshLoadLaterHole(
            _loadLaterHoleId,
            isThumb,
            replied,
            followed,
            thumbNum,
            replyNum,
            followNum
        )
    }

}