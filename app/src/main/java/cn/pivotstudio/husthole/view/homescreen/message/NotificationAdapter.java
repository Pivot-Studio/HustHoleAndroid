package cn.pivotstudio.husthole.view.homescreen.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cn.pivotstudio.husthole.R;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NotificationBean> mNotificationList;
    Context context;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener listener;
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    public static final int ITEM_TYPE_BOTTOM = 2;
    private int mHeaderCount=1;//头部View个数
    private int mBottomCount=1;//底部View个数

    public String mContent = null;
    public interface OnItemClickListener {
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NotificationAdapter(Context context,List<NotificationBean> notificationList){
        mNotificationList = notificationList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    //内容长度
    public int getContentItemCount(){
        return mNotificationList.size();
    }

    //判断当前item是否是HeadView
    public boolean isHeaderView(int position) {
        return mHeaderCount != 0 && position < mHeaderCount;
    }
    //判断当前item是否是FooterView
    public boolean isBottomView(int position) {
        return mBottomCount != 0 && position >= (mHeaderCount + getContentItemCount());
    }

    public void getSystemNotification(String content){
        this.mContent=content;
    }

    @Override
    public int getItemViewType(int position) {
        int dataItemCount = getContentItemCount();
        if (mHeaderCount != 0 && position < mHeaderCount) {
            //头部View
            return ITEM_TYPE_HEADER;
        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {
            //底部View
            return ITEM_TYPE_BOTTOM;
        } else {
            //内容View
            return ITEM_TYPE_CONTENT;
        }
    }

    //内容 ViewHolder
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView alias;
        TextView type;
        TextView time;
        TextView content;
        TextView hole_id;
        LinearLayout linearLayout;
        RelativeLayout relativeLayout;

        public ContentViewHolder(View view) {
            super(view);
            alias = view.findViewById(R.id.name);
            type = view.findViewById(R.id.title);
            time = view.findViewById(R.id.time);
            content = view.findViewById(R.id.content);
            hole_id = view.findViewById(R.id.hole_id);
            linearLayout = view.findViewById(R.id.outer);
            relativeLayout = view.findViewById(R.id.relative_layout);
        }
    }
    //头部 ViewHolder
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView latestSystemNotification;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            latestSystemNotification = itemView.findViewById(R.id.latest_system_notification);
        }

    }
    //底部 ViewHolder
    public static class BottomViewHolder extends RecyclerView.ViewHolder {

        public BottomViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType ==ITEM_TYPE_HEADER) {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.notification_item_header,
                    parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT) {
            return  new ContentViewHolder(mLayoutInflater.inflate(R.layout.notification_item,
                    parent, false));
        } else if (viewType == ITEM_TYPE_BOTTOM) {
            return new BottomViewHolder(mLayoutInflater.inflate(R.layout.notification_item_footer,
                    parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder,int position) {
        //废
        /*if(position == mNotificationList.size() *//*||position > mNotificationList.size()*//*){
           holder.alias.setText("");
           holder.time.setText("");
           holder.type.setText("");
           holder.content.setText("");
           holder.hole_id.setText("");
           holder.linearLayout.setBackgroundColor(Color.parseColor("#F3F3F3"));
           holder.relativeLayout.setBackgroundColor(Color.parseColor("#F3F3F3"));

        }
        else {
        NotificationBean notification = mNotificationList.get(position);
        holder.alias.setText(notification.getAlias());
        holder.time.setText(notification.getTime());
        if (notification.getType().equals("0")) {
            holder.type.setText("评论了你的树洞");
        } else if (notification.getType().equals("1")) {
            holder.type.setText("回复了你的评论");
        }
        holder.content.setText(notification.getReplyContent());
        holder.hole_id.setText("# " + notification.getHole_id());
        }*/


        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder)holder).latestSystemNotification.setText(mContent);
        } else if (holder instanceof ContentViewHolder) {

            NotificationBean notification = mNotificationList.get(position-mHeaderCount);
            ((ContentViewHolder)holder).alias.setText(notification.getAlias());
            ((ContentViewHolder)holder).time.setText(notification.getTime());
            if (notification.getType().equals("0")) {
                ((ContentViewHolder)holder).type.setText("评论了你的树洞");
            } else if (notification.getType().equals("1")) {
                ((ContentViewHolder)holder).type.setText("回复了你的评论");
            }
            ((ContentViewHolder)holder).content.setText(notification.getReplyContent());
            ((ContentViewHolder)holder).hole_id.setText("# " + notification.getHole_id());

        } else if (holder instanceof BottomViewHolder) {
        }
        /*if(position == mNotificationList.size()-1){
            onBindViewHolder(holder,mNotificationList.size());
            Log.d(TAG, "onBindViewHolder: i am size " );
        }*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                    String TAG ="tag";
                    Log.d(TAG, "onClick: click "+position);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return mHeaderCount + getContentItemCount() + mBottomCount;
    }
}

