package cn.pivotstudio.modulep.hole.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel;

import java.util.LinkedList;

import cn.pivotstudio.modulep.hole.BuildConfig;
import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.custom_view.dialog.DeleteDialog;
import cn.pivotstudio.modulep.hole.model.HoleResponse;
import cn.pivotstudio.modulep.hole.model.MsgResponse;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import cn.pivotstudio.modulep.hole.repository.HoleRepository;

/**
 * @classname:HoleViewModel
 * @description:
 * @date:2022/5/8 15:57
 * @version:1.0
 * @author:
 */
public class HoleViewModel extends BaseViewModel {
    //网路数据
    public MutableLiveData<HoleResponse> pHole;
    public MutableLiveData<ReplyListResponse> pReplyList;
    public MutableLiveData<ReplyListResponse.ReplyResponse> pInputText;
    public MutableLiveData<MsgResponse> pClickMsg;
    public MutableLiveData<MsgResponse> pSendReply;
    public MutableLiveData<LinkedList<Integer>> pUsedEmojiList;
    //
    private Integer hole_id;
    private Integer start_id;

    //下面三项非网络请求所得，但是变化需要及时反馈在ui上
    private ObservableField<ReplyListResponse.ReplyResponse> answered;
    private ObservableField<Boolean> is_descend;
    private ObservableField<Boolean> is_owner;
    private ObservableField<Boolean> is_emoji;

    //进行数据请求的地方
    private final HoleRepository mHoleRepository;

    /**
     * 构造函数
     */
    public HoleViewModel() {
        mHoleRepository = new HoleRepository();
        pHole = mHoleRepository.pHole;
        pReplyList = mHoleRepository.pReplyList;
        pInputText = mHoleRepository.pInputText;
        pClickMsg = mHoleRepository.pClickMsg;
        pSendReply = mHoleRepository.pSendReply;
        pUsedEmojiList = mHoleRepository.pUsedEmojiList;

        failed = mHoleRepository.failed;

        answered = new ObservableField<>();
        is_owner = new ObservableField<>();
        is_descend = new ObservableField<>();
        is_emoji = new ObservableField<>();
    }

    public void getUsedEmojiList() {
        mHoleRepository.getUsedEmojiForLocalDB();
    }

    public ObservableField<ReplyListResponse.ReplyResponse> getAnswered() {
        if (answered.get() == null) {
            ReplyListResponse.ReplyResponse base = new ReplyListResponse.ReplyResponse();
            base.setAlias("洞主");
            base.setIs_mine(false);
            base.setReply_local_id(-1);
            answered.set(base);
        }
        return answered;
    }

    public void setAnswered(ObservableField<ReplyListResponse.ReplyResponse> answered) {
        this.answered = answered;
    }

    public ObservableField<Boolean> getIs_owner() {
        if (is_owner.get() == null) is_owner.set(false);
        return is_owner;
    }

    public void setIs_owner(ObservableField<Boolean> is_owner) {
        this.is_owner = is_owner;
    }

    public ObservableField<Boolean> getIs_descend() {
        if (is_descend.get() == null) is_descend.set(false);
        return is_descend;
    }

    public void setIs_descend(ObservableField<Boolean> is_descend) {
        this.is_descend = is_descend;
    }

    public ObservableField<Boolean> getIs_emoji() {
        if (is_emoji.get() == null) is_emoji.set(false);
        return is_emoji;
    }

    public void setIs_emoji(ObservableField<Boolean> is_emoji) {
        this.is_emoji = is_emoji;
    }

    public Integer getHole_id() {
        return hole_id;
    }

    public void setHole_id(Integer hole_id) {
        this.hole_id = hole_id;
    }

    public Integer getStart_id() {
        if (start_id == null) start_id = 0;
        return start_id;
    }

    public void setStart_id(Integer start_id) {
        this.start_id = start_id;
    }

    public void getInputText() {
        mHoleRepository.getInputTextForLocalDB(hole_id);
    }

    public void saveInputText(String text) {
        ReplyListResponse.ReplyResponse answered = getAnswered().get();
        mHoleRepository.saveInputTextForLocalDB(hole_id, text, answered.getAlias(), answered.getIs_mine(), answered.getReply_local_id());
    }

