package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.modulec.homescreen.databinding.ItemForestBinding;
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestHeadBinding;
import cn.pivotstudio.modulec.homescreen.model.ForestHoleResponse;

/**
 * @classname: ForestHeadAdapter
 * @description: 小树林头部"已关注小树林"的 Adapter
 * @date: 2022/6/5 2:13
 * @version: 1.0
 * @author: mhh
 */
public class ForestHeadAdapter extends ListAdapter<ForestHoleResponse.ForestHead, ForestHeadAdapter.ForestHoleViewHolder> {

    public static final DiffUtil.ItemCallback<ForestHoleResponse.ForestHead> DIFF_CALLBACK = new DiffUtil.ItemCallback<ForestHoleResponse.ForestHead>() {
        @Override
        public boolean areItemsTheSame(@NonNull ForestHoleResponse.ForestHead oldItem, @NonNull ForestHoleResponse.ForestHead newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForestHoleResponse.ForestHead oldItem, @NonNull ForestHoleResponse.ForestHead newItem) {
            return false;
        }
    };

    public ForestHeadAdapter() {
        super(DIFF_CALLBACK);
    }


    @NonNull
    @Override
    public ForestHoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForestHoleViewHolder(
                ItemForestHeadBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull ForestHoleViewHolder holder, int position) {
        final ForestHoleResponse.ForestHead forestHead = getItem(position);
        holder.bind(forestHead);
    }

    static class ForestHoleViewHolder extends RecyclerView.ViewHolder {

        private final ItemForestHeadBinding binding;

        public ForestHoleViewHolder(ItemForestHeadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForestHoleResponse.ForestHead forestHead) {
            binding.setForestHead(forestHead);
        }

    }

}
