package cn.pivotstudio.modulec.loginandregister.repository;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse;
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface;
import com.example.libbase.constant.Constant;

/**
 * @classname:VerificationCodeRepository
 * @description:
 * @date:2022/5/1 23:22
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class VerificationCodeRepository {
    public final MutableLiveData<String> failed = new MutableLiveData<>();
    public MutableLiveData<MsgResponse> mVerificationCode = new MutableLiveData<>();

    public VerificationCodeRepository() {
    }

    public void verifyEmailForNetwork(String id, String verify_code) {
        NetworkApi.createService(LRRequestInterface.class, 2)
            .verifyCode(Constant.BASE_URL
                + "auth/verifyCodeMatch?email="
                + id
                + "&verify_code="
                + verify_code)
            .compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
                @Override
                public void onSuccess(MsgResponse msgResponse) {
                    mVerificationCode.setValue(msgResponse);
                }

                @Override
                public void onFailure(Throwable e) {
                    failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                }
            }));
    }
}
