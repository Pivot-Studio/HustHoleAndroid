package cn.pivotstudio.modulep.hole.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.modulep.hole.repository.InnerReplyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InnerReplyViewModel(baseReply: Reply) : ViewModel() {

    private val repo = InnerReplyRepo()

    private var _reply = MutableStateFlow(baseReply)
    val reply: StateFlow<Reply> = _reply

    private var _innerReplies = MutableStateFlow(listOf<Reply>())
    val innerReplies: StateFlow<List<Reply>> = _innerReplies

    private var _showingEmojiPad = MutableStateFlow(false)
    val showEmojiPad: StateFlow<Boolean> = _showingEmojiPad

    fun triggerEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(showEmojiPad.value.not())
        }
    }

    fun loadSecondLvReplies() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.loadInnerReplies(reply.value.replyId).collectLatest {
                _innerReplies.emit(it)
            }
        }
    }

    init {
        loadSecondLvReplies()
    }

}