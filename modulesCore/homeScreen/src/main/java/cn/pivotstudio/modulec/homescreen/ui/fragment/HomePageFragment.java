package cn.pivotstudio.modulec.homescreen.ui.fragment;

import static com.example.libbase.util.data.CheckStrUtil.checkStrIsNum;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.libbase.base.model.HoleReturnInfo;
import com.example.libbase.base.ui.fragment.BaseFragment;
import com.example.libbase.constant.Constant;
import com.example.libbase.constant.RequestCodeConstant;
import com.example.libbase.constant.ResultCodeConstant;
import com.example.libbase.util.ui.EditTextUtil;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter;
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader;
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding;
import cn.pivotstudio.modulec.homescreen.ui.adapter.HoleRecyclerViewAdapter;
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel;

/**
 * @classname:HomePageFragment
 * @description:
 * @date:2022/5/2 22:56
 * @version:1.0
 * @author:
 */
public class HomePageFragment extends BaseFragment {
    private FragmentHomepageBinding binding;
    private HomePageViewModel mViewModel;
    private HoleRecyclerViewAdapter holeRecyclerViewAdapter;

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false);
        mViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        //
        mViewModel.refreshHoleList(0);//初次加载
        initView();
        initRefresh();
        initObserver();
        return binding.getRoot();
    }

    /**
     * 获取点赞，关注，回复结果反馈的，fragment的onActivityResult在androidx的某个版本不推荐使用了，先暂时用着
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != RequestCodeConstant.HOMEPAGE) {//如果不是由homepage这个fragment跳转过去的，返回的结果不接受
            return;
        }
        if (resultCode == ResultCodeConstant.Hole) {//如果是由hole返回的数据
            HoleReturnInfo returnInfo = data.getParcelableExtra(Constant.HOLE_RETURN_INFO);
            mViewModel.pClickDataBean.setIs_thumbup(returnInfo.getIs_thumbup());
            mViewModel.pClickDataBean.setIs_reply(returnInfo.getIs_reply());
            mViewModel.pClickDataBean.setIs_follow(returnInfo.getIs_follow());
            mViewModel.pClickDataBean.setThumbup_num(returnInfo.getThumbup_num());
            mViewModel.pClickDataBean.setReply_num(returnInfo.getReply_num());
            mViewModel.pClickDataBean.setFollow_num(returnInfo.getFollow_num());
        }
    }

    /**
     * 视图初始化
     */
    private void initView() {

        EditTextUtil.EditTextSize(binding.etHomepage, new SpannableString(this.getResources().getString(R.string.page1fragment_1)), 12);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holeRecyclerViewAdapter = new HoleRecyclerViewAdapter(mViewModel, getContext());

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                ConstraintLayout lastMoreListCl = ((HoleRecyclerViewAdapter) binding.recyclerView.getAdapter()).lastMoreListCl;
                if (lastMoreListCl != null) lastMoreListCl.setVisibility(View.GONE);
                lastMoreListCl = null;
            }
        });


        binding.clMidBlock.setOptionsListener(this::onClick);
        binding.etHomepage.setOnClickListener(this::onClick);
        binding.etHomepage.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        binding.etHomepage.setOnEditorActionListener(this::onEditorListener);


    }

    /**
     * 初始化刷新框架
     */
    private void initRefresh() {
        binding.refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));//设置自定义刷新头
        binding.refreshLayout.setRefreshFooter(new StandardRefreshFooter(getActivity()));//设置自定义刷新底

        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {//下拉刷新触发
            mViewModel.refreshHoleList(0);
            binding.recyclerView.setOnTouchListener((v, event) -> true);
        });

        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {//上拉加载触发
            if (mViewModel.pHomePageHoles.getValue() == null) {//特殊情况，首次加载没加载出来又选择上拉加载
                mViewModel.refreshHoleList(0);
                binding.recyclerView.setOnTouchListener((v, event) -> true);
            } else {
                binding.recyclerView.setOnTouchListener((v, event) -> true);
                if (mViewModel.getIsSearch()) {//如果当前是搜索状态
                    mViewModel.searchHoleList(mViewModel.getStartLoadId() + 20);
                } else {
                    mViewModel.refreshHoleList(mViewModel.getStartLoadId() + 20);
                }
            }
        });
    }

    /**
     * 初始化ViewModel数据观察者
     */
    private void initObserver() {
        mViewModel.pHomePageHoles.observe(context, homepageHoleResponse -> {//监听列表信息变化
            if (binding.recyclerView.getAdapter() == null)
                binding.recyclerView.setAdapter(holeRecyclerViewAdapter);
            int length = homepageHoleResponse.getData().size();
            switch (homepageHoleResponse.getModel()) {
                case "REFRESH":
                    finishRefresh(true);
                    break;
                case "SEARCH_REFRESH":
                    finishRefresh(false);
                    break;
                case "LOAD_MORE":
                    finishLoadMore(length);
                    break;
                case "SEARCH_LOAD_MORE":
                    finishLoadMore(length);
                    break;
                case "SEARCH_HOLE":
                    finishRefresh(false);
                    break;

                case "BASE":
                    break;
            }
            finishRefreshAnim();
        });
        mViewModel.pClickMsg.observe(context, MsgResponse -> {
            showMsg(MsgResponse.getMsg());
        });
        mViewModel.failed.observe(context, s -> {//监听网络请求错误信息变化
            if (binding.recyclerView.getAdapter() == null)
                binding.recyclerView.setAdapter(holeRecyclerViewAdapter);
            showMsg(s);
            finishRefreshAnim();
        });
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    private void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ppwhomepage_newpublish) {
            if (!mViewModel.getIsDescend()) {
                mViewModel.setIsDescend(true);
                mViewModel.refreshHoleList(0);
            }
        } else if (id == R.id.btn_ppwhomepage_newcomment) {
            if (mViewModel.getIsDescend()) {
                mViewModel.setIsDescend(false);
                mViewModel.refreshHoleList(0);
            }
        } else if (id == R.id.et_homepage) {
        }
    }

    /**
     * 键盘搜索监听
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    private boolean onEditorListener(TextView v, int actionId, KeyEvent event) {
        if ((binding.etHomepage.getText() != null
                && !binding.etHomepage.getText().toString().equals("")
        ) && (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()))) {
            String et = binding.etHomepage.getText().toString();
            mViewModel.setSearchKeyword(et);
            mViewModel.setIsSearch(true);
            if (checkStrIsNum(et) || (et.charAt(0)) == ('#')) {
                mViewModel.searchSingleHole();
            } else {
                mViewModel.searchHoleList(0);
            }
        }
        return false;
    }

    /**
     * 下拉刷新或搜索的数据更新
     *
     * @param isSearch 决定是否是搜索状态下，非搜索状态下下拉加载需要将状态切换
     */
    private void finishRefresh(Boolean isSearch) {
        if (isSearch) mViewModel.setIsSearch(false);
        mViewModel.setStartLoadId(0);


    }

    /**
     * 上拉加载的数据更新
     */
    private void finishLoadMore(int length) {
        Integer lastStartId = mViewModel.getStartLoadId();
        mViewModel.setStartLoadId(lastStartId + length);
    }

    /**
     * 刷新结束后动画的流程
     */
    private void finishRefreshAnim() {
        binding.etHomepage.setText("");
        binding.refreshLayout.finishRefresh();//结束下拉刷新动画
        binding.refreshLayout.finishLoadMore();//结束上拉加载动画
        binding.recyclerView.setOnTouchListener((v, event) -> false);//加载结束后允许滑动

    }
}
