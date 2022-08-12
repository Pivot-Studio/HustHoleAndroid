package cn.pivotstudio.modulec.homescreen.oldversion.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.modulec.homescreen.R;
import java.util.List;

public class UpdateRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_Head = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    private static List<Update> updateList;

    public UpdateRecycleViewAdapter(List<Update> updates) {
        updateList = updates;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_Head;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.update_item, parent, false));
        } else if (viewType == ITEM_TYPE_Head) {
            return new HeadHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_updatehead, parent, false));
        }
        return null;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bind(position - 1);
        } else if (holder instanceof HeadHolder) {

            ((HeadHolder) holder).bind(position);
        }
    }

    public int getItemCount() {
        return updateList.size() + 1;
    }

    static class HeadHolder extends RecyclerView.ViewHolder {

        public HeadHolder(View view) {
            super(view);
        }

        public void bind(int position) {

        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View updateView;
        TextView version;
        TextView date;
        TextView detail;

        public ViewHolder(View view) {
            super(view);
            updateView = view;
            version = (TextView) view.findViewById(R.id.version);
            date = (TextView) view.findViewById(R.id.created_timestamp);
            detail = (TextView) view.findViewById(R.id.detail);
        }

        public void bind(int position) {
            Update update = updateList.get(position);
            version.setText(update.getVersion());
            date.setText(update.getDate());
            detail.setText(update.getDetail());
        }
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
