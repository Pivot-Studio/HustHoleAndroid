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
import cn.pivotstudio.modulec.homescreen.oldversion.mine.HoleStarReplyActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken;
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshFooter;
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshHeader;
import cn.pivotstudio.modulec.homescreen.oldversion.model.TimeCount;
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg;
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

public class MyStarFragment extends Fragment {

    private static final String BASE_URL = RetrofitManager.API;
    String TAG = "myStar";
    private final ArrayList<String[]> myStarsList = new ArrayList<>();
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

    public static MyStarFragment newInstance() {
        return new MyStarFragment();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View myStarView = inflater.inflate(R.layout.fragment_mystar, container, false);

        myRecycleView = myStarView.findViewById(R.id.myStarRecyclerView);
        Log.d(TAG, "this is my star fragment");

        RefreshLayout refreshLayout = myStarView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new StandardRefreshFooter(getActivity()));

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);

        refreshLayout.setOnRefreshListener(mRefreshLayout -> {
            myStarsList.clear();
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

        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (myStarsList.size() == 0) {
            update();
        } else {
            myRecycleView.setAdapter(new CardsRecycleViewAdapter());
        }
        return myStarView;
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
            Call<ResponseBody> call = request.myFollow(start_id, list_size);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = "null";
                    //myStarsList.clear();
                    try {
                        if (response.body() != null) {
                            json = response.body().string();
                            Log.d(TAG, "this is myStars reply: " + json);
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
                                myStarsList.add(SingleHole);
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
                .inflate(R.layout.card_myfollow, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((ViewHolder) holder).bind(position);
        }

        @Override
        public int getItemCount() {
            return myStarsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Boolean more_condition = false;
            int position;
            TextView ID, date, content, text_up, text_talk, text_star;
            ImageView img_up, img_talk, img_star, moreWhat;
            ConstraintLayout myInform;
            View totalView;

            public ViewHolder(View view) {

                super(view);

                totalView = view.findViewById(R.id.my_follow_total);
                ID = (TextView) view.findViewById(R.id.hole_id);
                date = (TextView) view.findViewById(R.id.created_timestamp);
                content = (TextView) view.findViewById(R.id.content);

                text_up = (TextView) view.findViewById(R.id.text_up);
                text_talk = (TextView) view.findViewById(R.id.text_talk);
                text_star = (TextView) view.findViewById(R.id.text_star);

                img_up = (ImageView) view.findViewById(R.id.img_up);
                img_star = (ImageView) view.findViewById(R.id.img_star);
                img_talk = (ImageView) view.findViewById(R.id.img_talk);
                moreWhat = (ImageView) view.findViewById(R.id.threePoint);

                myInform = (ConstraintLayout) view.findViewById(R.id.inform);
                myInform.setVisibility(View.GONE);
                moreWhat.setOnClickListener(v -> {
                    if (!more_condition) {
                        myInform.setVisibility(View.VISIBLE);
                        more_condition = true;
                    } else {
                        myInform.setVisibility(View.GONE);
                        more_condition = false;
                    }
                });
                myInform.setOnClickListener(v -> {
                    View mView = View.inflate(getContext(), R.layout.dialog_inform, null);
                    // mView.setBackgroundResource(R.drawable.homepage_notice);
                    //设置自定义的布局
                    //mBuilder.setView(mView);
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(mView);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                    TextView no = (TextView) mView.findViewById(R.id.dialog_inform_tv_cancel);
                    TextView yes = (TextView) mView.findViewById(R.id.dialog_inform_tv_yes);
                    no.setOnClickListener(v1 -> {
                        myInform.setVisibility(View.GONE);
                        more_condition = false;
                        dialog.dismiss();
                    });
                    yes.setOnClickListener(v12 -> {
                        new Thread(new Runnable() {//加载纵向列表标题
                            @Override
                            public void run() {
                                Call<ResponseBody> call = request.report_2(
                                    RetrofitManager.API + "reports?hole_id=" + myStarsList.get(
                                        position)[4] + "&reply_local_id= -1");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        dialog.dismiss();
                                        myInform.setVisibility(View.GONE);
                                        more_condition = false;
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
                                                    //Toast.makeText(getContext(),"举报成功",Toast.LENGTH_SHORT).show();

                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {

                                                Toast.makeText(getContext(),
                                                    "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报",
                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            ErrorMsg.getErrorMsg(response, getContext());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(),
                                                R.string.network_reportfailture, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                });
                            }
                        }).start();
                    });
                    dialog.show();
                });
                img_up.setOnClickListener(v -> {
                    if (CheckingToken.IfTokenExist()) {
                        if (myStarsList.get(position)[6].equals("false")) {
                            new Thread(() -> {
                                request = retrofit.create(RequestInterface.class);
                                Call<ResponseBody> call = request.thumbups(RetrofitManager.API
                                    + "thumbups/"
                                    + myStarsList.get(position)[4]
                                    + "/-1");//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        img_up.setImageResource(R.mipmap.active);
                                        myStarsList.get(position)[6] = "true";
                                        myStarsList.get(position)[8] =
                                            (Integer.parseInt(myStarsList.get(position)[8]) + 1)
                                                + "";
                                        text_up.setText(myStarsList.get(position)[8]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                });
                            }).start();
                        } else {
                            new Thread(() -> {
                                Call<ResponseBody> call = request.deletethumbups(RetrofitManager.API
                                    + "thumbups/"
                                    + myStarsList.get(position)[4]
                                    + "/-1");//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        //                                                Log.d(TAG,"取消点赞 ： " + response.body());
                                        img_up.setImageResource(R.mipmap.inactive);
                                        myStarsList.get(position)[6] = "false";
                                        myStarsList.get(position)[8] =
                                            (Integer.parseInt(myStarsList.get(position)[8]) - 1)
                                                + "";
                                        text_up.setText(myStarsList.get(position)[8]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "取消点赞失败", Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                });
                            }).start();
                        }
                    } else {
                        //   Intent intent=new Intent(getContext(), EmailVerifyActivity.class);
                        //   startActivity(intent);
                    }
                });
                img_talk.setOnClickListener(v -> {
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(Constant.HOLE_ID, Integer.valueOf(myStarsList.get(position)[4]))
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, true)
                        .navigation((HoleStarReplyActivity) v.getContext(), 2);
                    //                    Intent intent = CommentListActivity.newIntent(getActivity(), null);
                    //                    intent.putExtra("reply","key_board");
                    //                    intent.putExtra("data_hole_id", myStarsList.get(position)[4]);
                    //                    startActivity(intent);
                });
                img_star.setOnClickListener(v -> {
                    if (CheckingToken.IfTokenExist()) {
                        if (myStarsList.get(position)[5].equals("false")) {
                            //加载纵向列表标题
                            new Thread(() -> {
                                Call<ResponseBody> call = request.follow(
                                    RetrofitManager.API + "follows/" + myStarsList.get(
                                        position)[4]);//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        img_star.setImageResource(R.mipmap.active_3);
                                        myStarsList.get(position)[5] = "true";
                                        myStarsList.get(position)[3] =
                                            (Integer.parseInt(myStarsList.get(position)[3]) + 1)
                                                + "";
                                        text_star.setText(myStarsList.get(position)[3]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "关注失败", Toast.LENGTH_SHORT)
                                            .show();
                                        Log.d("", "关注失败");
                                    }
                                });
                            }).start();
                        } else {
                            new Thread(() -> {
                                Call<ResponseBody> call = request.deletefollow(
                                    RetrofitManager.API + "follows/" + myStarsList.get(
                                        position)[4]);//进行封装
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        img_star.setImageResource(R.mipmap.inactive_3);
                                        myStarsList.get(position)[5] = "false";
                                        myStarsList.get(position)[3] =
                                            (Integer.parseInt(myStarsList.get(position)[3]) - 1)
                                                + "";
                                        notifyDataSetChanged();
                                        text_star.setText(myStarsList.get(position)[3]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(), "取消关注失败", Toast.LENGTH_SHORT)
                                            .show();
                                        Log.d("", "取消关注失败");
                                    }
                                });
                            }).start();
                        }
                    } else {
                        //  Intent intent=new Intent(getContext(), EmailVerifyActivity.class);
                        //  startActivity(intent);
                    }
                });
                totalView.setOnClickListener(v -> {
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(Constant.HOLE_ID, Integer.valueOf(myStarsList.get(position)[4]))
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation((HoleStarReplyActivity) v.getContext(), 2);
                    //                    Log.d("data[2]1", myStarsList.get(position)[2]);
                    //                    Intent intent= CommentListActivity.newIntent(getActivity(), null);
                    //                    intent.putExtra("data_hole_id",myStarsList.get(position)[4]);
                    //                    Log.d("holeid",myStarsList.get(position)[4]);
                    //                    startActivity(intent);
                });
                myRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        myInform.setVisibility(View.GONE);
                        more_condition = false;
                    }
                });
            }

            public void bind(int position) {
                this.position = position;
                content.setText(myStarsList.get(position)[1]);
                date.setText(TimeCount.time(myStarsList.get(position)[2]));
                text_star.setText(myStarsList.get(position)[3]);
                ID.setText("# " + myStarsList.get(position)[4]);
                img_star.setImageResource(
                    myStarsList.get(position)[5].equals("true") ? R.mipmap.active_3
                        : R.mipmap.inactive_3);
                img_up.setImageResource(
                    myStarsList.get(position)[6].equals("true") ? R.mipmap.active
                        : R.mipmap.inactive);
                text_talk.setText(myStarsList.get(position)[7]);
                text_up.setText(myStarsList.get(position)[8]);
            }
        }
    }
}
