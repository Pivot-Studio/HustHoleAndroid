package cn.pivotstudio.modulec.homescreen.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.constant.Constant;
import com.example.libbase.base.viewmodel.BaseViewModel;


import cn.pivotstudio.modulec.homescreen.BuildConfig;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.custom_view.OptionsListener;
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.DeleteDialog;
import cn.pivotstudio.modulec.homescreen.model.HomepageHoleResponse;
import cn.pivotstudio.modulec.homescreen.model.MsgResponse;
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository;
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity;

/**
 * @classname:HomePageViewModel
 * @description:
 * @date:2022/5/2 23:09
 * @version:1.0
 * @author:
 */
public class HomePageViewModel extends BaseViewModel {
    public MutableLiveData<HomepageHoleResponse> pHomePageHoles;//数据类型网络请求结果
    public MutableLiveData<MsgResponse> pClickMsg;//通知类型网路请求结果

    private Boolean mIsSearch;//是否是搜索状态
    private String mSearchKeyword;//搜索关键词
    private Boolean mIsDescend;//是新发布树洞还是新更新树洞
    private Integer mStartLoadId;//网络起始id

    public HomepageHoleResponse.DataBean pClickDataBean;

    private final HomePageHoleRepository mHomePageHoleRepository;

    /**
     * 初始化
     */
   public HomePageViewModel() {
       mHomePageHoleRepository = new HomePageHoleRepository();
       pHomePageHoles=mHomePageHoleRepository.pHomePageHoles;
       pClickMsg=mHomePageHoleRepository.pClickMsg;
       failed = mHomePageHoleRepository.failed;
   }

    public Boolean getIsSearch() {
        if(mIsSearch ==null){mIsSearch =false;}
        return mIsSearch;
    }
    public void setIsSearch(Boolean pIsSearch) {
        this.mIsSearch = pIsSearch;
    }


    public String getSearchKeyword() {
       if(mSearchKeyword==null){mSearchKeyword="";}
        return mSearchKeyword;
    }

    public void setSearchKeyword(String pSearchKeyword) {
        this.mSearchKeyword = pSearchKeyword;
    }



    public Boolean getIsDescend() {
       if(mIsDescend ==null){mIsDescend =true;}
        return mIsDescend;
    }

    public void setIsDescend(Boolean pIsDescend) {
        this.mIsDescend = pIsDescend;
    }


    public Integer getStartLoadId() {
       if(mStartLoadId ==null){ mStartLoadId =0;}
        return mStartLoadId;
    }

    public void setStartLoadId(Integer pStartLoadId) {
        this.mStartLoadId = pStartLoadId;
    }





    /**
     * 获取正常树洞列表
     * @param mStartingLoadId 起始id
     */
    public void refreshHoleList(int mStartingLoadId){
           mHomePageHoleRepository.getHolesForNetwork(this.getIsDescend(), mStartingLoadId);
   }

    /**
     * 搜索单个树洞
     */
    public void searchSingleHole(){
        mHomePageHoleRepository.searchSingleHoleForNetwork(getSearchKeyword());
    }

    /**
     * 搜索相关关键词的所有树洞
     * @param mStartingLoadId 起始id
     */
    public void searchHoleList(int mStartingLoadId){
        mHomePageHoleRepository.searchHolesForNetwork(getSearchKeyword(),mStartingLoadId);
    }

    /**
     * 涉及到网络请求相关的点击事件
     * @param v 被点击的view
     * @param dataBean item的数据
     */
    public void itemClick(View v,HomepageHoleResponse.DataBean dataBean) {
        Integer holeId=dataBean.getHole_id();
        int id = v.getId();
        if (id == R.id.cl_itemhomepage_thumbup) {//点击点赞
            Boolean isThunbup = dataBean.getIs_thumbup();
            Integer thumbupNum = dataBean.getThumbup_num();
            mHomePageHoleRepository.thumbupForNetwork(holeId, thumbupNum, isThunbup, dataBean);
        } else if (id == R.id.cl_itemhomepage_reply) {//点击回复
            if(BuildConfig.isRelease) {
                ARouter.getInstance().build("/hole/HoleActivity")
                        .withInt(Constant.HOLE_ID, holeId)
                        .withBoolean(Constant.IF_OPEN_KEYBOARD,true)
                        .navigation();
            }else{
                //测试阶段不可跳转
            }
        } else if (id == R.id.cl_itemhomepage_follow) {//点击收藏
            Boolean isFollow = dataBean.getIs_follow();
            Integer followNum = dataBean.getFollow_num();
            mHomePageHoleRepository.followForNetwork(holeId, followNum, isFollow, dataBean);
        } else if (id == R.id.btn_itemhomepage_jumptodetailforest) {//点击小树林图标

        } else if (id == R.id.cl_itemhomepage_morelist) {//点击右下角三个点
            Boolean isMine = dataBean.getIs_mine();
            if (isMine) {
                DeleteDialog dialog = new DeleteDialog(v.getContext());
                dialog.show();
                dialog.setOptionsListener(new OptionsListener() {
                    @Override
                    public void onClick(View v) {
                        mHomePageHoleRepository.moreActionForNetwork(holeId, isMine);
                    }
                });
            } else {
                mHomePageHoleRepository.moreActionForNetwork(holeId, isMine);
            }
            v.setVisibility(View.GONE);
        }else if(id==R.id.cl_itemhomepage_frame){//点击树洞跳转
            this.pClickDataBean=dataBean;
            if(BuildConfig.isRelease) {
                ARouter.getInstance().build("/hole/HoleActivity")
                        .withInt(Constant.HOLE_ID, holeId)
                        .withBoolean(Constant.IF_OPEN_KEYBOARD,false)
                        .navigation((HomeScreenActivity)v.getContext(),1);
//
            }else{
                //测试阶段不可跳转
            }
            }
    }
}
