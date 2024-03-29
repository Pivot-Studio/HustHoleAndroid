package cn.pivotstudio.modulep.hole.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil
import cn.pivotstudio.moduleb.rebase.lib.base.app.BaseApplication
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import java.util.*

class HoleViewModel : ViewModel() {
    @JvmField
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>>
    private val repository = ActivityRepository()

    val fragmentStack: Stack<Fragment> = Stack()
    init {
        pUsedEmojiList = repository.pUsedEmojiList
    }
    fun usedEmojiList() = repository.usedEmojiForLocalDB


    inner class ActivityRepository {
        var pUsedEmojiList: MutableLiveData<LinkedList<Int>> = MutableLiveData()
        val usedEmojiForLocalDB: Unit
            get() {
                val mmkvUtil = MMKVUtil.getMMKV(BaseApplication.context)
                val list = mmkvUtil.getArray(Constant.UsedEmoji, 0) as LinkedList<Int>
                pUsedEmojiList.postValue(list)
            }
    }
}