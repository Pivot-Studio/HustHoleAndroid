package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.modulec.homescreen.databinding.ItemForestBinding;
import cn.pivotstudio.modulec.homescreen.model.ForestResponse;


/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
public class ForestHoleAdapter extends ListAdapter<ForestResponse.ForestHole, ForestHoleAdapter.ForestHoleViewHolder> {

    public static final DiffUtil.ItemCallback<ForestResponse.ForestHole> DIFF_CALLBACK = new DiffUtil.ItemCallback<ForestResponse.ForestHole>() {
        @Override
        public boolean areItemsTheSame(@NonNull ForestResponse.ForestHole oldItem, @NonNull ForestResponse.ForestHole newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForestResponse.ForestHole oldItem, @NonNull ForestResponse.ForestHole newItem) {
            return false;
        }
    };

    public ForestHoleAdapter() {
        super(DIFF_CALLBACK);
    }


    @NonNull
    @Override
    public ForestHoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForestHoleViewHolder(
                ItemForestBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull ForestHoleViewHolder holder, int position) {
        final ForestResponse.ForestHole forestHole = getItem(position);
        holder.bind(forestHole);
    }

    static class ForestHoleViewHolder extends RecyclerView.ViewHolder {

        private final ItemForestBinding binding;

        public ForestHoleViewHolder(ItemForestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForestResponse.ForestHole forestHole) {
            binding.setForestHole(forestHole);
        }

    }

}

