package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.modulec.homescreen.network.HomeScreenNetworkApi
import cn.pivotstudio.modulec.homescreen.repository.NoticeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoticeViewModel : ViewModel() {

    private val repository = NoticeRepo()

    private val _replies = MutableStateFlow<List<Reply>>(mutableListOf())
    private val _showPlaceholder = MutableStateFlow(false)

    val replies: StateFlow<List<Reply>> = _replies
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder
    val state = repository.state

    init {
        loadReplies()
    }

    fun loadReplies() {
        viewModelScope.launch {
            repository.loadRepliesV2()
                .collectLatest {
                    _replies.emit(it)
                }
        }
    }

    fun loadMoreV2() {
        viewModelScope.launch {
            repository.loadMoreV2()
                .collectLatest {
                    _replies.emit(_replies.value.toMutableList().apply { addAll(it) })
                }
        }
    }

    /** Flow + Retrofit 迁移实验 **/
    fun loadRepliesFlow() {
        viewModelScope.launch {
            flow {
                emit(HomeScreenNetworkApi.retrofitService.searchNoticesFlow(0, 20))
            }.flowOn(Dispatchers.IO).catch { e ->
                e.printStackTrace()
            }.collect {

                _showPlaceholder.value = _replies.value.isEmpty()
            }
        }
    }

}