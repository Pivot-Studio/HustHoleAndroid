package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.model.Hole
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository

class ForestDetailViewModel(val forestId: Int) : ViewModel() {
    val repository = ForestDetailRepository()

    val holes = repository.holes
    val overview = repository.overview
    val state = repository.state

    init {
        loadHoles()
        loadOverview()
    }

    fun loadHoles() {
        repository.loadHolesByForestId(forestId)
    }

    fun loadOverview() {
        repository.loadOverviewByForestId(forestId)
    }

    fun loadMore() {
        repository.loadMoreHolesByForestId(forestId)
    }

    fun checkIfJoinedTheForest(forestsJoined: List<ForestHead>) {
        overview.value?.let { overview ->
            forestsJoined.forEach {
                if (it.forestId == overview.forestId)
                    overview.Joined = true
            }
        }

    }

    fun giveALikeToTheHole(holeId: Int) {
        val hole = repository.holes.value?.first {
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
        val hole = repository.holes.value?.first {
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

    fun deleteTheHole(hole: Hole) {
        repository.deleteTheHole(hole as DetailForestHole)
    }

    fun joinTheForest() {
        repository.joinTheForest(forestId)
    }

    fun quitTheForest() {
        repository.quitTheForest(forestId)
    }

}

class ForestDetailViewModelFactory(private val forestId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForestDetailViewModel(forestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}