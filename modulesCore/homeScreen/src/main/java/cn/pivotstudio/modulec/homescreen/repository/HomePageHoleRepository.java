package cn.pivotstudio.modulec.homescreen.repository;

import static com.example.libbase.constant.Constant.CONSTANT_STANDARD_LOAD_SIZE;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.constant.Constant;

import java.util.ArrayList;
import java.util.List;


import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulec.homescreen.model.HomepageHoleResponse;
import cn.pivotstudio.modulec.homescreen.model.MsgResponse;
import cn.pivotstudio.modulec.homescreen.network.HSRequestInterface;
import io.reactivex.Observable;

/**
 * @classname: HomePageHoleResponse
 * @description:
 * @date:2022/5/3 22:55
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class HomePageHoleRepository {
    public MutableLiveData<HomepageHoleResponse> pHomePageHoles;//数据类型网络请求结果
    public MutableLiveData<MsgResponse> pClickMsg;//通知类型网路请求结果
    public final MutableLiveData<String> failed;//网络请求失败结果
    /**
     * 初始化
     */
    public HomePageHoleRepository() {
        pHomePageHoles=new MutableLiveData<>();
        pClickMsg=new MutableLiveData<>();
        failed = new MutableLiveData<>();
    }
    /**
     * 获取正常状态树洞
     * @param mHolesSequenceCondition 是新更新还是新发布
     * @param mStartingLoadId 起始id
     */
    public void getHolesForNetwork(Boolean mHolesSequenceCondition,int mStartingLoadId){
            NetworkApi.createService(HSRequestInterface.class, 2).
                    homepageHoles(true, mHolesSequenceCondition, mStartingLoadId, CONSTANT_STANDARD_LOAD_SIZE).compose(NetworkApi.applySchedulers(new BaseObserver<List<HomepageHoleResponse.DataBean>>() {
                @Override
                public void onSuccess(List<HomepageHoleResponse.DataBean> requestedDataList) {
                    //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                    for(HomepageHoleResponse.DataBean item:requestedDataList){
                       item.setIs_last_reply(mHolesSequenceCondition);
                    }

                    if (mStartingLoadId != 0) {//上拉加载得到

                        //手动补充为完整response
                        HomepageHoleResponse lastRequestedData = pHomePageHoles.getValue();
                        lastRequestedData.getData().addAll(requestedDataList);
                        lastRequestedData.setModel("LOAD_MORE");
                        pHomePageHoles.setValue(lastRequestedData);
                    } else {//下拉刷新或者搜索得到


                        //手动补充为完整response
                        HomepageHoleResponse homepageHoleResponse = new HomepageHoleResponse();
                        homepageHoleResponse.setData(requestedDataList);
                        homepageHoleResponse.setModel("REFRESH");
                        pHomePageHoles.setValue(homepageHoleResponse);


                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                }
            }));
    }

    /**
     * 关键词搜索树洞
     * @param et 关键词内容
     * @param mStartingLoadId 起始id
     */
    public void searchHolesForNetwork(String et,Integer mStartingLoadId){
        NetworkApi.createService(HSRequestInterface.class, 2).
                searchHoles(et, mStartingLoadId, CONSTANT_STANDARD_LOAD_SIZE).compose(NetworkApi.applySchedulers(new BaseObserver<List<HomepageHoleResponse.DataBean>>() {
            @Override
            public void onSuccess(List<HomepageHoleResponse.DataBean> requestedDataList){
                 //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                for(HomepageHoleResponse.DataBean item:requestedDataList){
                    item.setIs_last_reply(false);
                }

                if (mStartingLoadId != 0) {//上拉加载得到
                    //手动补充为完整response
                    HomepageHoleResponse lastRequestedDataList = pHomePageHoles.getValue();
                    lastRequestedDataList.getData().addAll(requestedDataList);
                    lastRequestedDataList.setModel("SEARCH_LOAD_MORE");
                    pHomePageHoles.setValue(lastRequestedDataList);
                } else {//下拉刷新或者搜索得到

                    //手动补充为完整response
                    HomepageHoleResponse homepageHoleResponse = new HomepageHoleResponse();
                    homepageHoleResponse.setData(requestedDataList);
                    homepageHoleResponse.setModel("SEARCH_REFRESH");
                    pHomePageHoles.setValue(homepageHoleResponse);


                }
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 搜索单个树洞
     * @param et 树洞号
     */
    public void searchSingleHoleForNetwork(String et){
        NetworkApi.createService(HSRequestInterface.class, 2).
                searchSingleHole(Constant.BASE_URL +"holes/"+et).compose(NetworkApi.applySchedulers(new BaseObserver<HomepageHoleResponse.DataBean>() {
            @Override
            public void onSuccess(HomepageHoleResponse.DataBean requestedData) {
                //手动为期添加状态，判断是新更新还是新发布，用于数据绑定，显式在解析的时间后
                requestedData.setIs_last_reply(false);
                //封装为list
                List<HomepageHoleResponse.DataBean> requestDataList=new ArrayList<>();
                requestDataList.add(requestedData);

                //手动补充为完整response
                HomepageHoleResponse homepageHoleResponse=new HomepageHoleResponse();
                homepageHoleResponse.setData(requestDataList);
                homepageHoleResponse.setModel("SEARCH_HOLE");
                pHomePageHoles.setValue(homepageHoleResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 点赞
     * @param hole_id 树洞号
     * @param thumbup_num 网络请求成功前的点赞数量
     * @param is_thumbup 网络请求成功前是否被点赞
     * @param dataBean item的所有数据
     */
    public void thumbupForNetwork(int hole_id, int thumbup_num, boolean is_thumbup, HomepageHoleResponse.DataBean dataBean){
        Observable<MsgResponse> observable;
        if(!is_thumbup){
            observable=NetworkApi.createService(HSRequestInterface.class, 2).thumbups(Constant.BASE_URL +"thumbups/" + hole_id + "/-1");
        }else{
            observable=NetworkApi.createService(HSRequestInterface.class, 2).deleteThumbups(Constant.BASE_URL +"thumbups/" + hole_id + "/-1");
        }
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {
                if(is_thumbup){
                    dataBean.setThumbup_num(thumbup_num-1);
                }else{
                    dataBean.setThumbup_num(thumbup_num+1);
                }
                dataBean.setIs_thumbup(!is_thumbup);
                pClickMsg.setValue(msg);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 收藏
     * @param hole_id 树洞号
     * @param follow_num 网络请求成功前的收藏数量
     * @param is_follow 网络请求成功前是否被收藏
     * @param dataBean item的所有数据
     */
    public void followForNetwork(int hole_id, int follow_num, boolean is_follow, HomepageHoleResponse.DataBean dataBean){
        Observable<MsgResponse> observable;
        if(!is_follow){
            observable=NetworkApi.createService(HSRequestInterface.class, 2).follow(Constant.BASE_URL +"follows/" + hole_id);
        }else{
            observable=NetworkApi.createService(HSRequestInterface.class, 2).deleteFollow(Constant.BASE_URL +"follows/" + hole_id );
        }
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {

                if(is_follow){
                    dataBean.setFollow_num(follow_num-1);
                }else{
                    dataBean.setFollow_num(follow_num+1);
                }
                dataBean.setIs_follow(!is_follow);
                pClickMsg.setValue(msg);

            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 举报或删除
     * @param hole_id 树洞号
     * @param is_mine 是否是自己发布的树洞
     */
    public void moreActionForNetwork(int hole_id, boolean is_mine){
        Observable<MsgResponse> observable;
        if(is_mine){
            observable=NetworkApi.createService(HSRequestInterface.class, 2).deleteHole(String.valueOf(hole_id));
            observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
                @Override
                public void onSuccess(MsgResponse msg) {
                    pClickMsg.setValue(msg);
                }

                @Override
                public void onFailure(Throwable e) {
                    failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                }
            }));
        }else{
            ARouter.getInstance().build("/report/ReportActivity")
                    .withInt(Constant.HOLE_ID, hole_id)
                    .withInt(Constant.REPLY_LOCAL_ID,-1)
                    .withString(Constant.ALIAS,"洞主")
                    .navigation();
            //observable=NetworkApi.createService(HSRequestInterface.class, 2).report(Constant.BASE_URL +"reports?hole_id=" + hole_id + "&reply_local_id= -1");
        }

    }
}
