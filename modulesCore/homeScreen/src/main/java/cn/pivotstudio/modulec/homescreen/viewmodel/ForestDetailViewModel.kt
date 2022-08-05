package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.model.Hole
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository
import com.alibaba.android.arouter.launcher.ARouter

class ForestDetailViewModel(val forestId: Int) : ViewModel() {
    val repository = ForestDetailRepository()

    val holes = repository.holes
    val overview = repository.overview
    val state = repository.state
    val tip: MutableLiveData<String?> = repository.tip

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

    fun giveALikeToTheHole(hole: Hole) {
            repository.giveALikeToTheHole(hole as DetailForestHole)
    }

    fun followTheHole(hole: Hole) {
        repository.followTheHole(hole as DetailForestHole)
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

    fun doneShowingTip() {
        tip.value = null
    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation()
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