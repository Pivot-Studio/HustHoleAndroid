package com.example.hustholetest1.view.homescreen.mine.fragment;

import android.app.Dialog;
import android.content.Intent;
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

import com.example.hustholetest1.R;
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.StandardRefreshFooter;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyHoleFragment extends Fragment {

    private static final String BASE_URL = "http://husthole.pivotstudio.cn/api/";
    private final ArrayList<String[]> myHolesList = new ArrayList<>();
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private RecyclerView myRecycleView;

    private int start_id = 0;
    private int list_size = 20;
    private boolean isRefresh = false;
    private boolean finishRefresh = false;
    private boolean isOnLoadMore = false;
    private boolean finishOnLoadMore = false;

    String TAG = "myHole";

    public static MyHoleFragment newInstance() {
        return new MyHoleFragment();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myHoleView = inflater.inflate(R.layout.fragment_myhole, container, false);

        RefreshLayout refreshLayout = myHoleView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new StandardRefreshFooter(getActivity()));

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);

        refreshLayout.setOnRefreshListener(mRefreshLayout -> {
            myHolesList.clear();
            start_id = 0;
            isRefresh = true;
            update();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                /**
                 *要执行的操作
                 */
                if(finishRefresh){
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                    finishRefresh = false;
                }
                else {
                    refreshLayout.autoRefresh();
                }
            }, 500);
            refreshLayout.finishRefresh(5000);
        });
        refreshLayout.setOnLoadMoreListener(mRefreshLayout -> {
            isOnLoadMore = true;
            start_id = myHolesList.size()-1;
            Log.d(TAG, "onCreateView: start_id = "+start_id);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                /**
                 *要执行的操作
                 */
                if(finishOnLoadMore){
                    refreshLayout.finishLoadMore();
                    isOnLoadMore = false;
                    finishOnLoadMore = false;
                }
                else {
                    refreshLayout.autoLoadMore();
                }
            }, 500);
            update();
            refreshLayout.finishLoadMore(5000);
        });


        myRecycleView = myHoleView.findViewById(R.id.myHoleRecyclerView);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        update();

        return myHoleView;
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
        update();
    }

    private void update() {
        Log.d("myHole", "in update");
        //加载纵向列表标题
        new Thread(() -> {
            Call<ResponseBody> call = request.myHoles(0, 20);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = "null";
                    myHolesList.clear();
                    try {
                        if (response.body() != null) {
                            json = response.body().string();
                            Log.d("myHole", "this is myHoles reply: " + json);
                        }
                        jsonArray = new JSONArray(json);
//                            new DownLoadTask().execute();
                        try {
                            for (int f = 0; f < jsonArray.length(); f++) {
                                JSONObject sonObject = jsonArray.getJSONObject(f);
                                String[] SingleHole = new String[9];
                                SingleHole[1] = sonObject.getString("content");
                                SingleHole[2] = sonObject.getString("created_timestamp");
                                SingleHole[3] = sonObject.getInt("follow_num") + "";
                                SingleHole[4] = sonObject.getInt("hole_id") + "";
                                SingleHole[5] = sonObject.getBoolean("is_follow") + "";
                                SingleHole[6] = sonObject.getBoolean("is_thumbup") + "";
                                SingleHole[7] = sonObject.getInt("reply_num") + "";
                                SingleHole[8] = sonObject.getInt("thumbup_num") + "";
                                myHolesList.add(SingleHole);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(isRefresh) finishRefresh = true;

                        if(isOnLoadMore){
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


    public class CardsRecycleViewAdapter extends RecyclerView.Adapter<CardsRecycleViewAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private Boolean more_condition = false;
            private int position;
            //            private View totalView;
            private TextView ID, date, content, text_up, text_talk, text_star;
            private ImageView img_up,img_star;
            private ConstraintLayout myDelete;

            public ViewHolder(View view) {

                super(view);

                ID = (TextView) view.findViewById(R.id.hole_id);
                date = (TextView) view.findViewById(R.id.created_timestamp);
                content = (TextView) view.findViewById(R.id.content);

                text_up = (TextView) view.findViewById(R.id.text_up);
                text_talk = (TextView) view.findViewById(R.id.text_talk);
                text_star = (TextView) view.findViewById(R.id.text_star);

                img_up = (ImageView) view.findViewById(R.id.img_up);
                img_star = (ImageView) view.findViewById(R.id.img_star);
                ImageView moreWhat = (ImageView) view.findViewById(R.id.threePoint);

                myDelete = (ConstraintLayout) view.findViewById(R.id.delete);
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
                        Toast.makeText(getContext(), "举报成功", Toast.LENGTH_SHORT).show();
                        myHolesList.remove(position);
//                            notifyItemChanged(position);
                        notifyDataSetChanged();
                        myDelete.setVisibility(View.GONE);
                        more_condition = false;
                        dialog.dismiss();
                    });
                    dialog.show();
                });

                img_up.setOnClickListener(v -> {
                    if (CheckingToken.IfTokenExist()) {
                        if (myHolesList.get(position)[6].equals("false")) {
                            new Thread(() -> {
                                request = retrofit.create(RequestInterface.class);
                                Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + myHolesList.get(position)[4] + "/-1");//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        img_up.setImageResource(R.mipmap.active);
                                        myHolesList.get(position)[6] = "true";
                                        myHolesList.get(position)[8] = (Integer.parseInt(myHolesList.get(position)[8]) + 1) + "";
                                        text_up.setText(myHolesList.get(position)[8]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).start();
                        } else {
                            new Thread(() -> {
                                Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + myHolesList.get(position)[4] + "/-1");//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                Log.d(TAG,"取消点赞 ： " + response.body());
                                        img_up.setImageResource(R.mipmap.inactive);
                                        myHolesList.get(position)[6] = "false";
                                        myHolesList.get(position)[8] = (Integer.parseInt(myHolesList.get(position)[8]) - 1) + "";
                                        text_up.setText(myHolesList.get(position)[8]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "取消点赞失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).start();
                        }
                    } else {
                        Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                        startActivity(intent);
                    }
                });
                img_star.setOnClickListener(v -> {
                    if (CheckingToken.IfTokenExist()) {
                        if (myHolesList.get(position)[5].equals("false")) {
                            //加载纵向列表标题
                            new Thread(() -> {
                                Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/" + myHolesList.get(position)[4]);//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        img_star.setImageResource(R.mipmap.active_3);
                                        myHolesList.get(position)[5] = "true";
                                        myHolesList.get(position)[3] = (Integer.parseInt(myHolesList.get(position)[3]) + 1) + "";
                                        text_star.setText(myHolesList.get(position)[3]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "关注失败", Toast.LENGTH_SHORT).show();
                                        Log.d("", "关注失败");
                                    }
                                });
                            }).start();
                        } else {
                            new Thread(() -> {
                                Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/" + myHolesList.get(position)[4]);//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        img_star.setImageResource(R.mipmap.inactive_3);
                                        myHolesList.get(position)[5] = "false";
                                        myHolesList.get(position)[3] = (Integer.parseInt(myHolesList.get(position)[3]) - 1) + "";
                                        notifyDataSetChanged();
                                        text_star.setText(myHolesList.get(position)[3]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "取消关注失败", Toast.LENGTH_SHORT).show();
                                        Log.d("", "取消关注失败");
                                    }
                                });
                            }).start();
                        }
                    } else {
                        Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                        startActivity(intent);
                    }
                });
                content.setOnClickListener(v -> {
                    Log.d(TAG, "现在跳转到评论界面。");
                    Intent intent = CommentListActivity.newIntent(getActivity(), null);
                    intent.putExtra("data_hole_id", myHolesList.get(position)[4]);
                    startActivity(intent);
                });
            }

            public void bind(int position) {
                this.position = position;

                content.setText(myHolesList.get(position)[1]);
                date.setText(myHolesList.get(position)[2]);
                text_star.setText(myHolesList.get(position)[3]);
                ID.setText("# " + myHolesList.get(position)[4]);
                img_star.setImageResource(myHolesList.get(position)[5].equals("true") ? R.mipmap.active_3 : R.mipmap.inactive_3);
                img_up.setImageResource(myHolesList.get(position)[6].equals("true") ? R.mipmap.active : R.mipmap.inactive);
                text_talk.setText(myHolesList.get(position)[7]);
                text_up.setText(myHolesList.get(position)[8]);

            }

        }

        public CardsRecycleViewAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardsRecycleViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_myhole, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((CardsRecycleViewAdapter.ViewHolder) holder).bind(position);
        }

        @Override
        public int getItemCount() {
            return myHolesList.size();
        }

    }
}
