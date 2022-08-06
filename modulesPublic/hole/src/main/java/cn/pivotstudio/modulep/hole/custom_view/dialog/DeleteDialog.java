package cn.pivotstudio.modulep.hole.custom_view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.custom_view.OptionsListener;


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
        getWindow().setBackgroundDrawableResource(R.drawable.ic_notice);
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
