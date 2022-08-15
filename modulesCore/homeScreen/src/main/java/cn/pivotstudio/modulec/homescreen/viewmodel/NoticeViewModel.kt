package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.model.Notice
import cn.pivotstudio.modulec.homescreen.repository.NoticeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoticeViewModel : ViewModel() {

    private val dataSource = NoticeRepo()

    private val _replies = MutableStateFlow<List<Notice>>(emptyList())

    val replies: StateFlow<List<Notice>> = _replies
    val state = dataSource.state

    init {
        viewModelScope.launch {
            loadReplies()
        }
    }
    fun loadReplies() {
        dataSource.loadReplies(_replies)
    }

    fun loadMore() {
        dataSource.loadMore(_replies)
    }

}