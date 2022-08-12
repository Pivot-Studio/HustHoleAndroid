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

import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.base.model.HoleReturnInfo;
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

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

@Route(path = "/hole/HoleActivity")
public class HoleActivity extends BaseActivity {
    @Autowired(name = Constant.HOLE_ID)
    int hole_id;
    @Autowired(name = Constant.IF_OPEN_KEYBOARD)
    boolean ifOpenKeyboard;

    private ActivityHoleBinding binding;
    private HoleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);//初始化@Autowired
        initView();
        initRefresh();
        initObserver();
        initListener();
    }

    private void initView() {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_hole);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HoleViewModel.class);
        binding.setViewmodel(viewModel);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.rvEmoji.setVisibility(View.GONE);


        if (ifOpenKeyboard) openKeyBoard(binding.etReplyPost);

        viewModel.setHole_id(hole_id);

        viewModel.getListData(false);
        viewModel.getInputText();
        viewModel.getUsedEmojiList();

        EditTextUtil.ButtonReaction(binding.etReplyPost, binding.btnCommentSendmsg);

        binding.rvCommentlist.setLayoutManager(new LinearLayoutManager(context));
        binding.rvCommentlist.setAdapter(new ReplyListRecyclerViewAdapter(this, viewModel));
        binding.rvCommentlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                ConstraintLayout lastMoreListCl = ((ReplyListRecyclerViewAdapter) binding.rvCommentlist.getAdapter()).lastMoreListCl;
                if (lastMoreListCl != null) lastMoreListCl.setVisibility(View.GONE);
                lastMoreListCl = null;
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(
                this,
                6,
                RecyclerView.VERTICAL,
                false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ((position == 0) || (position == 7)) {
                    return 6;
                } else {
                    return 1;
                }
            }
        });
        binding.rvEmoji.setLayoutManager(layoutManager);
        binding.rvEmoji.setAdapter(new EmojiRecyclerViewAdapter(this, binding, viewModel));

        binding.includeHoletitlebar.tvTitlebargreenTitle.setText("#" + hole_id);
        binding.includeHoletitlebar.titlebarAVLoadingIndicatorView.hide();
        binding.includeHoletitlebar.titlebarAVLoadingIndicatorView.setVisibility(View.GONE);
        binding.includeHoletitlebar.clTitlebargreenBack.setOnClickListener(this::onClick);
    }

    private void initRefresh() {
        binding.srlCommmentlistLoadmore.setRefreshHeader(new StandardRefreshHeader(this));//设置自定义刷新头
        binding.srlCommmentlistLoadmore.setRefreshFooter(new ClassicsFooter(this));//设置自定义刷新底

        binding.srlCommmentlistLoadmore.setOnRefreshListener(refreshlayout -> {//下拉刷新触发
            viewModel.setStart_id(0);
            viewModel.getListData(false);
            binding.rvCommentlist.setOnTouchListener((v, event) -> true); //加载时静止滑动
        });

        binding.srlCommmentlistLoadmore.setOnLoadMoreListener(refreshlayout -> {//上拉加载触发
            viewModel.getListData(true);
            binding.rvCommentlist.setOnTouchListener((v, event) -> true);
        });
    }

    private void initObserver() {

        viewModel.pHole.observe(this, holeResponse -> {
        });

        viewModel.pReplyList.observe(this, replyListResponse -> {
            switch (replyListResponse.getModel()) {
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

        viewModel.pInputText.observe(this, replyResponse -> {
            binding.etReplyPost.setText(replyResponse.getContent());
            viewModel.getAnswered().set(replyResponse);
        });
        viewModel.pSendReply.observe((HoleActivity) context, msgResponse -> {
            binding.btnCommentSendmsg.setClickable(true);//发送成功运行点击，以免重复发送
            showMsg(msgResponse.getMsg());
            binding.etReplyPost.setText("");//将输入框内容清空

            //将表情包栏关掉
            binding.rvEmoji.setVisibility(View.GONE);
            // setVisibility(View.GONE);
            ObservableField<Boolean> is_opened = viewModel.getIs_emoji();
            is_opened.set(false);

            viewModel.getListData(false);//重新刷新数据
        });

        viewModel.pClickMsg.observe(this, msgResponse -> {
            showMsg(msgResponse.getMsg());
            switch (msgResponse.getModel()) {
                case "DELETE_HOLE":
                    finish();
                    break;
                case "DELETE_REPLY":
                    viewModel.getListData(false);
                    break;
            }
        });

        viewModel.failed.observe(this, s -> {
            binding.btnCommentSendmsg.setClickable(true);
            showMsg(s);
        });
    }

    private void initListener() {
        binding.etReplyPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 500) {
                    Toast.makeText(HoleActivity.this, "评论不得超过500个字噢~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 下拉刷新或搜索的数据更新
     */
    private void finishRefresh() {
        viewModel.setStart_id(0);
    }

    /**
     * 上拉加载的数据更新
     */
    private void finishLoadMore() {
        viewModel.setStart_id(viewModel.getStart_id() + 20);
    }

    /**
     * 刷新结束后动画的流程
     */
    private void finishRefreshAnim() {
        binding.srlCommmentlistLoadmore.finishRefresh();//结束下拉刷新动画
        binding.srlCommmentlistLoadmore.finishLoadMore();//结束上拉加载动画
        binding.rvCommentlist.setOnTouchListener((v, event) -> false);//加载结束后允许滑动
    }

    /**
     * 处理activity点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        if (view.getId() == R.id.btn_comment_sendmsg) {
            binding.btnCommentSendmsg.setClickable(false);//避免重复发送
            viewModel.sendReply(binding.etReplyPost.getText().toString());
            closeKeyBoard();
        } else if (view.getId() == R.id.btn_replylist_owner) {
            ObservableField<Boolean> observableField = viewModel.getIs_owner();
            observableField.set(!observableField.get());
            viewModel.getListData(false);
        } else if (view.getId() == R.id.cl_titlebargreen_back) {
            if (BuildConfig.isRelease) {
                saveData();//保存数据
                finish();//关闭活动
                closeKeyBoard();//关闭键盘
            } else {
                showMsg("当前处于模块测试阶段");
            }
        } else if (view.getId() == R.id.iv_openemoji) {
            ObservableField<Boolean> is_opened = viewModel.getIs_emoji();
            if (!is_opened.get()) {
                closeKeyBoard();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                ((EmojiRecyclerViewAdapter) (binding.rvEmoji.getAdapter())).refreshData();
                binding.rvEmoji.setVisibility(View.VISIBLE);
                is_opened.set(true);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                binding.rvEmoji.setVisibility(View.GONE);
                // setVisibility(View.GONE);
                is_opened.set(false);
            }
        } else if (view.getId() == R.id.et_reply_post) {
            ObservableField<Boolean> is_opened = viewModel.getIs_emoji();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            binding.rvEmoji.setVisibility(View.VISIBLE);
            openKeyBoard(binding.etReplyPost);
            is_opened.set(false);
        }
    }

    /**
     * 监听手机回退键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            saveData();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 保存输入内容以及选择回复的人
     */
    private void saveData() {
        Intent intent = new Intent();
        HoleReturnInfo holeReturnInfo = new HoleReturnInfo();
        HoleResponse holeResponse = viewModel.pHole.getValue();
        if (holeResponse != null) {
            holeReturnInfo.setIs_thumbup(holeResponse.getIs_thumbup());
            holeReturnInfo.setIs_follow(holeResponse.getIs_follow());
            holeReturnInfo.setIs_reply(holeResponse.getIs_reply());
            holeReturnInfo.setThumbup_num(holeResponse.getThumbup_num());
            holeReturnInfo.setReply_num(holeResponse.getReply_num());
            holeReturnInfo.setFollow_num(holeResponse.getFollow_num());
        }
        intent.putExtra(Constant.HOLE_RETURN_INFO, holeReturnInfo);
        setResult(ResultCodeConstant.Hole, intent);

        MMKVUtil mmkvUtil = MMKVUtil.getMMKV(context);
        mmkvUtil.setArray(Constant.UsedEmoji, viewModel.pUsedEmojiList.getValue());

        String inputText = binding.etReplyPost.getText().toString();
        ReplyListResponse.ReplyResponse lastInputText = viewModel.pInputText.getValue();
        if (lastInputText == null) {
            if (!inputText.equals("")) {
                viewModel.saveInputText(inputText);
            }
        } else {
            if (!inputText.equals("")) {
                viewModel.updateInputText(inputText);
            } else {
                viewModel.deleteInputText();
            }
        }
    }
}
