package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter;

import java.util.List;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.databinding.ItemHomepageholeBinding;
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse;
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity;
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel;

/**
 * @classname:HomePageHoleAdapter
 * @description:
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author:
 */
public class HoleRecyclerViewAdapter extends BaseRecyclerViewAdapter<HomepageHoleResponse.DataBean, RecyclerView.ViewHolder> {
    private List<HomepageHoleResponse.DataBean> mHomePageHolesList;
    private HomePageViewModel mViewModel;
    public ConstraintLayout lastMoreListCl;//保证整个recyclerView每次只有一个举报选项显式
    public static final int ITEM_TYPE_REQUEST_FAILED = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    public static final int ITEM_TYPE_NO_SEARCH = 2;

    @Override
    public int getItemViewType(int position) {
        if (mHomePageHolesList == null) {
            return ITEM_TYPE_REQUEST_FAILED;
        } else if (mHomePageHolesList.size() == 0) {
            return ITEM_TYPE_NO_SEARCH;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    /**
     * 如果item数量有限，item会被底部选项栏遮住，这个viewHolder补充底部块的，暂时弃用bottom
     */
    public class RequestFailedHolder extends RecyclerView.ViewHolder {
        public RequestFailedHolder(View view) {
            super(view);

        }

        public void bind(int position) {

        }
    }

    public class NOSearchHolder extends RecyclerView.ViewHolder {
        public NOSearchHolder(View view) {
            super(view);

        }

        public void bind(int position) {

        }
    }

    /**
     * 树洞item的viewHolder
     */
    public class HoleViewHolder extends RecyclerView.ViewHolder {
        ItemHomepageholeBinding mItemHomePageHoleBinding;

        public HoleViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mItemHomePageHoleBinding = (ItemHomepageholeBinding) binding;
            mItemHomePageHoleBinding.setOnClick(mViewModel);
            mItemHomePageHoleBinding.ivItemhomepageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemHomePageHoleBinding.clItemhomepageMorelist.setVisibility(View.VISIBLE);
                    if (lastMoreListCl != null && lastMoreListCl != mItemHomePageHoleBinding.clItemhomepageMorelist) {
                        lastMoreListCl.setVisibility(View.GONE);
                    }
                    lastMoreListCl = mItemHomePageHoleBinding.clItemhomepageMorelist;
                }
            });
            mItemHomePageHoleBinding.clItemhomepageMorelist.setOnClickListener(v -> mItemHomePageHoleBinding.clItemhomepageMorelist.setVisibility(View.INVISIBLE));
        }

        public ItemHomepageholeBinding getBinding() {
            return mItemHomePageHoleBinding;
        }

        public void bind(int position) {
            mItemHomePageHoleBinding.clItemhomepageMorelist.setVisibility(View.GONE);
            lastMoreListCl = null;
        }
    }

    /**
     * 构造函数
     *
     * @param mViewModel 碎片的viewModel
     * @param context
     */
    public HoleRecyclerViewAdapter(HomePageViewModel mViewModel, Context context) {
        super(context);
        this.mViewModel = mViewModel;
        mViewModel.pHomePageHoles.observe((HomeScreenActivity) context, homepageHoleResponse -> {
            int lastLength = getItemCount();
            mHomePageHolesList = homepageHoleResponse.getData();
            switch (homepageHoleResponse.getModel()) {
                case "REFRESH":
                    notifyDataSetChanged();
                    break;
                case "SEARCH_REFRESH":
                    notifyDataSetChanged();
                    break;
                case "LOAD_MORE":
                case "SEARCH_LOAD_MORE":
                    notifyItemRangeChanged(lastLength, homepageHoleResponse.getData().size());
                    break;
                case "SEARCH_HOLE":
                    notifyDataSetChanged();
                    break;

                case "BASE":
                    break;
            }


        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT) {
            ItemHomepageholeBinding itemHomepageholeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_homepagehole, parent, false);
            return new HoleViewHolder(itemHomepageholeBinding);
        } else if (viewType == ITEM_TYPE_REQUEST_FAILED) {
            return new HoleRecyclerViewAdapter.RequestFailedHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_requestfailed, parent, false));
        } else if (viewType == ITEM_TYPE_NO_SEARCH) {
            return new HoleRecyclerViewAdapter.NOSearchHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_nosearch, parent, false));
        }
        return null;
    }

    @Override
    public void onBindBaseViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HoleRecyclerViewAdapter.HoleViewHolder) {
            ItemHomepageholeBinding binding = ((HoleRecyclerViewAdapter.HoleViewHolder) holder).getBinding();
            binding.setHomePageHole(mHomePageHolesList.get(position));
            binding.executePendingBindings();
            ((HoleRecyclerViewAdapter.HoleViewHolder) holder).bind(position);
        }
    }

    /**
     * 更新数据
     *
     * @param list
     */
    public void refreshData(List<HomepageHoleResponse.DataBean> list) {

    }

    @Override
    public int getItemCount() {
        return ((mHomePageHolesList == null) || (mHomePageHolesList.size() == 0)) ? 1 : mHomePageHolesList.size();
    }
}
