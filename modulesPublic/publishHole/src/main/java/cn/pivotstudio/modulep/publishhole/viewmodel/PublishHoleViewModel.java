package cn.pivotstudio.modulep.publishhole.viewmodel;

import androidx.lifecycle.MutableLiveData;

import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel;

import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestListsResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse;
import cn.pivotstudio.modulep.publishhole.model.MsgResponse;
import cn.pivotstudio.modulep.publishhole.repository.PublishHoleRepository;

/**
 * @classname:PublishHoleViewModel
 * @description:
 * @date:2022/5/6 12:38
 * @version:1.0
 * @author:
 */
public class PublishHoleViewModel extends BaseViewModel {
    public MutableLiveData<ForestListsResponse> pTypeForestList;//所有小树林
    public MutableLiveData<DetailTypeForestResponse> pJoinedForest;//加入的小树林
    public MutableLiveData<ForestTypeResponse> pForestType;//小树林类型
    public MutableLiveData<MsgResponse> pOnClickMsg;//点击事件成功的网络请求结果

    public MutableLiveData<String> pForestName;//选择的小树林的名字
    private Integer mForestId;//选中的小树林的id

    private final PublishHoleRepository mPublishHoleRepository;

    /**
     * 构造函数
     */
    public PublishHoleViewModel() {
        mPublishHoleRepository = new PublishHoleRepository();
        pForestName = new MutableLiveData<>();
        pTypeForestList = mPublishHoleRepository.pTypeForestList;
        pForestType = mPublishHoleRepository.pForestType;
        pJoinedForest = mPublishHoleRepository.pJoinedForest;
        pOnClickMsg = mPublishHoleRepository.pClickMsg;
        failed = mPublishHoleRepository.failed;
    }

    public Integer getForestId() {
        if (mForestId == null) mForestId = 0;
        return mForestId;
    }

    public void setForestId(Integer mForestId) {
        this.mForestId = mForestId;
    }

    /**
     * 获取加入的小树林
     */
    public void getJoinedForests() {
        mPublishHoleRepository.getJoinedForestForNetwork();
    }

    /**
     * 获取小树林类型
     */
    public void getForestType() {
        mPublishHoleRepository.getForestTypeForNetwork();
    }

    /**
     * 获取某一类型的所有小树林
     *
     * @param forest_type 小树林类型
     * @param location    对应总列表的位置
     */
    public void getTypeForest(String forest_type, int location) {
        mPublishHoleRepository.getTypeForestForNetwork(forest_type, location);
    }

    /**
     * 获取热门小树林
     */
    public void getHotForest() {
        mPublishHoleRepository.getHotForestForNetwork();
    }

    /**
     * 发送树洞
     *
     * @param content 树洞内容
     */
    public void postHoleRequest(String content) {
        mPublishHoleRepository.publishHole(content, getForestId());
    }
}
