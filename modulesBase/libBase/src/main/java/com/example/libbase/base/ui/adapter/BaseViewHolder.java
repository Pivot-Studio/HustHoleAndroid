package com.example.libbase.base.ui.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @classname:BaseViewHolder
 * @description:
 * @date:2022/5/4 1:39
 * @version:1.0
 * @author:
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    ViewDataBinding mDataBinding;
    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mDataBinding=binding;
    }

    public ViewDataBinding getBinding() {
        return mDataBinding;
    }
}