    public void updateInputText(String text) {
        ReplyListResponse.ReplyResponse answered = getAnswered().get();
        mHoleRepository.updateInputTextForLocalDB(hole_id, text, answered.getAlias(), answered.getIs_mine(), answered.getReply_local_id());
    }

    public void deleteInputText() {
        mHoleRepository.deleteInputTextForLocalDB();
    }

    public void sendReply(String content) {
        if (!BuildConfig.isRelease) {//供测试阶段使用
            setHole_id(79419);
        }
        mHoleRepository.sendReplyForNetwork(content, getHole_id(), getAnswered().get().getReply_local_id());
    }

    ;

    public void getListData(Boolean ifLoadMore) {
        if (!BuildConfig.isRelease) {//供测试阶段使用
            setHole_id(79419);
        }
        mHoleRepository.getListForNetwork(getHole_id(), getIs_descend().get(), (ifLoadMore ? (getStart_id() + 20) : 0), getIs_owner().get());
    }

    /**
     * 涉及到网络请求相关的点击事件
     *
     * @param v        被点击的view
     * @param dataBean item的数据
     */
    public void itemClick(View v, HoleResponse dataBean) {
        Integer holeId = dataBean.getHole_id();
        int id = v.getId();
        if (id == R.id.cl_hole_thumbup) {
            Boolean isThunbup = dataBean.getIs_thumbup();
            Integer thumbupNum = dataBean.getThumbup_num();
            mHoleRepository.thumbupForNetwork(holeId, thumbupNum, isThunbup, dataBean);
        } else if (id == R.id.cl_hole_reply) {
            ReplyListResponse.ReplyResponse as = new ReplyListResponse.ReplyResponse();
            as.setReply_local_id(-1);
            as.setIs_mine(false);
            as.setAlias("洞主");
            answered.set(as);
        } else if (id == R.id.cl_hole_follow) {
            Boolean isFollow = dataBean.getIs_follow();
            Integer followNum = dataBean.getFollow_num();
            mHoleRepository.followForNetwork(holeId, followNum, isFollow, dataBean);
        } else if (id == R.id.btn_hole_jumptodetailforest) {


        } else if (id == R.id.cl_hole_morelist) {
            Boolean isMine = dataBean.getIs_mine();
            if (isMine) {
                DeleteDialog dialog = new DeleteDialog(v.getContext());
                dialog.show();
                dialog.setOptionsListener(v1 -> mHoleRepository.moreActionForNetwork(holeId, isMine, -1, "洞主"));
            } else {
                mHoleRepository.moreActionForNetwork(holeId, isMine, -1, "洞主");
            }
            v.setVisibility(View.GONE);
        } else if (id == R.id.cl_hole_changesequence) {
            ObservableField<Boolean> observableField = getIs_descend();
            observableField.set(!observableField.get());
            getListData(false);
        } else if (id == R.id.tv_hole_content) {
            ReplyListResponse.ReplyResponse as = new ReplyListResponse.ReplyResponse();
            as.setReply_local_id(-1);
            as.setIs_mine(false);
            as.setAlias("洞主");
            answered.set(as);
        }
    }

    public void replyItemClick(View v, ReplyListResponse.ReplyResponse dataBean) {
        int id = v.getId();
        int reply_local_id = dataBean.getReply_local_id();
        int hole_id = dataBean.getHole_id();
        if (id == R.id.cl_reply_thumbup) {
            Boolean isThunbup = dataBean.getIs_thumbup();
            Integer thumbupNum = dataBean.getThumbup_num();
            mHoleRepository.thumbupReplyForNetwork(hole_id, reply_local_id, thumbupNum, isThunbup, dataBean);
        } else if (id == R.id.cl_reply_morelist) {
            Boolean isMine = dataBean.getIs_mine();
            if (isMine) {
                DeleteDialog dialog = new DeleteDialog(v.getContext());
                dialog.show();
                dialog.setOptionsListener(v1 -> mHoleRepository.moreActionForNetwork(hole_id, isMine, reply_local_id, dataBean.getAlias()));
            } else {
                mHoleRepository.moreActionForNetwork(hole_id, isMine, reply_local_id, dataBean.getAlias());
            }
            v.setVisibility(View.GONE);
        } else if ((id == R.id.cl_reply) || (id == R.id.tv_reply_content)) {
            //setReply_local_id(dataBean.getReply_local_id());
            answered.set(dataBean);
        }
    }

}
