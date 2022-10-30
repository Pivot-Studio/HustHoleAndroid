package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.ReplyNotice
import cn.pivotstudio.modulec.homescreen.repository.NoticeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoticeViewModel : ViewModel() {

    private val repository = NoticeRepo()

    private val _replies = MutableStateFlow<List<ReplyNotice>>(mutableListOf())
    private val _showPlaceholder = MutableStateFlow(false)

    val replies: StateFlow<List<ReplyNotice>> = _replies
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder
    val state = repository.state

    init {
        loadReplies()
    }

    fun loadReplies() {
        viewModelScope.launch {
            repository.loadRepliesV2()
                .collectLatest {
                    _showPlaceholder.emit(it.isEmpty())
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
        } }
}