package cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.EmailActivity;
import cn.pivotstudio.modulec.homescreen.oldversion.model.SoftKeyBoardListener;
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdviceFragment extends Fragment {
    private static final String BASE_URL = RetrofitManager.API;
    private RequestInterface request;
    Boolean mSendCondition;
    private TextView length;
    private EditText et_advice;
    private Chip chipAdvice, chipBug, chipOthers;

    public static AdviceFragment newInstance() {
        return new AdviceFragment();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View AdviceView = inflater.inflate(R.layout.fragment_advice, container, false);
        ChipGroup chipGroup = AdviceView.findViewById(R.id.chip_group2);
        Button btn_ok = AdviceView.findViewById(R.id.btn_ok);
        et_advice = AdviceView.findViewById(R.id.et_advice_1);
        chipAdvice = AdviceView.findViewById(R.id.chip_advice);
        chipBug = AdviceView.findViewById(R.id.chip_bug);
        chipOthers = AdviceView.findViewById(R.id.chip_other);

        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final int[] type = { 0 };

        SoftKeyBoardListener.setListener(getActivity(),
            new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                @Override
                public void keyBoardShow(int height) {
                    et_advice.setHeight(600);
                    length.setPadding(0, 0, 0, 700);
                }

                @Override
                public void keyBoardHide(int height) {
                    et_advice.setHeight(900);
                    length.setPadding(0, 0, 0, 0);
                }
            });
        RetrofitManager.RetrofitBuilder(BASE_URL);
        Retrofit retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        length = AdviceView.findViewById(R.id.tv_advice_textnumber);

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String s = et_advice.getText().toString();
            if (s.length() >= 300) {
                Toast.makeText(getContext(), "输入内容过长！", Toast.LENGTH_SHORT).show();
            }
            if (s.length() > 0 && (chipAdvice.isChecked()
                || chipBug.isChecked()
                || chipOthers.isChecked())) {
                btn_ok.setBackgroundResource(R.drawable.advice_button_green);
                btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_100));
            } else {
                btn_ok.setBackgroundResource(R.drawable.advice_button);
                btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_20));
            }
            if (checkedId == R.id.chip_advice) {
                type[0] = 0;
            } else if (checkedId == R.id.chip_bug) {
                type[0] = 1;
            } else if (checkedId == R.id.chip_other) {
                type[0] = 2;
            }
        });
        btn_ok.setOnClickListener(v -> {
            String content = et_advice.getText().toString().replace("\n", "%0A");
            if ((chipAdvice.isChecked() || chipBug.isChecked() || chipOthers.isChecked())
                && content.length() > 0) {
                new Thread(() -> {
                    Call<ResponseBody> call = request.advice(
                        BASE_URL + "feedback?type=" + type[0] + "&content=" + content);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                mSendCondition = false;
                                Toast.makeText(getContext(), "提交成功！", Toast.LENGTH_SHORT).show();
                                InputMethodManager imm =
                                    (InputMethodManager) requireActivity().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                et_advice.setText("");
                                // 隐藏软键盘
                                imm.hideSoftInputFromWindow(
                                    getActivity().getWindow().getDecorView().getWindowToken(), 0);
                                et_advice.clearFocus();
                                chipAdvice.setChecked(false);
                                chipBug.setChecked(false);
                                chipOthers.setChecked(false);
                            } else {
                                mSendCondition = false;
                                ErrorMsg.getErrorMsg(response, getContext());
                                if (response.code() == 401) {
                                    //                                    Intent intent = new Intent(getContext(), EmailActivity.class);
                                    //                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        }
                    });
                }).start();
            } else if (chipAdvice.isChecked() || chipBug.isChecked() || chipOthers.isChecked()) {
                Toast.makeText(getContext(), "请输入建议再提交！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "请选择反馈类型再提交！", Toast.LENGTH_SHORT).show();
            }
        });
        et_advice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                length.setText(s.length() + "/300");
                if (s.length() >= 300) {
                    Toast.makeText(getContext(), "输入内容过长！", Toast.LENGTH_SHORT).show();
                }
                if (s.length() > 0 && (chipAdvice.isChecked()
                    || chipBug.isChecked()
                    || chipOthers.isChecked())) {
                    btn_ok.setBackgroundResource(R.drawable.advice_button_green);
                    btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_100));
                } else {
                    btn_ok.setBackgroundResource(R.drawable.advice_button);
                    btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_20));
                }
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    et_advice.setHeight(300);
                } else {
                    et_advice.setHeight(500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return AdviceView;
    }
}
