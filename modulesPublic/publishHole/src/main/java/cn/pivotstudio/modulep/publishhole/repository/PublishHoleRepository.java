package cn.pivotstudio.modulep.publishhole.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.util.data.GetUrlUtil;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;

import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestListsResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse;
import cn.pivotstudio.modulep.publishhole.model.MsgResponse;
import cn.pivotstudio.modulep.publishhole.network.PHRequestInterface;

/**
 * @classname:PublishHoleRepository
 * @description:
 * @date:2022/5/5 22:52
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class PublishHoleRepository {
    public MutableLiveData<ForestListsResponse> pTypeForestList;
    public MutableLiveData<DetailTypeForestResponse> pJoinedForest;
    public MutableLiveData<ForestTypeResponse> pForestType;
    public MutableLiveData<MsgResponse> pClickMsg;
    public MutableLiveData<String>failed;
    private final static Integer list_size=20;

    public PublishHoleRepository() {
        pTypeForestList = new MutableLiveData<>();
        pJoinedForest=new MutableLiveData<>();
        pForestType =new MutableLiveData<>();
        pClickMsg=new MutableLiveData<>();
        failed = new MutableLiveData<>();
    }

    /**
     * 获取热门小树林
     */
    public void getHotForestForNetwork(){
        NetworkApi.createService(PHRequestInterface.class,2)
                .getHotForest(0,list_size).compose(NetworkApi.applySchedulers(new BaseObserver<DetailTypeForestResponse>() {
            @Override
            public void onSuccess(DetailTypeForestResponse requestedData) {
                ForestListsResponse forestListsResponse=pTypeForestList.getValue();

                //放在总列表首位
                DetailTypeForestResponse detailTypeForestResponse=forestListsResponse.getLists().get(0);
                detailTypeForestResponse.setForests(requestedData.getForests());
                forestListsResponse.setItemNumber(0);

                pTypeForestList.postValue(forestListsResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 获取指定类型的小树林
     * @param forest_type 小树林类型
     * @param location 在总列表中的位置
     */
    public void getTypeForestForNetwork(String forest_type, int location){
        NetworkApi.createService(PHRequestInterface.class,2)
                .getDetailTypeForest(forest_type,0,list_size,false).compose(NetworkApi.applySchedulers(new BaseObserver<DetailTypeForestResponse>() {
            @Override
            public void onSuccess(DetailTypeForestResponse list) {
               // DetailTypeForestResponse detailTypeForestResponse=new DetailTypeForestResponse();
                //detailTypeForestResponse.setList(list);

                //放在列表指定位置
                ForestListsResponse forestListsResponse=pTypeForestList.getValue();
                DetailTypeForestResponse detailTypeForestResponse=forestListsResponse.getLists().get(location);
                detailTypeForestResponse.setForests(list.getForests());
                forestListsResponse.setItemNumber(location);

                pTypeForestList.postValue(forestListsResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 获取加入的小树林
     */
    public void getJoinedForestForNetwork(){
        NetworkApi.createService(PHRequestInterface.class,2)
                .joined(list_size,0).compose(NetworkApi.applySchedulers(new BaseObserver<DetailTypeForestResponse>() {
            @Override
            public void onSuccess(DetailTypeForestResponse detailTypeForestResponse) {
                pJoinedForest.postValue(detailTypeForestResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    /**
     * 获取小树林类型
     */
    public void getForestTypeForNetwork(){
        NetworkApi.createService(PHRequestInterface.class,2)
                .getType(0,list_size).compose(NetworkApi.applySchedulers(new BaseObserver<ForestTypeResponse>() {
            @Override
            public void onSuccess(ForestTypeResponse forestTypeResponse) {
                //加载完类型，为总列表初始化相同长度的数据
                if(pTypeForestList.getValue()==null) {
                    List<DetailTypeForestResponse> lists = new ArrayList<>();
                    for (int i = 0; i < forestTypeResponse.getTypes().size() + 1; i++) {
                        lists.add(new DetailTypeForestResponse());
                    }
                    ForestListsResponse forestListsResponse=new ForestListsResponse();
                    forestListsResponse.setItemNumber(-1);//只有当数量大于等于0时才更新数据
                    forestListsResponse.setLists(lists);
                    pTypeForestList.setValue(forestListsResponse);
                }


                pForestType.postValue(forestTypeResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    public void publishHole(String content,Integer forest_name){
        NetworkApi.createService(PHRequestInterface.class,2)
                .publishHole(Constant.BASE_URL +"holes?content="+ GetUrlUtil.getURLEncoderString(content)+ "&forest_id=" +forest_name)
                .compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {
                //返回结果
                pClickMsg.postValue(msg);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }


}
