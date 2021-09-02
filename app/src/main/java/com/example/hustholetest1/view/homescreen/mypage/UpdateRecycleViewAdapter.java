package com.example.hustholetest1.view.homescreen.mypage;;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.R;

import java.util.List;

public class UpdateRecycleViewAdapter extends RecyclerView.Adapter<UpdateRecycleViewAdapter.ViewHolder>{
    private List<Update> updateList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View updateView;
        TextView version;
        TextView date;
        TextView detail;

        public ViewHolder(View view){
            super(view);
            updateView = view;
            version = (TextView) view.findViewById(R.id.version);
            date = (TextView) view.findViewById(R.id.created_timestamp);
            detail = (TextView)view.findViewById(R.id.detail);
        }
    }

    public UpdateRecycleViewAdapter(List<Update> updates){
        updateList = updates;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.update_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Update update = updateList.get(position);
        holder.version.setText(update.getVersion());
        holder.date.setText(update.getDate());
        holder.detail.setText(update.getDetail());
    }

    public int getItemCount() {
        return updateList.size();
    }
}

//public class RecycleViewAdapter1 extends RecyclerView.Adapter<RecycleViewAdapter1.ViewHolder> {
//    private List<Item1> Item1List;
//
//    static class ViewHolder extends RecyclerView.ViewHolder{
//        View      item1View;
//        ImageView image1;
//        TextView  item1Name;
//
//        public ViewHolder(View view){
//            super(view);
//            item1View = view;
//            image1 = (ImageView) view.findViewById(R.id.item1_image);
//            item1Name = (TextView) view.findViewById(R.id.item1_name);
//        }
//    }
//
//    public RecycleViewAdapter1(List<Item1> item1List){
//        Item1List = item1List;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item1,parent,false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Item1 item1 = Item1List.get(position);
//        holder.image1.setImageResource(item1.getImage1());
//        holder.item1Name.setText(item1.getItem1Name());
//    }
//
//    @Override
//    public int getItemCount() {
//        return Item1List.size();
//    }
//}
//
