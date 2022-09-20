package cn.pivotstudio.modulep.hole.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter;
import cn.pivotstudio.moduleb.libbase.util.emoji.EmotionUtil;


import java.util.LinkedList;
import java.util.List;

import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.databinding.ActivityHoleBinding;
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity;
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel;

/**
 * @classname:EmojiRecyclerViewAdapter
 * @description:
 * @date:2022/5/12 23:20
 * @version:1.0
 * @author:
 */
public class EmojiRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    private final static Integer Title = 0, Used_Emoji = 1, Emoji = 2;
    private LinkedList<Integer> mUsedEmoji;
    private LinkedList<Integer> mUsedEmojiCopy;
    private List<Integer> mEmoji;
    private List<String> mEmojiName;
    private ActivityHoleBinding mActivityBinding;

    @Override
    public int getItemViewType(int position) {
        if ((position == 0) || (position == (1 + mUsedEmoji.size()))) {
            return Title;
        } else if ((position > 0) && (position < (1 + mUsedEmoji.size()))) {
            return Used_Emoji;
        } else {
            return Emoji;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (viewType == Title) {
                        return 6;
                    } else {
                        return 1;
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return 2 + mEmoji.size() + mUsedEmoji.size();
    }

    private class EmojiHolder extends RecyclerView.ViewHolder {
        ImageView img;
        int position;

        public EmojiHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_emoji);
            itemView.setOnClickListener(v -> {
                mActivityBinding.etReplyPost.
                        getText().
                        insert(mActivityBinding.etReplyPost.getSelectionStart(), mEmojiName.get(position));
                changeData(position);
            });

        }

        public void bind(int position) {
            this.position = position;

            img.setImageResource(mEmoji.get(position));
        }
    }

    private class UsedEmojiHolder extends RecyclerView.ViewHolder {
        ImageView img;
        int position;

        public UsedEmojiHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_emoji);
            itemView.setOnClickListener(v -> {
                mActivityBinding.etReplyPost.
                        getText().
                        insert(mActivityBinding.etReplyPost.getSelectionStart(), mEmojiName.get(mUsedEmoji.get(position)));
                changeData(mUsedEmoji.get(position));

            });

        }

        public void bind(int position) {
            this.position = position;
            img.setImageResource(mEmoji.get(mUsedEmoji.get(position)));
        }
    }

    private class TitleHolder extends RecyclerView.ViewHolder {
        TextView text;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_emoji_title);
        }

        public void bind(int position) {
            text.setText(position == 0 ? "最近使用" : "树洞小表情");
        }

    }

    public EmojiRecyclerViewAdapter(Context context, ActivityHoleBinding mActivityBinding, HoleViewModel mViewModel) {
        super(context);
        this.mActivityBinding = mActivityBinding;
        mEmoji = EmotionUtil.getResourceList();
        mEmojiName = EmotionUtil.getResourceName();
        mUsedEmoji = new LinkedList<>();
        mViewModel.pUsedEmojiList.observe((HoleActivity) context, usedEmojiList -> {
            if (usedEmojiList != null) mUsedEmoji.addAll(usedEmojiList);
            mUsedEmojiCopy = usedEmojiList;
        });
    }

    public void refreshData() {
        mUsedEmoji.clear();
        mUsedEmoji.addAll(mUsedEmojiCopy);
        notifyDataSetChanged();
    }

    private void changeData(int position) {
        if (mUsedEmojiCopy.contains(position)) {
            for (int i = 0; i < mUsedEmojiCopy.size(); i++) {
                if (position == (mUsedEmojiCopy.get(i))) {
                    mUsedEmojiCopy.remove(i);
                    mUsedEmojiCopy.addFirst(position);
                    break;
                }
            }
        } else {
            mUsedEmojiCopy.addFirst(position);
            if (mUsedEmojiCopy.size() > 6) {
                mUsedEmojiCopy.removeLast();
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Title) {
            return new EmojiRecyclerViewAdapter.TitleHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emoji_title, parent, false));
        } else if (viewType == Used_Emoji) {
            return new EmojiRecyclerViewAdapter.UsedEmojiHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emoji, parent, false));
        } else if (viewType == Emoji) {
            return new EmojiRecyclerViewAdapter.EmojiHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emoji, parent, false));
        }
        return null;
    }

    @Override
    public void onBindBaseViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmojiHolder) {
            ((EmojiRecyclerViewAdapter.EmojiHolder) holder).bind(position - 2 - mUsedEmoji.size());
        } else if (holder instanceof TitleHolder) {
            ((EmojiRecyclerViewAdapter.TitleHolder) holder).bind(position);
        } else if (holder instanceof UsedEmojiHolder) {
            ((EmojiRecyclerViewAdapter.UsedEmojiHolder) holder).bind(position - 1);
        }
    }

}
