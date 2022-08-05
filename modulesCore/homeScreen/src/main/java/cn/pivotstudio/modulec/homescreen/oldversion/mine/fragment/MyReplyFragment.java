package cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.fragment.MyReplyItem;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.HoleStarReplyActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshFooter;
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshHeader;
import cn.pivotstudio.modulec.homescreen.oldversion.model.TimeCount;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.alibaba.android.arouter.launcher.ARouter;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyReplyFragment extends Fragment {

    private static final String BASE_URL = RetrofitManager.API;
    //    private final ArrayList<String[]> myList = new ArrayList<>();
    private final ArrayList<MyReplyItem> myList = new ArrayList<>();
    String TAG = "myReply";
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private RecyclerView myRecycleView;
    private int start_id = 0;
    private final int list_size = 20;
    private boolean isRefresh = false;
    private boolean finishRefresh = false;
    private boolean isOnLoadMore = false;
    private boolean finishOnLoadMore = false;

    public static MyReplyFragment newInstance() {
        return new MyReplyFragment();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View myReplyView = inflater.inflate(R.layout.fragment_myhole, container, false);

        RefreshLayout refreshLayout = myReplyView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new StandardRefreshFooter(getActivity()));

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);

        refreshLayout.setOnRefreshListener(mRefreshLayout -> {
            myList.clear();
            start_id = 0;
            isRefresh = true;
            update();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                /**
                 *要执行的操作
                 */
                if (finishRefresh) {
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                    finishRefresh = false;
                } else {
                    refreshLayout.autoRefresh();
                }
            }, 500);
            refreshLayout.finishRefresh(5000);
        });
        refreshLayout.setOnLoadMoreListener(mRefreshLayout -> {
            isOnLoadMore = true;
            start_id += list_size;
            Log.d(TAG, "onCreateView: start_id = " + start_id);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                /**
                 *要执行的操作
                 */
                if (finishOnLoadMore) {
                    refreshLayout.finishLoadMore();
                    isOnLoadMore = false;
                    finishOnLoadMore = false;
                } else {
                    refreshLayout.autoLoadMore();
                }
            }, 500);
            update();
            refreshLayout.finishLoadMore(5000);
        });

        myRecycleView = myReplyView.findViewById(R.id.myHoleRecyclerView);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (myList.size() == 0) {
            update();
        } else {
            myRecycleView.setAdapter(new CardsRecycleViewAdapter());
        }

        return myReplyView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        //update();
    }

    private void update() {
        Log.d(TAG, "in update");
        //加载纵向列表标题
        new Thread(() -> {
            Call<ResponseBody> call = request.myReplies(start_id, list_size);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String jsonStr = "null";
                    //myList.clear();
                    try {
                        if (response.body() != null) {
                            jsonStr = response.body().string();
                            Log.d(TAG, "this is myReplies reply: " + jsonStr);
                        }
                        jsonArray = new JSONArray(jsonStr);
                        //                            new DownLoadTask().execute();
                        try {
                            for (int f = 0; f < jsonArray.length(); f++) {
                                JSONObject sonObject = jsonArray.getJSONObject(f);
                                MyReplyItem singleHole =
                                    new MyReplyItem(sonObject.getString("alias"),
                                        sonObject.getString("content"),
                                        sonObject.getString("created_timestamp"),
                                        sonObject.getString("hole_content"),
                                        sonObject.getInt("hole_id"),
                                        sonObject.getInt("local_reply_id"));
                                myList.add(singleHole);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isRefresh) {
                            finishRefresh = true;
                        }

                        if (isOnLoadMore) {
                            myRecycleView.getAdapter().notifyDataSetChanged();
                            finishOnLoadMore = true;
                        } else {
                            myRecycleView.setAdapter(new CardsRecycleViewAdapter());
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                }
            });
        }).start();
    }

    public class CardsRecycleViewAdapter
        extends RecyclerView.Adapter<CardsRecycleViewAdapter.ViewHolder> {

        public CardsRecycleViewAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_myreply, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((ViewHolder) holder).bind(position);
        }

        @Override
        public int getItemCount() {
            return myList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private Boolean more_condition = false;
            private int position;
            private final TextView alias;
            private final TextView createdTimestamp;
            private final TextView content;
            private final TextView holeId;
            private final TextView holeContent;
            private final ImageView moreWhat;
            private final ConstraintLayout myDelete;
            private final ConstraintLayout myReplyTotal;

            public ViewHolder(View view) {

                super(view);

                alias = (TextView) view.findViewById(R.id.my_reply_alias);
                createdTimestamp = (TextView) view.findViewById(R.id.my_reply_created_timestamp);
                content = (TextView) view.findViewById(R.id.my_reply_content);
                holeId = (TextView) view.findViewById(R.id.my_reply_hole_id);
                holeContent = (TextView) view.findViewById(R.id.my_reply_hole_content);

                moreWhat = (ImageView) view.findViewById(R.id.my_reply_moreWhat);

                myReplyTotal = (ConstraintLayout) view.findViewById(R.id.my_reply_total);
                myDelete = (ConstraintLayout) view.findViewById(R.id.my_reply_delete);
                myDelete.setVisibility(View.GONE);
                moreWhat.setOnClickListener(v -> {
                    if (!more_condition) {
                        myDelete.setVisibility(View.VISIBLE);
                        more_condition = true;
                    } else {
                        myDelete.setVisibility(View.GONE);
                        more_condition = false;
                    }
                });
                myDelete.setOnClickListener(v -> {
                    View mView = View.inflate(getContext(), R.layout.dialog_delete, null);
                    // mView.setBackgroundResource(R.drawable.homepage_notice);
                    //设置自定义的布局
                    //mBuilder.setView(mView);
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(mView);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                    TextView no = (TextView) mView.findViewById(R.id.dialog_delete_tv_cancel);
                    TextView yes = (TextView) mView.findViewById(R.id.dialog_delete_tv_yes);
                    no.setOnClickListener(v12 -> {
                        myDelete.setVisibility(View.GONE);
                        more_condition = false;
                        dialog.dismiss();
                    });
                    yes.setOnClickListener(v1 -> {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Call<ResponseBody> call = request.delete_hole_2(RetrofitManager.API
                                    + "replies/"
                                    + myList.get(position).hole_id
                                    + "/"
                                    + myList.get(position).local_reply_id);//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        myDelete.setVisibility(View.GONE);
                                        more_condition = false;
                                        dialog.dismiss();
                                        if (response.code() == 200) {
                                            String json = "null";
                                            String returncondition = null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(getContext(), returncondition,
                                                        Toast.LENGTH_SHORT).show();
                                                    // Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                    myList.remove(position);
                                                    notifyDataSetChanged();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "删除失败，超过可删除的时间范围",
                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            //followCondition = false;
                                            String json = "null";
                                            String returncondition = null;
                                            if (response.errorBody() != null) {
                                                try {
                                                    json = response.errorBody().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(getContext(), returncondition,
                                                        Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                //FailureAction();
                                            } else {
                                                Toast.makeText(getContext(),
                                                    R.string.network_unknownfailture,
                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(),
                                                R.string.network_deletefailture, Toast.LENGTH_SHORT)
                                            .show();
                                        // mDeleteCondition=false;
                                    }
                                });
                            }
                        }).start();
                    });
                    dialog.show();
                });
                myReplyTotal.setOnClickListener(v -> {
                    Log.d(TAG, "现在跳转到评论界面。");
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(Constant.HOLE_ID, (myList.get(position)).hole_id)
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation((HoleStarReplyActivity) v.getContext(), 2);

                    // Intent intent = CommentListActivity.newIntent(getActivity(), null);
                    // intent.putExtra("data_hole_id", (myList.get(position)).hole_id+"");
                    // startActivity(intent);
                });
                myRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        myDelete.setVisibility(View.GONE);
                        more_condition = false;
                    }
                });
            }

            public void bind(int position) {
                this.position = position;

                alias.setText(myList.get(position).alias);
                content.setText(myList.get(position).content);

                createdTimestamp.setText(
                    TimeCount.replytime(myList.get(position).created_timestamp));
                holeContent.setText(myList.get(position).hole_content);
                holeId.setText("# " + myList.get(position).hole_id);
            }
        }
    }
}
