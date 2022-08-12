package cn.pivotstudio.modulep.publishhole.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulep.publishhole.R;
import cn.pivotstudio.modulep.publishhole.custom_view.ForestExpandableListView;
import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow;

import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForestlistBinding;
import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestListsResponse;
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity;
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel;

/**
 * @classname:ForestAdapter
 * @description:recyclerview的适配器
 * @date:2022/5/6 0:54
 * @version:1.0
 * @author:
 */
public class ForestRecyclerViewAdapter extends BaseRecyclerViewAdapter<DetailTypeForestResponse, RecyclerView.ViewHolder> {
    private final int Title = 0, JoinedForest = 1, Forest = 2, Bottom = 3;
    private List<DetailTypeForestResponse.ForestResponse> mJoined;
    private List<String> mType;
    public List<DetailTypeForestResponse> mDetailTypeForest;
    private ForestListElAdapter mElAdapter;//elv的适配器
    private ForestsPopupWindow mPpw;//外层ppw

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mJoined.size() + 1) {
            return Title;
        } else if (position > 0 && position < (mJoined.size() + 1)) {
            return JoinedForest;
        } else if (position == getItemCount() - 1 || position == getItemCount() - 2) {
            return Bottom;
        } else {
            return Forest;
        }
    }

    public ForestRecyclerViewAdapter(Context context, ForestsPopupWindow mPpw) {
        super(context);
        mJoined = new ArrayList<>();
        mType = new ArrayList<>();
        this.mPpw = mPpw;
    }

    public class BottomViewHolder extends RecyclerView.ViewHolder {
        public BottomViewHolder(View itemView) {
            super(itemView);

        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTv;

        public TitleViewHolder(View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.tv_publishholeforestlisttitle_title);
        }

        public void bind(int position) {
            mTitleTv.setText(position == 0 ? "加入的小树林" : "全部小树林");
        }
    }

    public class JoinedForestViewHolder extends RecyclerView.ViewHolder {
        ItemPublishholeForestlistBinding binding;
        int position;

        public JoinedForestViewHolder(ItemPublishholeForestlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnPublishholeforestlistChooseforest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublishHoleViewModel publishHoleViewModel = new ViewModelProvider((PublishHoleActivity) mContext, new ViewModelProvider.NewInstanceFactory()).get(PublishHoleViewModel.class);
                    publishHoleViewModel.setForestId(mJoined.get(position).getForest_id());
                    publishHoleViewModel.pForestName.setValue(mJoined.get(position).getName());
                    mPpw.dismiss();
                }
            });
            binding.ivPublishholeforestlistIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public ItemPublishholeForestlistBinding getBinding() {
            return binding;
        }

        public void bind(int position) {
            this.position = position;
        }
    }

    public class ForestTypeViewHolder extends RecyclerView.ViewHolder {
        ForestExpandableListView mElv;

        public ForestTypeViewHolder(View itemView) {
            super(itemView);
            mElv = itemView.findViewById(R.id.elv_ppwpublishhole);
            if (mElAdapter == null) {
                mElAdapter = new ForestListElAdapter(mContext, mType, mPpw);
            }
            ;
            mElv.setAdapter(mElAdapter);
            //默认展开第一个数组
            //mElv.setGroupIndicator(null);
            //mElv.expandGroup(0);
            //关闭数组某个数组，可以通过该属性来实现全部展开和只展开一个列表功能
            //expand_list_id.collapseGroup(0);
            mElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                    if (mDetailTypeForest == null || mDetailTypeForest.get(groupPosition).getForests() == null) {
                        showMsg("加载中...");
                        return true;
                    } else if (mDetailTypeForest.get(groupPosition).getForests().size() == 0) {
                        showMsg("无此类型小树林");
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            //子视图的点击事件
            mElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {


                    return false;
                }
            });
            //用于当组项折叠时的通知。
            mElv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {

                }
            });
            //
            //用于当组项折叠时的通知。
            mElv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });
        }

        public void bind(int position) {

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Title) {
            return new ForestRecyclerViewAdapter
                    .TitleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_publishhole_forestlisttitle, parent, false));
        } else if (viewType == JoinedForest) {
            ItemPublishholeForestlistBinding itemPublishholeForestlistBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_publishhole_forestlist, parent, false);
            return new JoinedForestViewHolder(itemPublishholeForestlistBinding);
        } else if (viewType == Forest) {
            return new ForestRecyclerViewAdapter
                    .ForestTypeViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expandablelistview, parent, false));
        } else if (viewType == Bottom) {
            return new ForestRecyclerViewAdapter
                    .BottomViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bottomblock, parent, false));
        }
        return null;
    }

    @Override
    public void onBindBaseViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ForestRecyclerViewAdapter.JoinedForestViewHolder) {
            ItemPublishholeForestlistBinding binding = ((ForestRecyclerViewAdapter.JoinedForestViewHolder) holder).getBinding();
            binding.setForest(mJoined.get(position - 1));
            binding.executePendingBindings();
            ((ForestRecyclerViewAdapter.JoinedForestViewHolder) holder).bind(position - 1);
        } else if (holder instanceof ForestRecyclerViewAdapter.ForestTypeViewHolder) {
            ((ForestRecyclerViewAdapter.ForestTypeViewHolder) holder).bind(position);
        } else if (holder instanceof ForestRecyclerViewAdapter.TitleViewHolder) {
            ((ForestRecyclerViewAdapter.TitleViewHolder) holder).bind(position);
        }
    }

    /**
     * 两个标题+两个底部支撑bottom防止遮盖+item+elv
     *
     * @return item数量
     */
    @Override
    public int getItemCount() {
        return (4 + (mJoined.size()) + (mType.size() == 0 ? 0 : 1));
    }

    /**
     * 添加加入的小树林
     *
     * @param mJoined
     */
    public void changeDataJoinedForest(DetailTypeForestResponse mJoined) {
        if (mJoined.getForests() != null) {//未加入的话，后端直接返回null
            this.mJoined = mJoined.getForests();

        }
        notifyDataSetChanged();
    }

    /**
     * 添加小树林类型
     *
     * @param Type
     */
    public void changeDataType(List<String> Type) {
        if (Type != null) {//防一手为null
            mType = Type;
            notifyDataSetChanged();
        }
    }

    /**
     * 改变具体类型的小树林
     *
     * @param lists
     */
    public void changeDataDetailTypeForest(ForestListsResponse lists) {
        if (mDetailTypeForest == null) {
            mDetailTypeForest = lists.getLists();
            mElAdapter.changeDataForests(lists.getLists());
        }
    }
}
