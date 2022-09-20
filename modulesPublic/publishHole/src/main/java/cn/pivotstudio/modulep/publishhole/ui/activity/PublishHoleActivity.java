package cn.pivotstudio.modulep.publishhole.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;
import cn.pivotstudio.moduleb.libbase.util.ui.SoftKeyBoardUtil;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.modulep.publishhole.BuildConfig;
import cn.pivotstudio.modulep.publishhole.R;

import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow;
import cn.pivotstudio.modulep.publishhole.databinding.ActivityPublishholeBinding;
import cn.pivotstudio.modulep.publishhole.ui.adapter.ForestRecyclerViewAdapter;
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel;

/**
 * @classname PublishHoleActivity
 * @description: 发布树洞界面
 * @date 2022/5/5 22:43
 * @version:1.0
 * @author:
 */
@Route(path = "/publishHole/PublishHoleActivity")
public class PublishHoleActivity extends BaseActivity {
    @Autowired(name = Constant.FROM_DETAIL_FOREST)
    Bundle args;

    String forestName;
    int forestId;

    private PublishHoleViewModel mViewModel;
    private ActivityPublishholeBinding binding;
    private ForestsPopupWindow mPpw;
    private MMKVUtil mmkvUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        if (args != null) {
            forestId = Integer.parseInt(args.getString(Constant.FOREST_ID));
            forestName = args.getString(Constant.FOREST_NAME);
        }
        initView();
        initListener();
        initObserver();
    }

    /**
     * 初始化view
     */
    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publishhole);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PublishHoleViewModel.class);
        if (args != null) {
            mViewModel.pForestName.setValue(forestName);
            mViewModel.setForestId(forestId);
        } else {
            mViewModel.pForestName.setValue("未选择小树林");
        }

        binding.clTitlebargreenBack.setOnClickListener(this::onClick);
        binding.titlebargreenAVLoadingIndicatorView.hide();
        binding.tvTitlebargreenTitle.setText("发树洞");
        binding.titlebargreenAVLoadingIndicatorView.setVisibility(View.GONE);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);
        EditTextUtil.ButtonReaction(binding.etPublishhole, binding.btnPublishholeSend);
        EditTextUtil.EditTextSize(binding.etPublishhole, new SpannableString(this.getResources().getString(R.string.publishhole_4)), 14);

        mmkvUtil = MMKVUtil.getMMKV(this);
        if (mmkvUtil != null) {
            if (mmkvUtil.getString(Constant.HOLE_TEXT) != null) {
                String lastText = mmkvUtil.getString(Constant.HOLE_TEXT);
                binding.etPublishhole.setText(lastText);
                binding.tvPublishholeTextnumber.setText(lastText.length() + "/1037");
            }
        }

    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        binding.etPublishhole.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvPublishholeTextnumber.setText(s.length() + "/1037");
                if (s.length() >= 1037) {
                    Toast.makeText(PublishHoleActivity.this, "输入内容过长", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SoftKeyBoardUtil.setListener(this, new SoftKeyBoardUtil.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                changeEtHeight();
            }

            @Override
            public void keyBoardHide(int height) {
                changeEtHeight();
            }

            public void changeEtHeight() {
                int[] locationHead = new int[2];
                binding.btnPublishholeLine.getLocationOnScreen(locationHead);
                int[] locationBottom = new int[2];
                binding.tvPublishholeTextnumber.getLocationOnScreen(locationBottom);
                binding.etPublishhole.setMaxHeight(locationBottom[1] - locationHead[1] - 20);
            }
        });
    }

    /**
     * 初始化数据监听
     */
    private void initObserver() {
        mViewModel.pJoinedForest.observe(this, detailTypeForestResponse -> {
            ((ForestRecyclerViewAdapter) (mPpw.recyclerView.getAdapter())).changeDataJoinedForest(detailTypeForestResponse);
        });
        mViewModel.pForestType.observe(this, forestTypeResponse -> {
            List<String> list = new ArrayList<>();
            list.add("热门");
            list.addAll(forestTypeResponse.getTypes());
            ((ForestRecyclerViewAdapter) (mPpw.recyclerView.getAdapter())).changeDataType(list);
            mViewModel.getHotForest();
            for (int i = 0; i < forestTypeResponse.getTypes().size(); i++) {
                mViewModel.getTypeForest(forestTypeResponse.getTypes().get(i), i + 1);
            }
        });
        mViewModel.pTypeForestList.observe(this, forestListsResponse -> {
            if (forestListsResponse.getItemNumber() != -1)
                ((ForestRecyclerViewAdapter) (mPpw.recyclerView.getAdapter())).changeDataDetailTypeForest(forestListsResponse);
        });
        mViewModel.pForestName.observe(this, s -> {
            binding.tvPublishholeForestname.setText(s);
        });

        mViewModel.pOnClickMsg.observe(this, msgResponse -> {
            binding.btnPublishholeSend.setClickable(true);
            mmkvUtil.put(Constant.HOLE_TEXT, "");
            showMsg("发布成功");
            finish();
        });
        mViewModel.failed.observe(this, s -> {
            binding.btnPublishholeSend.setClickable(true);
            showMsg(s);
        });
    }


    /**
     * 相关点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        closeKeyBoard();
        int id = view.getId();
        if (id == R.id.tv_publishhole_forestname) {
            if (mPpw == null) mPpw = new ForestsPopupWindow(this);

            //没有必要保证实时性重复创建，所以需要保存
            mViewModel.getJoinedForests();
            mViewModel.getForestType();

            mPpw.showAtLocation(binding.clPublishhole, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);//在activity的底部展示。
            mPpw.show();
        } else if (id == R.id.btn_publishhole_send) {
            String content = binding.etPublishhole.getText().toString();
            if (content.length() > 15) {
                binding.btnPublishholeSend.setClickable(false);
                mViewModel.postHoleRequest(content);
            } else {
                showMsg("输入内容至少需要15字");
            }
        } else if (id == R.id.cl_titlebargreen_back) {
            if (BuildConfig.isRelease) {
                mmkvUtil.put(Constant.HOLE_TEXT, binding.etPublishhole.getText().toString());
                finish();
            } else {
                showMsg("当前处于模块测试阶段");
            }
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
            mmkvUtil.put(Constant.HOLE_TEXT, binding.etPublishhole.getText().toString());
        }
        return super.onKeyDown(keyCode, event);
    }

}
