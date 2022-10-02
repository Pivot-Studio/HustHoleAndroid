package cn.pivotstudio.modulep.publishhole.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse
import cn.pivotstudio.modulep.publishhole.model.MsgResponse
import cn.pivotstudio.modulep.publishhole.repository.PublishHoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * @classname:PublishHoleViewModel
 * @description:
 * @date:2022/5/6 12:38
 * @version:1.0
 * @author:
 */
class PublishHoleViewModel : BaseViewModel() {

    private val repository: PublishHoleRepository = PublishHoleRepository()
    private var _forestsMeta = listOf<ForestBrief>()
    private var _forests = MutableStateFlow<List<Pair<String, List<ForestBrief>>>>(mutableListOf())
    private var _joinedForests = MutableStateFlow<List<ForestBrief>>(mutableListOf())

    //所有小树林
    @JvmField
    val typeForestList: LiveData<List<Pair<String, List<ForestBrief>>>> = _forests.asLiveData()

    //加入的小树林
    @JvmField
    var joinedForest: LiveData<List<ForestBrief>> = _joinedForests.asLiveData()

    //小树林类型
    @JvmField
    var pForestType: MutableLiveData<ForestTypeResponse> = repository.pForestType

    //点击事件成功的网络请求结果
    @JvmField
    var pOnClickMsg: MutableLiveData<MsgResponse> = repository.pClickMsg

    //选择的小树林的名字
    @JvmField
    var forestName: MutableLiveData<String> = MutableLiveData()

    //选中的小树林的id
    var forestId: String? = null

    /**
     * 获取加入的小树林
     */
    val joinedForests: Unit
        get() {
            repository.joinedForestForNetwork
        }

    /**
     * 获取小树林类型
     */
    val forestType: Unit
        get() {
            repository.forestTypeForNetwork
        }

    /**
     * 获取某一类型的所有小树林
     *
     * @param forest_type 小树林类型
     * @param location    对应总列表的位置
     */
    fun getTypeForest(forest_type: String?, location: Int) {
        repository.getTypeForestForNetwork(forest_type, location)
    }

    /**
     * 获取热门小树林
     */
    val hotForest: Unit
        get() {
            repository.hotForestForNetwork
        }

    /**
     * 发送树洞
     *
     * @param content 树洞内容
     */
    fun postHoleRequest(content: String?) {
//        repository.publishHole(content, forestId!!)
    }

    fun loadAllForests() {
        viewModelScope.launch {
            repository.loadAllForests()
                .map {
                    _forestsMeta = it
                    transToAllForestsWithType(it)
                }.collectLatest {
                    _forests.emit(it)
                }
        }
    }

    fun loadJoinedForestsV2() {
        viewModelScope.launch {
            repository.loadJoinedForestsV2()
                .collectLatest {
                    _joinedForests.emit(it)
                }
        }
    }

    private fun transToAllForestsWithType(forestList: List<ForestBrief>): List<Pair<String, List<ForestBrief>>> {
        val forests = mutableListOf<Pair<String, List<ForestBrief>>>()
        val forestsWithType = mutableListOf<ForestBrief>()
        if (forestList.isEmpty()) {
            return forests
        }

        var lastType = forestList.first().type
        forestList.forEach {
            if (lastType == it.type) {
                forestsWithType.add(it)
            } else {
                forests.add(Pair(lastType!!, forestsWithType.toList()))
                forestsWithType.clear()
                lastType = it.type
            }
        }
        forests.add(Pair(lastType!!, forestsWithType.toList()))
        return forests
    }

    init {
        failed = repository.failed
    }
}