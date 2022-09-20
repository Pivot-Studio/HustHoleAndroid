package cn.pivotstudio.modulec.homescreen.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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
    val pHomePageHoles: LiveData<HomepageHoleResponse> =
        repository.pHomePageHoles //数据类型网络请求结果

    private var _holesV2 = MutableStateFlow<List<HoleV2>>(mutableListOf())
    val holesV2: StateFlow<List<HoleV2>> = _holesV2

    private var _isLatestReply = MutableStateFlow(false)
    val isLatestReply: StateFlow<Boolean> = _isLatestReply

    private var _loadLaterHoleId = -1

    init {
        loadHolesV2()
    }

    fun loadHolesV2() {
        viewModelScope.launch {
            repository.loadHoles()
                .collectLatest {
                    _holesV2.emit(it)
                }
        }
    }

    fun loadHoleLater(holeId: Int) {
        _loadLaterHoleId = holeId
    }


    val tip: MutableLiveData<String?> = repository.tip
    private var mIsSearch: Boolean? = null //是否是搜索状态
    private var mSearchKeyword: String? = null //搜索关键词
    private var mIsDescend: Boolean? = null //是新发布树洞还是新更新树洞
    private var mStartLoadId: Int? = null //网络起始id
    var pClickDataBean: DataBean? = null
    var isSearch: Boolean?
        get() {
            if (mIsSearch == null) {
                mIsSearch = false
            }
            return mIsSearch
        }
        set(pIsSearch) {
            mIsSearch = pIsSearch
        }
    var searchKeyword: String?
        get() {
            if (mSearchKeyword == null) {
                mSearchKeyword = ""
            }
            return mSearchKeyword
        }
        set(pSearchKeyword) {
            mSearchKeyword = pSearchKeyword
        }
    var isDescend: Boolean?
        get() {
            if (mIsDescend == null) {
                mIsDescend = true
            }
            return mIsDescend
        }
        set(pIsDescend) {
            mIsDescend = pIsDescend
        }
    var startLoadId: Int?
        get() {
            if (mStartLoadId == null) {
                mStartLoadId = 0
            }
            return mStartLoadId
        }
        set(pStartLoadId) {
            mStartLoadId = pStartLoadId
        }

    /**
     * 获取正常树洞列表
     *
     * @param mStartingLoadId 起始id
     */
    fun refreshHoleList(mStartingLoadId: Int) {
        isDescend?.let { repository.getHolesForNetwork(it, mStartingLoadId) }
    }

    /**
     * 搜索单个树洞
     */
    fun searchSingleHole() {
        repository.searchSingleHoleForNetwork(searchKeyword!!)
    }

    /**
     * 搜索相关关键词的所有树洞
     *
     * @param mStartingLoadId 起始id
     */
    fun searchHoleList(mStartingLoadId: Int) {
        repository.searchHolesForNetwork(searchKeyword, mStartingLoadId)
    }

    /**
     * 涉及到网络请求相关的点击事件
     *
     * @param v        被点击的view
     * @param dataBean item的数据
     */
    fun itemClick(v: View, dataBean: DataBean) {
        val holeId = dataBean.hole_id
        val id = v.id
        if (id == R.id.cl_item_homepage_thumbup) { //点击点赞
            val isThumbup = dataBean.is_thumbup
            val thumbupNum = dataBean.thumbup_num
            repository.giveALikeToAHole(holeId, thumbupNum, isThumbup, dataBean)
        } else if (id == R.id.cl_item_homepage_reply) { //点击回复
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                    .withBoolean(Constant.IF_OPEN_KEYBOARD, true).navigation()
            }
        } else if (id == R.id.cl_item_homepage_follow) { //点击收藏
            val isFollow = dataBean.is_follow
            val followNum = dataBean.follow_num
            repository.followForNetwork(holeId, followNum, isFollow, dataBean)
        } else if (id == R.id.btn_item_homepage_jump_to_detail_forest) { //点击小树林图标
        } else if (id == R.id.cl_item_homepage_morelist) { //点击右下角三个点
            val isMine = dataBean.is_mine
            if (isMine) {
                val dialog = DeleteDialog(v.context)
                dialog.show()
                dialog.setOptionsListener {
                    repository.moreActionForNetwork(
                        holeId, isMine
                    )
                }
            } else {
                repository.moreActionForNetwork(holeId, isMine)
            }
            v.visibility = View.GONE
        } else if (id == R.id.cl_item_homepage_frame) { //点击树洞跳转
            pClickDataBean = dataBean
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/hole/HoleActivity")
                    .withInt(Constant.HOLE_ID, holeId)
                    .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                    .navigation(v.context as HomeScreenActivity, ResultCodeConstant.Hole)
                //
            } else {
                //测试阶段不可跳转
            }
        }
    }

    fun doneShowingTip() {
        tip.value = null
    }
}