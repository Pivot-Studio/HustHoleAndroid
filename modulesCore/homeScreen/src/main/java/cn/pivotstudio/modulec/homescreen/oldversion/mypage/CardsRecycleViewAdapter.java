package cn.pivotstudio.modulec.homescreen.oldversion.mypage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.pivotstudio.modulec.homescreen.R;


public class CardsRecycleViewAdapter extends RecyclerView.Adapter<CardsRecycleViewAdapter.ViewHolder>{
    private List<Card> mCards;
//    Boolean isUp,isStar;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View totalView;
        TextView  ID,date,content,text_up,text_talk,text_star;
        ImageView img_up, img_talk, img_star, threePoint;


        public ViewHolder(View view){
            super(view);
            totalView = view;

            ID = (TextView) view.findViewById(R.id.hole_id);
            date = (TextView) view.findViewById(R.id.created_timestamp);
            content = (TextView) view.findViewById(R.id.content);

            text_up = (TextView) view.findViewById(R.id.text_up);
            text_talk = (TextView) view.findViewById(R.id.text_talk);
            text_star = (TextView) view.findViewById(R.id.text_star);

            img_up = (ImageView) view.findViewById(R.id.img_up);
            img_talk = (ImageView) view.findViewById(R.id.img_talk);
            img_star = (ImageView) view.findViewById(R.id.img_star);
            threePoint = (ImageView) view.findViewById(R.id.threePoint);
        }

    }

    public CardsRecycleViewAdapter(List<Card> cards){
        mCards = cards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_myhole,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.img_up.setOnClickListener(new View.OnClickListener() {
            Boolean isUp = false;
            Integer up_num;
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Card card = mCards.get(position);
                up_num = Integer.parseInt(card.up);
                if(isUp){
                    holder.img_up.setImageResource(R.mipmap.inactive);
                    holder.text_up.setText((up_num - 1) + "");
                    notifyDataSetChanged();
                    isUp = false;
                }else{
                    holder.img_up.setImageResource(R.mipmap.active);
                    holder.text_up.setText((up_num + 1) + "");
                    isUp = true;
                }

            }
        });
        holder.img_star.setOnClickListener(new View.OnClickListener() {
            Boolean isStar = false;
            Integer star_num;
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Card card = mCards.get(position);
                star_num = Integer.parseInt(card.star);
                if(isStar){
                    holder.img_star.setImageResource(R.mipmap.inactive_3);
                    holder.text_star.setText((star_num - 1) + "");
                    isStar = false;
                }else{
                    holder.img_star.setImageResource(R.mipmap.active_3);
                    holder.text_star.setText((star_num + 1) + "");
                    isStar = true;
                }
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = mCards.get(position);
        holder.ID.setText(card.getID()+"");
        holder.date.setText(card.getDate()+"");
        holder.content.setText(card.getContent()+"");
        holder.text_up.setText(card.getUp()+"");
        holder.text_talk.setText(card.getTalk()+"");
        holder.text_star.setText(card.getStar()+"");
        holder.img_up.setImageResource(R.mipmap.inactive);
        holder.img_talk.setImageResource(R.mipmap.inactive_2);
        holder.img_star.setImageResource(R.mipmap.inactive_3);
        holder.threePoint.setImageResource(R.mipmap.threepoint);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }
}
//public class RecycleViewAdapter1 extends RecyclerView.Adapter<RecycleViewAdapter1.ViewHolder> {
//    private List<Item1> Item1List;
//
//    static class ViewHolder extends RecyclerView.ViewHolder{
//        View item1View;
//        ImageView image1;
//        TextView item1Name;
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