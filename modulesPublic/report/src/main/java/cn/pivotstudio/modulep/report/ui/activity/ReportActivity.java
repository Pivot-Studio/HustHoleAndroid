package cn.pivotstudio.modulep.report.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.base.ui.activity.BaseActivity;
import com.example.libbase.constant.Constant;
import com.example.libbase.util.ui.EditTextUtil;
import com.githang.statusbar.StatusBarCompat;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulep.report.BuildConfig;
import cn.pivotstudio.modulep.report.R;
import cn.pivotstudio.modulep.report.databinding.ActivityReportBinding;
import cn.pivotstudio.modulep.report.model.MsgResponse;
import cn.pivotstudio.modulep.report.network.ReportRequestInterface;
import cn.pivotstudio.modulep.report.viewmodel.ReportViewModel;
import io.reactivex.Observable;

/**
 * @classname:ReportActivity
 * @description:
 * @date:2022/5/18 14:50
 * @version:1.0
 * @author:
 */

@Route(path="/report/ReportActivity")
public class ReportActivity extends BaseActivity {
    @Autowired(name= Constant.HOLE_ID)
    int hole_id;
    @Autowired(name= Constant.REPLY_LOCAL_ID)
    int reply_local_id;
    @Autowired(name= Constant.ALIAS)
    String alias;

    String reportType;
    Button lastClickBtn;
    ReportViewModel mViewModel;
    ActivityReportBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);//初始化@Autowired
        initView();
    }
    private void initView(){
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        mViewModel=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(ReportViewModel.class);
        mBinding.tvReportHoleid.setText("#"+hole_id);
        mBinding.tvAlias.setText(alias);
        mBinding.tvTitlebargreenTitle.setText("举报");
        mBinding.titlebargreenAVLoadingIndicatorView.hide();
        mBinding.titlebargreenAVLoadingIndicatorView.setVisibility(View.GONE);
        mBinding.clTitlebargreenBack.setOnClickListener(this::onClick);

        EditTextUtil.ButtonReaction(mBinding.etReport,mBinding.btnReport);
    }
    public void reportForNetwork(){
        String content="举报类型：\n"+reportType+"\n"+"举报描述：\n"+mBinding.etReport.getText().toString();

        Observable<MsgResponse> observable;
            observable=NetworkApi.createService(ReportRequestInterface.class, 2).report(Constant.BASE_URL +"reports?hole_id=" + hole_id + "&reply_local_id="+reply_local_id+ "&content="+content);
        observable.compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msg) {
                showMsg(msg.getMsg());
                finish();
            }

            @Override
            public void onFailure(Throwable e) {
                showMsg(((ExceptionHandler.ResponseThrowable) e).message);
                finish();
            }
        }));
    }
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.et_report){

        }else if(id==R.id.btn_report){
                if(reportType!=null){
                    reportForNetwork();
                }else{
                    showMsg("您还未选择举报类型");
                }
                closeKeyBoard();
        }else if(id==R.id.cl_titlebargreen_back){
            if (BuildConfig.isRelease) {
                finish();
                closeKeyBoard();
            } else {
                showMsg("当前处于模块测试阶段");
            }
        }else{
            if(view instanceof Button){
                if(lastClickBtn!=null) {
                    lastClickBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.report_button));
                }
                view.setBackground(AppCompatResources.getDrawable(this,R.drawable.report_button_green));
                lastClickBtn=(Button)view;
                reportType=((Button)view).getText().toString();
            }
        }
    }
}
