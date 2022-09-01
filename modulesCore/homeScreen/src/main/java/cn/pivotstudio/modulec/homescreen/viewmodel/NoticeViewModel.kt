package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.model.Notice
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.repository.NoticeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoticeViewModel : ViewModel() {

    private val dataSource = NoticeRepo()

    private val _replies = MutableStateFlow<List<Notice>>(emptyList())
    private val _showPlaceholder = MutableStateFlow(false)

    val replies: StateFlow<List<Notice>> = _replies
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder
    val state = dataSource.state

    init {
        loadRepliesFlow()
    }

    fun loadReplies() {
        dataSource.loadReplies(_replies)
    }

    fun loadMore() {
        dataSource.loadMore(_replies)
    }

    /** Flow + Retrofit 迁移实验 **/
    fun loadRepliesFlow() {
        viewModelScope.launch {
            flow {
                emit(HomeScreenNetworkApi.retrofitService.searchNoticesFlow(0, 20))
            }.flowOn(Dispatchers.IO).catch { e ->
                e.printStackTrace()
            }.collect {
                _replies.value = it.notices!!
                _showPlaceholder.value = _replies.value.isEmpty()
            }
        }
    }

}