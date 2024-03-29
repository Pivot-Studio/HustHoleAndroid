package cn.pivotstudio.moduleb.rebase.lib.util.ui;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @classname: SoftKeyBoardUtil
 * @description:
 * @date: 2022/5/6 20:35
 * @version:1.0
 * @author:
 */
public class SoftKeyBoardUtil {
    private View rootView;//activity的根视图
    int rootViewVisibleHeight;//纪录根视图的显示高度
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

    public SoftKeyBoardUtil(Activity activity) {
        //获取activity的根视图
        rootView = activity.getWindow().getDecorView();

        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //获取当前根视图在屏幕上显示的大小
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int visibleHeight = r.height();
                        if (rootViewVisibleHeight == 0) {
                            rootViewVisibleHeight = visibleHeight;
                            return;
                        }

                        //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                        if (rootViewVisibleHeight == visibleHeight) {
                            return;
                        }

                        //根视图显示高度变小超过200，可以看作软键盘显示了
                        if (rootViewVisibleHeight - visibleHeight > 200) {
                            if (onSoftKeyBoardChangeListener != null) {
                                onSoftKeyBoardChangeListener.keyBoardShow(
                                        rootViewVisibleHeight - visibleHeight);
                            }
                            rootViewVisibleHeight = visibleHeight;
                            return;
                        }

                        //根视图显示高度变大超过200，可以看作软键盘隐藏了
                        if (visibleHeight - rootViewVisibleHeight > 200) {
                            if (onSoftKeyBoardChangeListener != null) {
                                onSoftKeyBoardChangeListener.keyBoardHide(
                                        visibleHeight - rootViewVisibleHeight);
                            }
                            rootViewVisibleHeight = visibleHeight;
                        }
                    }
                });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow(int height);

        void keyBoardHide(int height);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity, EditText et) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void setListener(Activity activity,
                                   OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        SoftKeyBoardUtil softKeyBoardListener = new SoftKeyBoardUtil(activity);
        softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }
}
