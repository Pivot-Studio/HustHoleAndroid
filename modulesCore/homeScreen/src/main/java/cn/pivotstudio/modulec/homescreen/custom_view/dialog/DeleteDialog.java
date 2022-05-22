package cn.pivotstudio.modulec.homescreen.custom_view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.custom_view.OptionsListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @classname:DeleteDIalog
 * @description:删除个人树洞的dialog
 * @date:2022/5/5 20:36
 * @version:1.0
 * @author:
 */
public class DeleteDialog extends Dialog {
    private OptionsListener mOptionsListener;

    public void setOptionsListener(OptionsListener mOptionsListener) {
        this.mOptionsListener = mOptionsListener;
    }

    public DeleteDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public DeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public DeleteDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    private void initView(){
        setContentView(R.layout.dialog_delete);
        getWindow().setBackgroundDrawableResource(R.drawable.notice);
        setCanceledOnTouchOutside(false);//触摸灰色部分无响应

        TextView no = (TextView)findViewById(R.id.dialog_delete_tv_cancel);
        TextView yes = (TextView)findViewById(R.id.dialog_delete_tv_yes);
        no.setOnClickListener(v12 -> {dismiss();});
        yes.setOnClickListener(v -> {
            dismiss();
            mOptionsListener.onClick(v);
        });
    }
}
