package cn.pivotstudio.modulep.hole.repository;

import static cn.pivotstudio.moduleb.libbase.constant.Constant.CONSTANT_STANDARD_LOAD_SIZE;
import static cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.context;
import static cn.pivotstudio.moduleb.libbase.util.data.GetUrlUtil.getURLEncoderString;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication;

import java.util.LinkedList;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.moduleb.database.bean.Hole;
import cn.pivotstudio.moduleb.database.repository.CustomDisposable;
import cn.pivotstudio.modulep.hole.model.MsgResponse;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import cn.pivotstudio.modulep.hole.model.HoleResponse;
import cn.pivotstudio.modulep.hole.network.HRequestInterface;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * @classname:HoleRepository
 * @description:
 * @date:2022/5/8 13:32
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class HoleRepository {
    public MutableLiveData<HoleResponse> pHole;
    public MutableLiveData<ReplyListResponse> pReplyList;
    public MutableLiveData<ReplyListResponse.ReplyResponse> pInputText;
    public MutableLiveData<LinkedList<Integer>> pUsedEmojiList;

    public MutableLiveData<MsgResponse> pClickMsg;
    public MutableLiveData<MsgResponse> pSendReply;
    public final MutableLiveData<String> failed;//网络请求失败结果
    private Hole hole;

    /**
     * 构造函数
     */
    public HoleRepository() {
        pHole = new MutableLiveData<>();
        pReplyList = new MutableLiveData<>();
        pInputText = new MutableLiveData<>();
        pUsedEmojiList = new MutableLiveData<>();
        pClickMsg = new MutableLiveData<>();
        pSendReply = new MutableLiveData<>();
        failed = new MutableLiveData<>();
    }

    public void getInputTextForLocalDB(Integer hole_id) {
        Flowable<Hole> flowable = BaseApplication.getDB().holeDao().findById(hole_id);
        CustomDisposable.addDisposable(flowable, holeEt -> {
            if (holeEt != null) {
                hole = holeEt;
                ReplyListResponse.ReplyResponse requestedData = new ReplyListResponse.ReplyResponse();
                requestedData.setContent(holeEt.getContent());
                requestedData.setAlias(holeEt.getAlias());
                requestedData.setIs_mine(holeEt.getIs_mine());
                requestedData.setReply_local_id(holeEt.getReply_local_id());
                pInputText.postValue(requestedData);
            }
        });
    }

    public void getUsedEmojiForLocalDB() {
        MMKVUtil mmkvUtil = MMKVUtil.getMMKV(context);
        LinkedList<Integer> list = (LinkedList<Integer>) mmkvUtil.getArray(Constant.UsedEmoji, 0);
        if (list == null) {
            list = new LinkedList<Integer>();
            mmkvUtil.put(Constant.UsedEmoji, list);
            // pUsedEmojiList.postValue(new LinkedList<Integer>());
        } else {
            pUsedEmojiList.postValue(list);
        }
    }

    public void saveInputTextForLocalDB(Integer hole_id, String text, String alias, Boolean is_mine, Integer reply_local_id) {
        Hole hole = new Hole(hole_id, text, alias, is_mine, reply_local_id);
        Completable insert = BaseApplication.getDB().holeDao().insert(hole);
        CustomDisposable.addDisposable(insert, () -> Log.d("数据库", "saveInputTextForLocalDB:数据保存成功"));

    }

    public void updateInputTextForLocalDB(Integer hole_id, String text, String alias, Boolean is_mine, Integer reply_local_id) {
        Hole hole = new Hole(hole_id, text, alias, is_mine, reply_local_id);
        Completable insert = BaseApplication.getDB().holeDao().update(hole);
        CustomDisposable.addDisposable(insert, () -> Log.d("数据库", "saveInputTextForLocalDB:数据更新成功"));

    }

    public void deleteInputTextForLocalDB() {
        Completable insert = BaseApplication.getDB().holeDao().delete(hole);
        CustomDisposable.addDisposable(insert, () -> Log.d("数据库", "saveInputTextForLocalDB:数据删除成功"));
    }

    public void getListForNetwork(int hole_id, Boolean is_descend, Integer start_id, Boolean isOwner) {
        Observable<ReplyListResponse> hotReplyObservable = NetworkApi.createService(HRequestInterface.class, 2).
                getHotReply(hole_id, 0, 3);

        Observable<ReplyListResponse> repliesObservable;
        if (!isOwner) {
            repliesObservable = NetworkApi.createService(HRequestInterface.class, 2)
                    .getReplies(Constant.BASE_URL + "replies?hole_id=" + hole_id + "&is_descend=" + is_descend + "&start_id=" + start_id + "&list_size=" + CONSTANT_STANDARD_LOAD_SIZE);

        } else {
            repliesObservable = NetworkApi.createService(HRequestInterface.class, 2)
                    .getOwnerReply(hole_id, start_id, CONSTANT_STANDARD_LOAD_SIZE, is_descend);
        }

        Observable<HoleResponse> holeObservable = NetworkApi.createService(HRequestInterface.class, 2).
                getHole(Constant.BASE_URL + "holes/" + hole_id);

        Observable<ReplyListResponse> result =
                Observable.zip(hotReplyObservable.subscribeOn(Schedulers.io()), repliesObservable.subscribeOn(Schedulers
                        .io()), holeObservable.subscribeOn(Schedulers.io()), new Function3<ReplyListResponse, ReplyListResponse, HoleResponse, ReplyListResponse>() {
                    @Override
                    public ReplyListResponse apply(ReplyListResponse hotReplyResponse, ReplyListResponse repliesResponse, HoleResponse holeResponse) {

                        pHole.postValue(holeResponse);
                        //依据是否是只看洞主，加载热评内容
                        if (isOwner || start_id != 0) {
                            //设置热评标识
                            for (ReplyListResponse.ReplyResponse requestedData : repliesResponse.getMsg()) {
                                requestedData.setIs_hot(false);
                            }

                            return repliesResponse;
                        } else {
                            //设置热评标识
                            for (ReplyListResponse.ReplyResponse requestedData : repliesResponse.getMsg()) {
                                requestedData.setIs_hot(false);
                            }
                            for (ReplyListResponse.ReplyResponse requestedData : hotReplyResponse.getMsg()) {
                                requestedData.setIs_hot(true);
                            }

                            hotReplyResponse.getMsg().addAll(repliesResponse.getMsg());
                            return hotReplyResponse;
                        }
                        //单独将树洞的信息发送出去

                    }
                });
        result.compose(NetworkApi.applySchedulers(new BaseObserver<ReplyListResponse>() {
            @Override
            public void onSuccess(ReplyListResponse requestedDataList) {

                // if(requestedDataList.getMsg().size()==0){//加载数据为空时
                if (start_id != 0) {//说明初始数据就是空的
                    ReplyListResponse lastRequestedDataList = pReplyList.getValue();
                    lastRequestedDataList.getMsg().addAll(requestedDataList.getMsg());
                    if (requestedDataList.getMsg().size() == 0) {//加载数据为空时
                        lastRequestedDataList.setModel("LOAD_ALL");
                    } else {
                        lastRequestedDataList.setModel("LOAD_MORE");
                    }
                    pReplyList.setValue(lastRequestedDataList);
                } else {//下拉刷新得到的数据是空的
                    if (requestedDataList.getMsg().size() == 0) {//加载数据为空时
                        requestedDataList.setModel("NO_REPLY");
                    } else {
                        requestedDataList.setModel("REFRESH");
                    }
                    pReplyList.setValue(requestedDataList);
                }
            }

            @Override
            public void onFailure(Throwable e) {

                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));

    }

    /**
     * 点赞单个评论
     *
     * @param hole_id     树洞号
     * @param thumbup_num 网络请求成功前的点赞数量
     * @param is_thumbup  网络请求成功前是否被点赞
     * @param dataBean    item的所有数据
     */
    public void thumbupReplyForNetwork(int hole_id, int reply_local_id, int thumbup_num, boolean is_thumbup, ReplyListResponse.ReplyResponse dataBean) {
        Observable<MsgResponse> observable;
        if (!is_thumbup) {
            observable = NetworkApi.createService(HRequestInterface.class, 2)
                    .thumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/" + reply_local_id);
        } else {
            observable = NetworkApi.createService(HRequestInterface.class, 2)
                    .deleteThumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/" + reply_local_id);
        }
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {
                if (is_thumbup) {
                    dataBean.setThumbup_num(thumbup_num - 1);
                } else {
                    dataBean.setThumbup_num(thumbup_num + 1);
                }
                dataBean.setIs_thumbup(!is_thumbup);
                Boolean isHot = dataBean.getIs_hot();//获取当前data是否是热门的
                //从总数据中遍历
                int traversal = 0;
                for (ReplyListResponse.ReplyResponse reply : pReplyList.getValue().getMsg()) {
                    //如果当前数据的id和点赞的id相同并且不是他自己，另一个需要同步的数据的ishot一定与此相反
                    if (reply.getReply_local_id().equals(reply_local_id) && reply.getIs_hot().equals(!isHot)) {
                        if (is_thumbup) {
                            reply.setThumbup_num(thumbup_num - 1);
                        } else {
                            reply.setThumbup_num(thumbup_num + 1);
                        }
                        reply.setIs_thumbup(!is_thumbup);
                        break;//只需要同步一种数据
                    }
                    //如果被点赞的是热评序列，就要一直从数据中查找到最后一项，直到查找到需要的值
                    //如果被点赞的不是热评序列，查询完热评后仍然没有所要的结果就说明点赞的不是热评，不需要继续查询了
                    if ((traversal > 2) && (!isHot)) {
                        break;
                    }
                    traversal++;
                }

                pClickMsg.setValue(msg);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));
    }

    public void thumbupForNetwork(int hole_id, int thumbup_num, boolean is_thumbup, HoleResponse dataBean) {
        Observable<MsgResponse> observable;
        if (!is_thumbup) {
            observable = NetworkApi.createService(HRequestInterface.class, 2).thumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/-1");
        } else {
            observable = NetworkApi.createService(HRequestInterface.class, 2).deleteThumbups(Constant.BASE_URL + "thumbups/" + hole_id + "/-1");
        }
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {
                if (is_thumbup) {
                    dataBean.setThumbup_num(thumbup_num - 1);
                } else {
                    dataBean.setThumbup_num(thumbup_num + 1);
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
     *
     * @param hole_id    树洞号
     * @param follow_num 网络请求成功前的收藏数量
     * @param is_follow  网络请求成功前是否被收藏
     * @param dataBean   item的所有数据
     */
    public void followForNetwork(int hole_id, int follow_num, boolean is_follow, HoleResponse dataBean) {
        Observable<MsgResponse> observable;
        if (!is_follow) {
            observable = NetworkApi.createService(HRequestInterface.class, 2).follow(Constant.BASE_URL + "follows/" + hole_id);
        } else {
            observable = NetworkApi.createService(HRequestInterface.class, 2).deleteFollow(Constant.BASE_URL + "follows/" + hole_id);
        }
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {

                if (is_follow) {
                    dataBean.setFollow_num(follow_num - 1);
                } else {
                    dataBean.setFollow_num(follow_num + 1);
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
     *
     * @param hole_id 树洞号
     * @param is_mine 是否是自己发布的树洞
     */
    public void moreActionForNetwork(int hole_id, Boolean is_mine, int reply_local_id, String alias) {
        Observable<MsgResponse> observable;
        if (is_mine) {
            if (reply_local_id == -1) {
                observable = NetworkApi.createService(HRequestInterface.class, 2).deleteHole(String.valueOf(hole_id));
            } else {
                observable = NetworkApi.createService(HRequestInterface.class, 2).deleteReply(Constant.BASE_URL + "replies/" + hole_id + "/" + reply_local_id);
            }
            observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
                @Override
                public void onSuccess(MsgResponse msg) {
                    msg.setModel(reply_local_id == -1 ? "DELETE_HOLE" : "DELETE_REPLY");
                    pClickMsg.setValue(msg);
                }

                @Override
                public void onFailure(Throwable e) {
                    failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                }
            }));
        } else {
            ARouter.getInstance().build("/report/ReportActivity")
                    .withInt(Constant.HOLE_ID, hole_id)
                    .withInt(Constant.REPLY_LOCAL_ID, reply_local_id)
                    .withString(Constant.ALIAS, alias)
                    .navigation();
        }

    }

    public void sendReplyForNetwork(String content, Integer hole_id, Integer user_id) {
        NetworkApi.createService(HRequestInterface.class, 2)
                .sendReply(Constant.BASE_URL + "replies?hole_id=" + hole_id + "&content=" + getURLEncoderString(content) + "&wanted_local_reply_id=" + user_id)
                .compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
                    @Override
                    public void onSuccess(MsgResponse msg) {
                        pSendReply.setValue(msg);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                    }
                }));
    }
}
