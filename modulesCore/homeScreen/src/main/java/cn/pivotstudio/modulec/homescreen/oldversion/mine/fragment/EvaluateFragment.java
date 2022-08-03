package cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EvaluateFragment extends Fragment {
    private static final String BASE_URL = RetrofitManager.API;
    Button btn_ok;
    private Retrofit retrofit;
    private RequestInterface request;

    public static EvaluateFragment newInstance() {
        return new EvaluateFragment();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View EvaluateView = inflater.inflate(R.layout.fragment_evaluate, container, false);

        ChipGroup chipGroup = (ChipGroup) EvaluateView.findViewById(R.id.chip_group);
        btn_ok = EvaluateView.findViewById(R.id.ok);
        final String[] Score = { "" };
        final boolean[] oo = { false };
        Chip chip_1 = EvaluateView.findViewById(R.id.chip_1);
        Chip chip_2 = EvaluateView.findViewById(R.id.chip_2);
        Chip chip_3 = EvaluateView.findViewById(R.id.chip_3);
        Chip chip_4 = EvaluateView.findViewById(R.id.chip_4);
        Chip chip_5 = EvaluateView.findViewById(R.id.chip_5);
        Chip chip_6 = EvaluateView.findViewById(R.id.chip_6);
        Chip chip_7 = EvaluateView.findViewById(R.id.chip_7);
        Chip chip_8 = EvaluateView.findViewById(R.id.chip_8);
        Chip chip_9 = EvaluateView.findViewById(R.id.chip_9);
        Chip chip_10 = EvaluateView.findViewById(R.id.chip_10);
        //        chip_1.setOnClickListener(this::onClick);
        //现在问题  chipGroup 和 按钮的逻辑不会写。
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                if (chip_1.isChecked()
                    || chip_2.isChecked()
                    || chip_3.isChecked()
                    || chip_4.isChecked()
                    || chip_5.isChecked()
                    || chip_6.isChecked()
                    || chip_7.isChecked()
                    || chip_8.isChecked()
                    || chip_9.isChecked()
                    || chip_10.isChecked()) {
                    btn_ok.setBackgroundResource(R.drawable.button);
                    btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_100));
                } else {
                    btn_ok.setBackgroundResource(R.drawable.standard_button_gray);
                    btn_ok.setTextColor(getResources().getColor(R.color.GrayScale_20));
                }
                if (checkedId == R.id.chip_1) {
                    Score[0] = "1";
                } else if (checkedId == R.id.chip_2) {
                    Score[0] = "2";
                } else if (checkedId == R.id.chip_3) {
                    Score[0] = "3";
                } else if (checkedId == R.id.chip_4) {
                    Score[0] = "4";
                } else if (checkedId == R.id.chip_5) {
                    Score[0] = "5";
                } else if (checkedId == R.id.chip_6) {
                    Score[0] = "6";
                } else if (checkedId == R.id.chip_7) {
                    Score[0] = "7";
                } else if (checkedId == R.id.chip_8) {
                    Score[0] = "8";
                } else if (checkedId == R.id.chip_9) {
                    Score[0] = "9";
                } else if (checkedId == R.id.chip_10) {
                    Score[0] = "10";
                }
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip_1.isChecked()
                    || chip_2.isChecked()
                    || chip_3.isChecked()
                    || chip_4.isChecked()
                    || chip_5.isChecked()
                    || chip_6.isChecked()
                    || chip_7.isChecked()
                    || chip_8.isChecked()
                    || chip_9.isChecked()
                    || chip_10.isChecked()) {
                    new Thread(() -> {
                        RetrofitManager.RetrofitBuilder(BASE_URL);
                        retrofit = RetrofitManager.getRetrofit();
                        request = retrofit.create(RequestInterface.class);
                        Call<ResponseBody> call =
                            request.evaluate(BASE_URL + "feedback/score?score=" + Score[0]);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                try {
                                    String s = response.body().string();
                                    //                                Toast.makeText(getContext(),"is",Toast.LENGTH_SHORT).show();
                                    chip_1.setChecked(false);
                                    chip_2.setChecked(false);
                                    chip_3.setChecked(false);
                                    chip_4.setChecked(false);
                                    chip_5.setChecked(false);
                                    chip_6.setChecked(false);
                                    chip_7.setChecked(false);
                                    chip_8.setChecked(false);
                                    chip_9.setChecked(false);
                                    chip_10.setChecked(false);
                                    Toast.makeText(getContext(), "感谢您的评分！", Toast.LENGTH_SHORT)
                                        .show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            }
                        });
                    }).start();
                } else {
                    Toast.makeText(getContext(), "请先评分再提交！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return EvaluateView;
    }
}
