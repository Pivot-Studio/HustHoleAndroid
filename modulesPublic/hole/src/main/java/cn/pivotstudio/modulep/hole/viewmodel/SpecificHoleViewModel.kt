package cn.pivotstudio.modulep.hole.viewmodel

import android.view.View
import cn.pivotstudio.modulep.hole.model.HoleResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel
import cn.pivotstudio.modulep.hole.BuildConfig
import cn.pivotstudio.modulep.hole.repository.HoleRepository
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulep.hole.model.MsgResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * @classname:HoleViewModel
 * @description:
 * @date:2022/5/8 15:57
 * @version:1.0
 * @author:
 */
class SpecificHoleViewModel(private var holeId: String) : BaseViewModel() {

    private var _hole = MutableStateFlow<HoleV2?>(null)
    val hole: StateFlow<HoleV2?> = _hole

    private var _replies = MutableStateFlow<List<ReplyWrapper>>(mutableListOf())
    val replies: StateFlow<List<ReplyWrapper>> = _replies

    private var _showingEmojiPad = MutableStateFlow(false)
    val showEmojiPad: StateFlow<Boolean> = _showingEmojiPad

    fun triggerEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(showEmojiPad.value.not())
        }
    }

    //网路数据
    var pInputText: MutableLiveData<ReplyResponse>
    var pClickMsg: MutableLiveData<MsgResponse>
    var pSendReply: MutableLiveData<MsgResponse>

    @JvmField
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>>

    var start_id: Int? = null
        get() {
            if (field == null) field = 0
            return field
        }

    //下面三项非网络请求所得，但是变化需要及时反馈在ui上
    var answered: ObservableField<ReplyResponse?>
    private var is_descend: ObservableField<Boolean?>
    var is_owner: ObservableField<Boolean?>
    var is_emoji: ObservableField<Boolean?>

    //进行数据请求的地方
    private val repository: HoleRepository = HoleRepository(holeId.toString())
    val usedEmojiList: Unit
        get() {
            repository.usedEmojiForLocalDB
        }

    @JvmName("getAnswered1")
    fun getAnswered(): ObservableField<ReplyResponse?> {
        if (answered.get() == null) {
            val base = ReplyResponse()
            base.alias = "洞主"
            base.is_mine = false
            base.reply_local_id = -1
            answered.set(base)
        }
        return answered
    }

    @JvmName("setAnswered1")
    fun setAnswered(answered: ObservableField<ReplyResponse?>) {
        this.answered = answered
    }

    fun getIs_owner(): ObservableField<Boolean?> {
        if (is_owner.get() == null) is_owner.set(false)
        return is_owner
    }

    fun setIs_owner(is_owner: ObservableField<Boolean?>) {
        this.is_owner = is_owner
    }

    fun getIs_descend(): ObservableField<Boolean?> {
        if (is_descend.get() == null) is_descend.set(false)
        return is_descend
    }

    fun setIs_descend(is_descend: ObservableField<Boolean?>) {
        this.is_descend = is_descend
    }

    fun getIs_emoji(): ObservableField<Boolean?> {
        if (is_emoji.get() == null) is_emoji.set(false)
        return is_emoji
    }

    fun setIs_emoji(is_emoji: ObservableField<Boolean?>) {
        this.is_emoji = is_emoji
    }

    val inputText: Unit
        get() {
            repository.getInputTextForLocalDB(holeId.toInt())
        }

    fun saveInputText(text: String?) {
        val answered = getAnswered().get()
        repository.saveInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.alias,
            answered.is_mine,
            answered.reply_local_id
        )
    }

    fun updateInputText(text: String?) {
        val answered = getAnswered().get()
        repository.updateInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.alias,
            answered.is_mine,
            answered.reply_local_id
        )
    }

    fun deleteInputText() {
        repository.deleteInputTextForLocalDB()
    }

    fun sendReply(content: String?) {
        if (!BuildConfig.isRelease) { //供测试阶段使用
            holeId = "79419"
        }
        repository.sendReplyForNetwork(
            content,
            holeId.toInt(),
            getAnswered().get()!!.reply_local_id
        )
    }

    fun getListData(ifLoadMore: Boolean) {

    }

    fun loadMoreReplies() {
        viewModelScope.launch {
            repository.loadMoreReplies()
                .collectLatest { holes ->
                    _replies.emit(replies.value.toMutableList().apply { addAll(holes) })
                }
        }
    }


    fun loadHole() {
        viewModelScope.launch {
            repository.apply {
                loadHole().zip(loadReplies()) { hole, replies ->
                    hole to replies
                }.collectLatest {
                    _hole.emit(it.first)
                    _replies.emit(it.second)
                }
            }
        }
    }

    /**
     * 涉及到网络请求相关的点击事件
     *
     * @param v        被点击的view
     * @param dataBean item的数据
     */
    fun itemClick(v: View, dataBean: HoleResponse) {
        val holeId = dataBean.hole_id
        val id = v.id
        if (id == R.id.cl_hole_thumbup) {
            val isThunbup = dataBean.is_thumbup
            val thumbupNum = dataBean.thumbup_num
            repository.thumbupForNetwork(holeId, thumbupNum, isThunbup, dataBean)
        } else if (id == R.id.cl_hole_reply) {
            val `as` = ReplyResponse()
            `as`.reply_local_id = -1
            `as`.is_mine = false
            `as`.alias = "洞主"
            answered.set(`as`)
        } else if (id == R.id.cl_hole_follow) {
            val isFollow = dataBean.is_follow
            val followNum = dataBean.follow_num
            repository.followForNetwork(holeId, followNum, isFollow, dataBean)
        } else if (id == R.id.btn_hole_jumptodetailforest) {
        } else if (id == R.id.cl_hole_more_action) {
            val isMine = dataBean.is_mine
            if (isMine) {
                val dialog = DeleteDialog(v.context)
                dialog.show()
                dialog.setOptionsListener { v1: View? ->
                    repository.moreActionForNetwork(
                        holeId,
                        isMine,
                        -1,
                        "洞主"
                    )
                }
            } else {
                repository.moreActionForNetwork(holeId, isMine, -1, "洞主")
            }
            v.visibility = View.GONE
        } else if (id == R.id.cl_hole_changesequence) {
            val observableField = getIs_descend()
            observableField.set(!observableField.get()!!)
            getListData(false)
        } else if (id == R.id.tv_hole_content) {
            val `as` = ReplyResponse()
            `as`.reply_local_id = -1
            `as`.is_mine = false
            `as`.alias = "洞主"
            answered.set(`as`)
        }
    }

    fun replyItemClick(v: View, dataBean: ReplyResponse) {
        val id = v.id
        val reply_local_id = dataBean.reply_local_id
        val hole_id = dataBean.hole_id
        if (id == R.id.cl_reply_thumbup) {
            val isThunbup = dataBean.is_thumbup
            val thumbupNum = dataBean.thumbup_num
            repository.thumbupReplyForNetwork(
                hole_id,
                reply_local_id,
                thumbupNum,
                isThunbup,
                dataBean
            )
        } else if (id == R.id.cl_reply_morelist) {
            val isMine = dataBean.is_mine
            if (isMine) {
                val dialog = DeleteDialog(v.context)
                dialog.show()
                dialog.setOptionsListener { v1: View? ->
                    repository.moreActionForNetwork(
                        hole_id,
                        isMine,
                        reply_local_id,
                        dataBean.alias
                    )
                }
            } else {
                repository.moreActionForNetwork(
                    hole_id,
                    isMine,
                    reply_local_id,
                    dataBean.alias
                )
            }
            v.visibility = View.GONE
        } else if (id == R.id.cl_reply || id == R.id.tv_reply_content) {
            //setReply_local_id(dataBean.getReply_local_id());
            answered.set(dataBean)
        }
    }

    /**
     * 构造函数
     */
    init {

        loadHole()

        pInputText = repository.pInputText
        pClickMsg = repository.pClickMsg
        pSendReply = repository.pSendReply
        pUsedEmojiList = repository.pUsedEmojiList
        failed = repository.failed
        answered = ObservableField()
        is_owner = ObservableField()
        is_descend = ObservableField()
        is_emoji = ObservableField()
    }
}