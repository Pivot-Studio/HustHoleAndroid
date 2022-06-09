package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.modulec.homescreen.databinding.ItemAllForestBinding;
import cn.pivotstudio.modulec.homescreen.model.ForestResponse;

public class AllForestAdapter extends ListAdapter<ForestResponse.ForestCard, AllForestAdapter.ForestCardViewHolder> {

    private View.OnClickListener itemClickListener;

    public static final DiffUtil.ItemCallback<ForestResponse.ForestCard> DIFF_CALLBACK = new DiffUtil.ItemCallback<ForestResponse.ForestCard>() {
        @Override
        public boolean areItemsTheSame(@NonNull ForestResponse.ForestCard oldItem, @NonNull ForestResponse.ForestCard newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForestResponse.ForestCard oldItem, @NonNull ForestResponse.ForestCard newItem) {
            return false;
        }
    };

    public AllForestAdapter(View.OnClickListener clickListener) {
        super(DIFF_CALLBACK);
        itemClickListener = clickListener;
    }

    @NonNull
    @Override
    public ForestCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForestCardViewHolder(
                ItemAllForestBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ForestCardViewHolder holder, int position) {
        ForestResponse.ForestCard card = getItem(position);
        holder.itemView.setOnClickListener(itemClickListener);
        holder.bind(card);
    }

    static class ForestCardViewHolder extends RecyclerView.ViewHolder {
        private final ItemAllForestBinding binding;

        public ForestCardViewHolder(ItemAllForestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForestResponse.ForestCard forestCard) {
            binding.setForestCard(forestCard);
        }

    }

}
