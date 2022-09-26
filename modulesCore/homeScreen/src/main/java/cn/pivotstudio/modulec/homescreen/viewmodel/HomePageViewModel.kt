package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * @classname:HomePageViewModel
 * @description:
 * @date:2022/5/2 23:09
 * @version:1.0
 * @author:
 */
class HomePageViewModel : BaseViewModel() {
    private val repository = HomePageHoleRepository()

    private var _holesV2 = MutableStateFlow<List<HoleV2>>(mutableListOf())
    val holesV2: StateFlow<List<HoleV2>> = _holesV2

    private var _isLatestReply = MutableStateFlow(true)
    val isLatestReply: StateFlow<Boolean> = _isLatestReply

    private var _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var _sortMode: String = NetworkConstant.SortMode.LATEST_REPLY

    private var _loadLaterHoleId = -1

    init {
        loadHolesV2()
    }

    fun loadHolesV2(sortMode: String = _sortMode) {
        isSearch = false
        when (sortMode) {
            NetworkConstant.SortMode.LATEST_REPLY -> _isLatestReply.value = true
            else -> _isLatestReply.value = false
        }

        viewModelScope.launch {
            _loading.emit(true)
            repository.loadHoles(sortMode)
                .onEach {
                    _loading.emit(false)
                    _sortMode = sortMode
                }
                .collectLatest {
                    it.forEach { hole ->
                        hole.isLatestReply = isLatestReply.value
                    }
                    _holesV2.emit(it)
                }
        }
    }

    fun loadMoreHoles(sortMode: String = _sortMode) {
        if (isSearch) {
            loadMoreSearchHoles()
            return
        }
        viewModelScope.launch {
            repository.loadMoreHoles(sortMode)
                .collectLatest {
                    _holesV2.emit(_holesV2.value.toMutableList().apply { addAll(it) })
                }
        }
    }

    fun searchHolesV2(queryKey: String) {
        queryKey.takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                _loading.emit(true)
                repository.searchHolesBy(it)
                    .onEach { _loading.emit(false) }
                    .collectLatest { holes ->
                        _holesV2.emit(holes)
                    }
            }
        }
    }

    private fun loadMoreSearchHoles() {
        viewModelScope.launch {
            repository.loadMoreSearchHoles(searchKeyword)
                .collectLatest {
                    _holesV2.emit(_holesV2.value.toMutableList().apply { addAll(it) })
                }
        }
    }

    fun loadHoleLater(holeId: Int) {
        _loadLaterHoleId = holeId
    }


    val tip: MutableLiveData<String?> = repository.tip
    private var mIsSearch: Boolean = false //是否是搜索状态
    private var mSearchKeyword: String = "" //搜索关键词
    var pClickDataBean: DataBean? = null
    var isSearch: Boolean
        get() {
            return mIsSearch
        }
        set(pIsSearch) {
            mIsSearch = pIsSearch
        }


    var searchKeyword: String
        get() {
            return mSearchKeyword
        }
        set(pSearchKeyword) {
            mSearchKeyword = pSearchKeyword
        }

    fun doneShowingTip() {
        tip.value = null
    }
}