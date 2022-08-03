package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.EditTextReaction;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.donkingliang.labels.LabelsView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SetScreenKeyWordActivity extends AppCompatActivity {
    private static final String ACTIVITY_TAG = "LogDemo";
    private JSONArray jsonArray;
    private LabelsView labelsView;
    private EditText et_label;
    private ImageView screen_keyword_img;
    private TextView addButton, number;
    private ConstraintLayout constraintLayout1_label, constraintLayout2_label;
    private final ArrayList<String> label = new ArrayList<>();
    private final Boolean mSetCondition = false;
    private Retrofit retrofit;
    private RequestInterface request;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.item_label);
        et_label = findViewById(R.id.et_label);
        addButton = findViewById(R.id.tv_addButton);
        number = (TextView) findViewById(R.id.tv_label_sheildnumber);
        constraintLayout1_label = findViewById(R.id.constraintLayout1_label);
        constraintLayout2_label = findViewById(R.id.constraintLayout2_label);
        constraintLayout2_label.setVisibility(View.INVISIBLE);
        labelsView = findViewById(R.id.labels);

        //mOnlyMaster.setBackground(getDrawable(R.drawable.forest_button));
        //mOnlyMaster.setText("加入");
        ///labelsView .setTextColor(getResources().getColor(R.color.HH_BandColor_3));

        screen_keyword_img = findViewById(R.id.screen_keyword_img);
        screen_keyword_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        retrofit = RetrofitManager.getRetrofit();
        request = RetrofitManager.getRequest();

        SpannableString string1 = new SpannableString("请尽可能简单地输入您想屏蔽的关键词");
        EditTextReaction.EditTextSize(et_label, string1, 14);

        constraintLayout1_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayout1_label.setVisibility(View.INVISIBLE);
                constraintLayout2_label.setVisibility(View.VISIBLE);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {//加载纵向列表标题
                    @Override
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("word", et_label.getText().toString());
                        Call<ResponseBody> call;
                        call = request.addblockword(
                            RetrofitManager.API + "blockwords?word=" + et_label.getText()
                                .toString());
                        // call = request.addblockword(map);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    String json = null;
                                    String returncondition = null;
                                    try {
                                        if (response.body() != null) {
                                            json = response.body().string();
                                        }
                                        JSONObject jsonObject = new JSONObject(json);
                                        returncondition = jsonObject.getString("msg");
                                        Toast.makeText(SetScreenKeyWordActivity.this,
                                            returncondition, Toast.LENGTH_SHORT).show();
                                        label.add(et_label.getText().toString() + "  ×");
                                        labelsView.setLabels(label);
                                        number.setText("(" + label.size() + "/5)");
                                        et_label.setText("");
                                        constraintLayout1_label.setVisibility(View.VISIBLE);
                                        constraintLayout2_label.setVisibility(View.INVISIBLE);
                                        et_label.setFocusable(true);
                                        et_label.setFocusableInTouchMode(true);
                                        et_label.requestFocus();
                                        InputMethodManager imm =
                                            (InputMethodManager) et_label.getContext()
                                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String json = "null";
                                    String returncondition = null;
                                    if (response.errorBody() != null) {
                                        try {
                                            json = response.errorBody().string();
                                            JSONObject jsonObject = new JSONObject(json);
                                            returncondition = jsonObject.getString("msg");
                                            Toast.makeText(SetScreenKeyWordActivity.this,
                                                returncondition, Toast.LENGTH_SHORT).show();
                                        } catch (IOException | JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(SetScreenKeyWordActivity.this,
                                                R.string.network_unknownfailture, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                Toast.makeText(SetScreenKeyWordActivity.this,
                                    R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

                //网络层

                //视图

            }
        });

        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label_text, Object data, int position) {
                //label是被点击的标签，data是标签所对应的数据，position是标签的位置。

                View mView = View.inflate(getApplicationContext(), R.layout.dialog_screen, null);
                Dialog dialog = new Dialog(SetScreenKeyWordActivity.this);
                dialog.setContentView(mView);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                TextView no = (TextView) mView.findViewById(R.id.tv_dialog_screen_notquit);
                TextView yes = (TextView) mView.findViewById(R.id.tv_dialog_screen_quit);
                TextView content = (TextView) mView.findViewById(R.id.tv_dialog_screen_content);
                content.setText("你确认要删除对关键词\"" + label.get(position)
                    .substring(0, label.get(position).length() - 3) + "\"的屏蔽吗？");
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {//加载纵向列表标题
                            @Override
                            public void run() {
                                Call<ResponseBody> call;
                                call = request.deleteblockword(
                                    RetrofitManager.API + "blockwords?word=" + label.get(position)
                                        .substring(0, label.get(position).length() - 3));
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call,
                                                           Response<ResponseBody> response) {
                                        if (response.code() == 200) {
                                            String json = null;
                                            String returncondition = null;
                                            dialog.dismiss();
                                            try {
                                                if (response.body() != null) {
                                                    json = response.body().string();
                                                }
                                                JSONObject jsonObject = new JSONObject(json);
                                                returncondition = jsonObject.getString("msg");
                                                Toast.makeText(SetScreenKeyWordActivity.this,
                                                    returncondition, Toast.LENGTH_SHORT).show();
                                                label.remove(label.get(position));
                                                labelsView.setLabels(label);
                                                number.setText("(" + label.size() + "/5)");
                                            } catch (IOException | JSONException e) {

                                                e.printStackTrace();
                                            }
                                        } else {
                                            dialog.dismiss();
                                            String json = "null";
                                            String returncondition = null;
                                            if (response.errorBody() != null) {
                                                try {
                                                    json = response.errorBody().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(SetScreenKeyWordActivity.this,
                                                        returncondition, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(SetScreenKeyWordActivity.this,
                                                    R.string.network_unknownfailture,
                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                        Toast.makeText(SetScreenKeyWordActivity.this,
                                                R.string.network_loadfailure, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                dialog.show();
            }
        });
        upDate();
        initListener();
    }

    private void upDate() {
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {

                Call<ResponseBody> call;
                call = request.blockwords(RetrofitManager.API + "blockwords");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }

                                JSONArray jsonArray = new JSONArray(json);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject sonObject2 = jsonArray.getJSONObject(i);
                                    label.add(sonObject2.getString("word") + "  ×");
                                }
                                labelsView.setLabels(label);
                                number.setText("(" + label.size() + "/5)");
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String json = "null";
                            String returncondition = null;
                            if (response.errorBody() != null) {
                                try {
                                    json = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(json);
                                    returncondition = jsonObject.getString("msg");
                                    Toast.makeText(SetScreenKeyWordActivity.this, returncondition,
                                        Toast.LENGTH_SHORT).show();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(SetScreenKeyWordActivity.this,
                                    R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        Toast.makeText(SetScreenKeyWordActivity.this, R.string.network_loadfailure,
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void initListener() {
        et_label.addTextChangedListener(new MyTextWatcher());
    }

    public void update() {
        //网络层
        label.clear();//先清空
        try {
            for (int f = 0; f < jsonArray.length(); f++) {
                JSONObject sonObject = jsonArray.getJSONObject(f);
                label.add(sonObject.getString("word"));
            }
            labelsView.setLabels(label);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //监听字数
    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_label.getText().toString().length() == 7) {
                View mView = View.inflate(getApplicationContext(), R.layout.dialog_screen, null);
                Dialog dialog = new Dialog(SetScreenKeyWordActivity.this);
                dialog.setContentView(mView);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                TextView no = (TextView) mView.findViewById(R.id.tv_dialog_screen_notquit);
                no.setVisibility(View.INVISIBLE);
                TextView yes = (TextView) mView.findViewById(R.id.tv_dialog_screen_quit);
                TextView content = (TextView) mView.findViewById(R.id.tv_dialog_screen_content);
                content.setText("关键词长度不得超过7个字符，请重新添加！");
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

