package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
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

    val forestHoles: LiveData<List<ForestHole>> = repository.forestHoles
    val forestHeads: LiveData<ForestHeads> = repository.forestHeads

    val loadState: LiveData<LoadStatus> = repository.state

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

    fun followTheHole(holeId: Int) {
        val hole = repository.forestHoles.value?.first {
            it.holeId == holeId
        }

        hole?.run {
            repository.followTheHole(this)
            if (followed)
                followNum -= 1
            else
                followNum += 1
            followed = !followed
        }
    }

    fun deleteTheHole(hole: ForestHole) {
        repository.deleteTheHole(hole)
    }

}