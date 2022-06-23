package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestCardBinding;
import cn.pivotstudio.modulec.homescreen.model.ForestCard;

public class AllForestItemAdapter extends ListAdapter<ForestCard, AllForestItemAdapter.ForestCardViewHolder> {

    private View.OnClickListener itemClickListener;

    public static final DiffUtil.ItemCallback<ForestCard> DIFF_CALLBACK = new DiffUtil.ItemCallback<ForestCard>() {
        @Override
        public boolean areItemsTheSame(@NonNull ForestCard oldItem, @NonNull ForestCard newItem) {
            return oldItem.getForestId() == newItem.getForestId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForestCard oldItem, @NonNull ForestCard newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public AllForestItemAdapter(View.OnClickListener clickListener) {
        super(DIFF_CALLBACK);
        itemClickListener = clickListener;
    }

    @NonNull
    @Override
    public ForestCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForestCardViewHolder(
                ItemForestCardBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ForestCardViewHolder holder, int position) {
        ForestCard card = getItem(position);
        holder.itemView.setOnClickListener(itemClickListener);
        holder.bind(card);
    }

    static class ForestCardViewHolder extends RecyclerView.ViewHolder {
        private final ItemForestCardBinding binding;

        public ForestCardViewHolder(ItemForestCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForestCard forestCard) {
            binding.setForestCard(forestCard);
        }

    }

}
