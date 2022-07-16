package cn.pivotstudio.modulec.homescreen.oldversion.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;


import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.AboutActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.DetailUpdateActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.EvaluateAndAdviceActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.HoleStarReplyActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.RulesActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.ScreenActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.SettingsActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.ShareCardActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MineFragment extends Fragment {

    private View rootView, shareCardView, backgroundView;
    private RelativeLayout settings, shield, rules, share, evaluate, about, update, logout, update2;
    private LinearLayout shareCard;
    private ConstraintLayout myHole, myStar, myReply;
    private TextView tv_joinDays, tv_myStarNum, tv_myHoleNum, tv_myReplyNum, cancel, location;
    private static String BASE_URL = RetrofitManager.API;
    private org.json.JSONArray jsonArray;
    private PopupWindow ppwBackground, ppwShare;

    Retrofit retrofit;
    RequestInterface request;
    String TAG = "isMine";
    int flag = 0;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.page4fragment, container, false);
        settings = rootView.findViewById(R.id.settings);
        shield = rootView.findViewById(R.id.shield);
        rules = rootView.findViewById(R.id.rules);
        share = rootView.findViewById(R.id.share);
        evaluate = rootView.findViewById(R.id.evaluateAndAdvice);
        //advice = rootView.findViewById(R.id.advice);
        about = rootView.findViewById(R.id.about);
        update = rootView.findViewById(R.id.update);
        //update2=rootView.findViewById(R.id.update2);
        logout = rootView.findViewById(R.id.logout);

        myHole = rootView.findViewById(R.id.my_hole);
        myStar = rootView.findViewById(R.id.my_star);
        myReply = rootView.findViewById(R.id.my_reply);

        tv_joinDays = rootView.findViewById(R.id.my_date);
        tv_myHoleNum = rootView.findViewById(R.id.my_hole_num);
        tv_myStarNum = rootView.findViewById(R.id.my_star_num);
        tv_myReplyNum = rootView.findViewById(R.id.my_reply_num);

        location = rootView.findViewById(R.id.ppw_share_location);

        shareCardView = LayoutInflater.from(getContext()).inflate(R.layout.ppw_share, null);
        // backgroundView = LayoutInflater.from(getContext()).inflate(R.layout.ppw_share_card_darkscreen, null);
        shareCard = shareCardView.findViewById(R.id.share_card);
        cancel = shareCardView.findViewById(R.id.share_cancel_button);

        ppwShare = new PopupWindow(shareCardView);
        ppwShare.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        ppwShare.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // ppwShare.setAnimationStyle(R.style.Page2Anim);

        //ppwBackground=new PopupWindow(backgroundView);
        // ppwBackground.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // ppwBackground.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //ppwBackground.setAnimationStyle(R.style.darkScreenAnim);

        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        getMyData();

        settings.setOnClickListener(this::onClick);
        shield.setOnClickListener(this::onClick);
        rules.setOnClickListener(this::onClick);
        share.setOnClickListener(this::onClick);
        evaluate.setOnClickListener(this::onClick);
        //advice.setOnClickListener(this::onClick);
        //advice.setOnClickListener(this::onClick);
        about.setOnClickListener(this::onClick);
        update.setOnClickListener(this::onClick);
        //  update2.setOnClickListener(this::onClick);
        logout.setOnClickListener(this::onClick);
        myHole.setOnClickListener(this::onClick);
        myStar.setOnClickListener(this::onClick);
        myReply.setOnClickListener(this::onClick);

       /* backgroundView.setOnClickListener(v -> {
            ppwShare.dismiss();
            ppwBackground.dismiss();

        });

        */
        cancel.setOnClickListener(v -> {

            ppwShare.dismiss();
            flag = 0;
            // ppwBackground.dismiss();
            cancelDarkBackGround();
        });
        shareCard.setOnClickListener(v -> {
            ppwShare.dismiss();
            flag = 0;
            cancelDarkBackGround();
            Intent intent = new Intent(getActivity(), ShareCardActivity.class);
            startActivity(intent);
        });

        getMyData();

        return rootView;
    }

    public void cancelDarkBackGround() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1f; // 0.0~1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private void getMyData() {
        new Thread(() -> {
            Call<ResponseBody> call = request.myData();//进行封装
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.errorBody() != null) {
//                             Log.d(TAG,"in error: " + response.errorBody());
                            // Log.d(TAG,"in error: " + response.errorBody().string());
                        }
                        if (response.body() != null) {
                            String jsonStr = response.body().string();
                            // Log.d(TAG,"1: " + response.body());
                            // Log.d(TAG,"2: " + jsonStr + "--");
//                                jsonArray=new org.json.JSONArray(jsonStr);
//                                JSONObject data = jsonArray.getJSONObject(0);
                            JSONObject data = new JSONObject(jsonStr);
                            int joinDays = data.getInt("join_days");
                            int myHoleNum = data.getInt("hole_sum");
                            int myStarNum = data.getInt("follow_num");
                            int myReplyNum = data.getInt("replies_num");
                            //  Log.d(TAG,joinDays+"");
                            String days = "我来到树洞已经<font color=\"#02A9F5\">" + joinDays + "</font>天啦。";
                            if (CheckingToken.IfTokenExist()) {
                                //Log.d(TAG,"已登陆");
                                tv_joinDays.setText(Html.fromHtml(days));
                                tv_myHoleNum.setText(String.valueOf(myHoleNum));
                                tv_myStarNum.setText(String.valueOf(myStarNum));
                                tv_myReplyNum.setText(String.valueOf(myReplyNum));
                            } else {
                                days = "我来到树洞已经<font color=\"#02A9F5\">" + 0 + "</font>天啦。";
                                tv_joinDays.setText(Html.fromHtml(days));
                                tv_myHoleNum.setText(String.valueOf(0));
                                tv_myStarNum.setText(String.valueOf(0));
                                tv_myReplyNum.setText(String.valueOf(0));
                            }
                        } else {
                            //Log.d(TAG,"is null");
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }).start();
    }

    public void onClick(View view) {
        Intent intent;
        int id = 0;
        if(flag == 0){
             id = view.getId();
        }
        if (id == R.id.my_hole) {
            if (CheckingToken.IfTokenExist()) {
                intent = new Intent(requireActivity().getApplicationContext(), HoleStarReplyActivity.class);
                intent.putExtra("initFragmentId", 0);
                startActivity(intent);
            } else {
                // intent = new Intent(getActivity(), EmailVerifyActivity.class);
                // startActivity(intent);
            }
        } else if (id == R.id.my_star) {
            if (CheckingToken.IfTokenExist()) {
                intent = new Intent(requireActivity().getApplicationContext(), HoleStarReplyActivity.class);
                intent.putExtra("initFragmentID", 1);
                startActivity(intent);
            } else {

            }
        } else if (id == R.id.my_reply) {
            if (CheckingToken.IfTokenExist()) {
                intent = new Intent(requireActivity().getApplicationContext(), HoleStarReplyActivity.class);
                intent.putExtra("initFragmentID", 2);
                startActivity(intent);
            } else {
//                intent = new Intent(getActivity(), EmailVerifyActivity.class);
//                startActivity(intent);
            }
        } else if (id == R.id.settings) {
            if (CheckingToken.IfTokenExist()) {
                intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            } else {
//                intent = new Intent(getActivity(), EmailVerifyActivity.class);
//                startActivity(intent);
            }
        } else if (id == R.id.shield) {
            intent = new Intent(getActivity().getApplicationContext(), ScreenActivity.class);
            startActivity(intent);
        } else if (id == R.id.rules) {
            intent = new Intent(getActivity().getApplicationContext(), RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.share) {
            flag = 1;
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.6f; // 0.0~1.0
            getActivity().getWindow().setAttributes(lp);
            //  ppwBackground.showAsDropDown(rootView);
            ppwShare.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        } else if (id == R.id.evaluateAndAdvice) {
            if (CheckingToken.IfTokenExist()) {
                intent = new Intent(requireActivity().getApplicationContext(), EvaluateAndAdviceActivity.class);
                intent.putExtra("initFragmentId", 0);
                startActivity(intent);
            } else {
//                intent = new Intent(getActivity(), EmailVerifyActivity.class);
//                startActivity(intent);
            }
        } else if (id == R.id.about) {
            intent = new Intent(getActivity().getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.update) {
            intent = new Intent(getActivity().getApplicationContext(), DetailUpdateActivity.class);
            startActivity(intent);
                /*case R.id.update2:
                intent = new Intent(getActivity().getApplicationContext(), Main3Activity.class);
                startActivity(intent);
                break;

             */
        } else if (id == R.id.logout) {
            Dialog dialog = new Dialog(getContext());
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_logout, null);
            dialog.setContentView(dialogView);
            Button btn_cancel = dialogView.findViewById(R.id.cancel);
            Button btn_logout = dialogView.findViewById(R.id.logout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    MMKVUtil mmkvUtil = MMKVUtil.getMMKVUtils(getContext());
                    // String token =mmkvUtil.getString("USER_TOKEN");
                    mmkvUtil.put("USER_TOKEN", "");
                    mmkvUtil.put(Constant.IS_LOGIN, false);
                    ARouter.getInstance().build("/loginAndRegister/WelcomeActivity").navigation();
                    ((HomeScreenActivity) getContext()).finish();
                }
            });
            dialog.show();
        }
    }
}
