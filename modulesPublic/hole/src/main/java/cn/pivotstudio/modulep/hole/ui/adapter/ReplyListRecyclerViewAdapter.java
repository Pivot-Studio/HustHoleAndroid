package cn.pivotstudio.modulep.hole.ui.adapter;

import static cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.context;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter;

import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.databinding.ItemHoleBinding;
import cn.pivotstudio.modulep.hole.databinding.ItemReplyBinding;
import cn.pivotstudio.modulep.hole.model.HoleResponse;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity;
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel;

/**
 * @classname:ReplyListRecyclerViewAdapter
 * @description:
 * @date:2022/5/9 0:50
 * @version:1.0
 * @author:
 */
public class ReplyListRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    private final int Hole = 0, Reply = 1, NOReply = 2, LoadingHole = 3, LoadingReply = 4;
    private final HoleViewModel mViewModel;
    private ReplyListResponse mReply;

    private HoleResponse mHole;
    public ConstraintLayout lastMoreListCl;//保证整个recyclerView每次只有一个举报选项显式

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (mHole == null) {
                return LoadingHole;
            } else {
                return Hole;
            }
        } else {
            if ((mReply == null)) {
                return LoadingReply;
            } else if ((mReply.getMsg().size() == 0)) {
                return NOReply;
            } else {
                return Reply;
            }
        }
    }

    public class HoleViewHolder extends RecyclerView.ViewHolder {
        ItemHoleBinding binding;
        int position;

        public HoleViewHolder(ItemHoleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.tvHoleContent.setOnLongClickListener(new View.OnLongClickListener() {  //给Button注册长按事件监听器
                @Override
                public boolean onLongClick(View v) {  //重写监听器中的onLongClick()方法
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(binding.tvHoleContent.getText().toString());
                    showMsg("内容已复制至剪切板");
                    return false;
                }
            });
            binding.ivHoleMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.clHoleMorelist.setVisibility(View.VISIBLE);
                    if (lastMoreListCl != null && lastMoreListCl != binding.clHoleMorelist) {
                        lastMoreListCl.setVisibility(View.GONE);
                    }
                    lastMoreListCl = binding.clHoleMorelist;
                }
            });
            binding.clHoleMorelist.setOnClickListener(v -> binding.clHoleMorelist.setVisibility(View.INVISIBLE));
        }

        public ItemHoleBinding getBinding() {
            return binding;
        }

        public void bind(int position) {
            this.position = position;
        }
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        ItemReplyBinding binding;
        int position;

        public ReplyViewHolder(ItemReplyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvReplyContent.setOnLongClickListener(new View.OnLongClickListener() {  //给Button注册长按事件监听器
                @Override
                public boolean onLongClick(View v) {  //重写监听器中的onLongClick()方法
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(binding.tvReplyContent.getText().toString());
                    showMsg("内容已复制至剪切板");
                    return false;
                }
            });
            binding.ivReplyMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.clReplyMorelist.setVisibility(View.VISIBLE);
                    if (lastMoreListCl != null && lastMoreListCl != binding.clReplyMorelist) {
                        lastMoreListCl.setVisibility(View.GONE);
                    }
                    lastMoreListCl = binding.clReplyMorelist;
                }
            });
            binding.clReplyMorelist.setOnClickListener(v -> binding.clReplyMorelist.setVisibility(View.INVISIBLE));
        }

        public ItemReplyBinding getBinding() {
            return binding;
        }

        public void bind(int position) {
            this.position = position;

            binding.tvReplyLine.setWidth(1);
            binding.tvReplyLine.setHeight(getViewHeight(binding.tvReplyContent, true) - 110);
        }
    }

    public class NoReplyHolder extends RecyclerView.ViewHolder {
        public NoReplyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class LoadingReplyHolder extends RecyclerView.ViewHolder {
        public LoadingReplyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class LoadingHoleHolder extends RecyclerView.ViewHolder {
        public LoadingHoleHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public ReplyListRecyclerViewAdapter(Context context, HoleViewModel mViewModel) {
        super(context);
        this.mViewModel = mViewModel;
        mViewModel.pReplyList.observe((HoleActivity) context, replyListResponse -> {
            switch (replyListResponse.getModel()) {
                case "REFRESH":
                    mReply = replyListResponse;
                    notifyDataSetChanged();
                    break;
                case "LOAD_MORE":
                    int length = getItemCount();
                    mReply = replyListResponse;
                    notifyItemRangeInserted(length, replyListResponse.getMsg().size());
                    break;
                case "LOAD_ALL":
                    showMsg("加载到底辣");
                    break;
                case "NO_REPLY":
                    mReply = replyListResponse;
                    notifyDataSetChanged();
                    break;
            }
        });

        mViewModel.pHole.observe((HoleActivity) context, holeResponse -> {
            mHole = holeResponse;
            notifyItemChanged(0);
        });
    }

    public static int getViewHeight(View view, boolean isHeight) {
        int result;
        if (view == null) return 0;
        if (isHeight) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(h, 0);
            result = view.getMeasuredHeight();
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(0, w);
            result = view.getMeasuredWidth();
        }
        return result;
    }

    @Override
    public int getItemCount() {

        int replyLength;
        if (mReply == null) {
            replyLength = 3;
        } else if (mReply.getMsg().size() == 0) {
            replyLength = 1;
        } else {
            replyLength = mReply.getMsg().size();
        }

        int holeLength = (mHole == null ? 0 : 1);

        return replyLength + holeLength;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Hole) {
            ItemHoleBinding itemHoleBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_hole, parent, false);
            return new HoleViewHolder(itemHoleBinding);
        } else if (viewType == Reply) {
            ItemReplyBinding itemReplyBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_reply, parent, false);


            return new ReplyViewHolder(itemReplyBinding);

        } else if (viewType == NOReply) {
            return new ReplyListRecyclerViewAdapter.NoReplyHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_noreply, parent, false));
        } else if (viewType == LoadingHole) {
            return new ReplyListRecyclerViewAdapter.NoReplyHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_holeloading, parent, false));
        } else if (viewType == LoadingReply) {
            return new ReplyListRecyclerViewAdapter.NoReplyHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_replyloading, parent, false));
        }
        return null;
    }

    @Override
    public void onBindBaseViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReplyListRecyclerViewAdapter.HoleViewHolder) {
            ItemHoleBinding binding = ((ReplyListRecyclerViewAdapter.HoleViewHolder) holder).getBinding();
            binding.setHole(mViewModel.pHole.getValue());
            binding.setOnClick(mViewModel);
            binding.executePendingBindings();

        } else if (holder instanceof ReplyListRecyclerViewAdapter.ReplyViewHolder) {
            ItemReplyBinding binding = ((ReplyListRecyclerViewAdapter.ReplyViewHolder) holder).getBinding();
            binding.setReply(mViewModel.pReplyList.getValue().getMsg().get(position - 1));
            binding.setOnClick(mViewModel);
            binding.executePendingBindings();
            ((ReplyListRecyclerViewAdapter.ReplyViewHolder) holder).bind(position);
        } else if (holder instanceof ReplyListRecyclerViewAdapter.NoReplyHolder) {

        }
    }
}
