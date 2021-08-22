package com.example.hustholetest1.view.homescreen.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hustholetest1.R;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.mine.AboutActivity;
import com.example.hustholetest1.view.homescreen.mine.MyHoleAndMyStarActivity;
import com.example.hustholetest1.view.homescreen.mine.RulesActivity;
import com.example.hustholetest1.view.homescreen.mine.SettingsActivity;
import com.example.hustholetest1.view.homescreen.mine.UpdateActivity;
import com.example.hustholetest1.view.registerandlogin.activity.WelcomeActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDate;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MineFragment extends Fragment {

    private View rootView;
    private RelativeLayout settings, rules, evaluate, advice, about, update, logout;
    private LinearLayout myHole, myStar;
    private TextView tv_joinDays,tv_myStarNum,tv_myHoleNum;
    private static String BASE_URL = "http://husthole.pivotstudio.cn/api/";
    Retrofit retrofit;
    RequestInterface request;
    String TAG = "Mine";


    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.page4fragment, container, false);
        settings = rootView.findViewById(R.id.settings);
        rules = rootView.findViewById(R.id.rules);
        evaluate = rootView.findViewById(R.id.evaluate);
        advice = rootView.findViewById(R.id.advice);
        about = rootView.findViewById(R.id.about);
        update = rootView.findViewById(R.id.update);
        logout = rootView.findViewById(R.id.logout);
        myHole = rootView.findViewById(R.id.my_hole);
        myStar = rootView.findViewById(R.id.my_star);
        tv_joinDays = rootView.findViewById(R.id.my_date);
        tv_myHoleNum = rootView.findViewById(R.id.my_hole_num);
        tv_myStarNum = rootView.findViewById(R.id.my_star_num);

        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        getMyData();

        settings.setOnClickListener(this::onClick);
        rules.setOnClickListener(this::onClick);
        evaluate.setOnClickListener(this::onClick);
        advice.setOnClickListener(this::onClick);
        about.setOnClickListener(this::onClick);
        update.setOnClickListener(this::onClick);
        logout.setOnClickListener(this::onClick);
        myHole.setOnClickListener(this::onClick);
        myStar.setOnClickListener(this::onClick);

        return rootView;
    }

    private void getMyData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request.myData();//进行封装
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.body() != null) {
                                String jsonStr = response.body().string();
                                JSONObject data = JSON.parseObject(jsonStr);
                                int joinDays = data.getInteger("join_days");
                                int myHoleNum = data.getInteger("hole_sum");
                                int myStarNum = data.getInteger("follow_num");
                                Log.d(TAG,joinDays+"");
                                tv_joinDays.setText("我来到树洞已经" + joinDays +"天啦。");
                                tv_myHoleNum.setText( String.valueOf(myHoleNum));
                                tv_myStarNum.setText( String.valueOf(myStarNum));
                            }else{
                                Log.d(TAG,"is null");
                            }
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });
            }
        }).start();
    }
//    public void getMyHoleNum(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Call<ResponseBody> call = request.myHoles(0,20);//进行封装
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try {
//                                if (response.body() != null) {
//                                    String jsonStr = "null";
//                                    jsonStr = response.body().string();
//                                    JSONArray myHoleList = new JSONArray(jsonStr);
//                                int myHoleNum = myHoleList.length();
//                                tv_myHoleNum.setText( String.valueOf(myHoleNum));
//                            }
//                            }catch (JSONException | IOException e) {
//                                e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
//            }
//        }).start();
//    }
//    public void getMyStarNum(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Call<ResponseBody> call = request.myFollow(0,20);//进行封装
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            if (response.body() != null) {
//                                String jsonStr = "null";
//                                jsonStr = response.body().string();
//                                JSONArray myStarList = new JSONArray(jsonStr);
//                                int myStarNum = myStarList.length();
//                                tv_myStarNum.setText( String.valueOf(myStarNum));
//                            }
//                        }catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                    }
//                });
//            }
//        }).start();
//    }
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.my_hole:
                intent = new Intent(getActivity().getApplicationContext(), MyHoleAndMyStarActivity.class);
                intent.putExtra("initFragmentId",0);
                startActivity(intent);
            case R.id.my_star:
                intent = new Intent(getActivity().getApplicationContext(), MyHoleAndMyStarActivity.class);
                intent.putExtra("initFragmentID",1);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.rules:
                intent = new Intent(getActivity().getApplicationContext(), RulesActivity.class);
                startActivity(intent);
                break;
//            case R.id.advice:
//                intent = new Intent(getActivity().getApplicationContext(), AdviceActivity.class);
//                startActivity(intent);
//                break;
            case R.id.about:
                intent = new Intent(getActivity().getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.update:
                intent = new Intent(getActivity().getApplicationContext(), UpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_logout, null);
                Button btn_cancel = dialogView.findViewById(R.id.cancel);
                Button btn_logout = dialogView.findViewById(R.id.logout);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();
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
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("Depository", Context.MODE_PRIVATE).edit();//获取编辑器
                        editor.putString("token", "");
                        editor.commit();
                        Intent intent=new Intent(getContext(), WelcomeActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();
                break;
        }
    }
}
