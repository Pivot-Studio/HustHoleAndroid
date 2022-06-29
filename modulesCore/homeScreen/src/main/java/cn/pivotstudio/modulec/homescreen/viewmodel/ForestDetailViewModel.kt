package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository

class ForestDetailViewModel(val forestId: Int) : ViewModel() {
    val repository = ForestDetailRepository()

    val holes = repository.holes
    val overview = repository.overview

    init {
        loadHolesByForestId(forestId = forestId)
        loadOverviewByForestId(forestId = forestId)
    }

    fun loadHolesByForestId(forestId: Int) {
        repository.loadHolesByForestId(forestId)
    }

    fun loadOverviewByForestId(forestId: Int) {
        repository.loadOverviewByForestId(forestId)
    }

    fun checkIfJoinedTheForest(forestsJoined: List<ForestHead>) {
        overview.value?.let { overview ->
            forestsJoined.forEach {
                if (it.forestId == overview.forestId)
                    overview.Joined = true
            }
        }

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