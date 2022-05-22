package cn.pivotstudio.modulep.hole.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.constant.Constant;
import com.example.libbase.base.ui.activity.BaseActivity;
import com.example.libbase.base.model.HoleReturnInfo;
import com.example.libbase.constant.ResultCodeConstant;
import com.example.libbase.util.ui.EditTextUtil;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smart.refresh.footer.ClassicsFooter;

import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.modulep.hole.BuildConfig;
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshHeader;

import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.databinding.ActivityHoleBinding;
import cn.pivotstudio.modulep.hole.model.HoleResponse;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import cn.pivotstudio.modulep.hole.ui.adapter.EmojiRecyclerViewAdapter;
import cn.pivotstudio.modulep.hole.ui.adapter.ReplyListRecyclerViewAdapter;
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel;

/**
 * @classname:HoleActivity
 * @description:
 * @date:2022/5/8 13:24
 * @version:1.0
 * @author:
 */

@Route(path="/hole/HoleActivity")
public class HoleActivity extends BaseActivity {
    @Autowired(name= Constant.HOLE_ID)
    int hole_id;
    @Autowired(name=Constant.IF_OPEN_KEYBOARD)
    boolean ifOpenKeyboard;

    private ActivityHoleBinding mBinding;
    private HoleViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);//初始化@Autowired


        initView();
        initRefresh();
        initObserver();
        initListener();
    }
    private void initView(){
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hole);

        mViewModel=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(HoleViewModel.class);
        mBinding.setViewmodel(mViewModel);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBinding.rvEmoji.setVisibility(View.GONE);


        if(ifOpenKeyboard)openKeyBoard(mBinding.etReplyPost);

        mViewModel.setHole_id(hole_id);

        mViewModel.getListData(false);
        mViewModel.getInputText();
        mViewModel.getUsedEmojiList();

        EditTextUtil.ButtonReaction(mBinding.etReplyPost,mBinding.btnCommentSendmsg);

        mBinding.rvCommentlist.setLayoutManager(new LinearLayoutManager(context));
        mBinding.rvCommentlist.setAdapter(new ReplyListRecyclerViewAdapter(this,mViewModel));
        mBinding.rvCommentlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                ConstraintLayout lastMoreListCl=((ReplyListRecyclerViewAdapter)mBinding.rvCommentlist.getAdapter()).lastMoreListCl;
                if(lastMoreListCl!=null)lastMoreListCl.setVisibility(View.GONE);
                lastMoreListCl=null;
            }
        });

        GridLayoutManager layoutManager= new GridLayoutManager(
                this,
                6,
                RecyclerView.VERTICAL,
                false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if((position==0)||(position==7)){
                    return 6;
                }else{
                    return 1;
                }
            }
        });
        mBinding.rvEmoji.setLayoutManager(layoutManager);
        mBinding.rvEmoji.setAdapter(new EmojiRecyclerViewAdapter(this,mBinding,mViewModel));

        mBinding.includeHoletitlebar.tvTitlebargreenTitle.setText("#"+hole_id);
        mBinding.includeHoletitlebar.titlebarAVLoadingIndicatorView.hide();
        mBinding.includeHoletitlebar.titlebarAVLoadingIndicatorView.setVisibility(View.GONE);
        mBinding.includeHoletitlebar.clTitlebargreenBack.setOnClickListener(this::onClick);
    }
    private void initRefresh(){
        mBinding.srlCommmentlistLoadmore.setRefreshHeader(new StandardRefreshHeader(this));//设置自定义刷新头
        mBinding.srlCommmentlistLoadmore.setRefreshFooter(new ClassicsFooter(this));//设置自定义刷新底

        mBinding.srlCommmentlistLoadmore.setOnRefreshListener(refreshlayout -> {//下拉刷新触发
            mViewModel.setStart_id(0);
            mViewModel.getListData(false);
            mBinding.rvCommentlist.setOnTouchListener((v, event) -> true); //加载时静止滑动
        });

        mBinding.srlCommmentlistLoadmore.setOnLoadMoreListener(refreshlayout -> {//上拉加载触发
            mViewModel.getListData(true);
            mBinding.rvCommentlist.setOnTouchListener((v, event) -> true);
        }); 
    }
    private void initObserver(){

        mViewModel.pHole.observe(this,holeResponse -> {});

        mViewModel.pReplyList.observe(this,replyListResponse -> {
            switch(replyListResponse.getModel()){
                case "REFRESH":
                    finishRefresh();
                    break;
                case "LOAD_MORE":
                    finishLoadMore();
                    break;
                case "LOAD_ALL":
                    showMsg("加载到底辣");
                    break;
                case "NO_REPLY":
                    break;
            }
            finishRefreshAnim();
        });

        mViewModel.pInputText.observe(this,replyResponse->{
            mBinding.etReplyPost.setText(replyResponse.getContent());
            mViewModel.getAnswered().set(replyResponse);
        });
        mViewModel.pSendReply.observe((HoleActivity)context,msgResponse->{
            mBinding.btnCommentSendmsg.setClickable(true);//发送成功运行点击，以免重复发送
            showMsg(msgResponse.getMsg());
            mBinding.etReplyPost.setText("");//将输入框内容清空

            //将表情包栏关掉
            mBinding.rvEmoji.setVisibility(View.GONE);
            // setVisibility(View.GONE);
            ObservableField<Boolean> is_opened=mViewModel.getIs_emoji();
            is_opened.set(false);

            mViewModel.getListData(false);//重新刷新数据
        });

        mViewModel.pClickMsg.observe(this,msgResponse -> {
            showMsg(msgResponse.getMsg());
            switch(msgResponse.getModel()){
                case "DELETE_HOLE":
                    finish();
                    break;
                case "DELETE_REPLY":
                    mViewModel.getListData(false);
                    break;
            }
        });

        mViewModel.failed.observe(this, s->{
            mBinding.btnCommentSendmsg.setClickable(true);
            showMsg(s);
        });
    }
    private void initListener(){
        mBinding.etReplyPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >=500) {
                    Toast.makeText(HoleActivity.this,"评论不得超过500个字噢~",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     *下拉刷新或搜索的数据更新
     */
    private void finishRefresh(){
        mViewModel.setStart_id(0);
    }
    /**
     * 上拉加载的数据更新
     */
    private void finishLoadMore(){
        mViewModel.setStart_id(mViewModel.getStart_id()+20);
    }
    /**
     * 刷新结束后动画的流程
     */
    private void finishRefreshAnim(){
        mBinding.srlCommmentlistLoadmore.finishRefresh();//结束下拉刷新动画
        mBinding.srlCommmentlistLoadmore.finishLoadMore();//结束上拉加载动画
        mBinding.rvCommentlist.setOnTouchListener((v, event) -> false);//加载结束后允许滑动
    }

    /**
     * 处理activity点击事件
     * @param view
     */
    public void onClick(View view) {
        if(view.getId() ==R.id.btn_comment_sendmsg){
            mBinding.btnCommentSendmsg.setClickable(false);//避免重复发送
            mViewModel.sendReply(mBinding.etReplyPost.getText().toString());
            closeKeyBoard();
        }else if(view.getId() ==R.id.btn_replylist_owner){
            ObservableField<Boolean> observableField=mViewModel.getIs_owner();
            observableField.set(!observableField.get());
            mViewModel.getListData(false);
        }else if (view.getId() == R.id.cl_titlebargreen_back) {
            if (BuildConfig.isRelease) {
                saveData();//保存数据
                finish();//关闭活动
                closeKeyBoard();//关闭键盘
            } else {
                showMsg("当前处于模块测试阶段");
            }
        }else if(view.getId()==R.id.iv_openemoji) {
            ObservableField<Boolean> is_opened=mViewModel.getIs_emoji();
            if(!is_opened.get()){
                closeKeyBoard();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                ((EmojiRecyclerViewAdapter)(mBinding.rvEmoji.getAdapter())).refreshData();
                mBinding.rvEmoji.setVisibility(View.VISIBLE);
                is_opened.set(true);
            }else{
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mBinding.rvEmoji.setVisibility(View.GONE);
               // setVisibility(View.GONE);
                is_opened.set(false);
            }
        }else if(view.getId()==R.id.et_reply_post){
            ObservableField<Boolean> is_opened=mViewModel.getIs_emoji();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mBinding.rvEmoji.setVisibility(View.VISIBLE);
            openKeyBoard(mBinding.etReplyPost);
            is_opened.set(false);
        }
    }

    /**
     * 监听手机回退键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            saveData();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 保存输入内容以及选择回复的人
     */
    private void saveData(){
        Intent intent = new Intent();
        HoleReturnInfo holeReturnInfo = new HoleReturnInfo();
        HoleResponse holeResponse=mViewModel.pHole.getValue();
        if(holeResponse!=null) {
            holeReturnInfo.setIs_thumbup(holeResponse.getIs_thumbup());
            holeReturnInfo.setIs_follow(holeResponse.getIs_follow());
            holeReturnInfo.setIs_reply(holeResponse.getIs_reply());
            holeReturnInfo.setThumbup_num(holeResponse.getThumbup_num());
            holeReturnInfo.setReply_num(holeResponse.getReply_num());
            holeReturnInfo.setFollow_num(holeResponse.getFollow_num());
        }
        intent.putExtra(Constant.HOLE_RETURN_INFO,holeReturnInfo);
        setResult(ResultCodeConstant.Hole,intent);

        MMKVUtil mmkvUtil=MMKVUtil.getMMKVUtils(context);
        mmkvUtil.setArray(Constant.UsedEmoji,mViewModel.pUsedEmojiList.getValue());

        String inputText=mBinding.etReplyPost.getText().toString();
        ReplyListResponse.ReplyResponse lastInputText=mViewModel.pInputText.getValue();
        if(lastInputText==null){
            if(!inputText.equals("")) {
                mViewModel.saveInputText(inputText);
            }
        }else{
            if(!inputText.equals("")){
                mViewModel.updateInputText(inputText);
            }else{
                mViewModel.deleteInputText();
            }
        }
    }
}
