package cn.pivotstudio.modulec.loginandregister.repository;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse;
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface;
import java.util.HashMap;

/**
 * @classname:ForgetPasswordRepository
 * @description:
 * @date:2022/5/1 22:23
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class ForgetPasswordRepository {
    public final MutableLiveData<String> failed = new MutableLiveData<>();
    public MutableLiveData<MsgResponse> mForgetPassword = new MutableLiveData<>();

    public ForgetPasswordRepository() {
    }

    public void verifyEmailForNetwork(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", id);
        map.put("isResetPassword", "true");
        NetworkApi.createService(LRRequestInterface.class, 2)
            .verifyCodeMatch(map)
            .compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
                @Override
                public void onSuccess(MsgResponse msgResponse) {
                    mForgetPassword.setValue(msgResponse);
                }

                @Override
                public void onFailure(Throwable e) {

                    failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                }
            }));
    }
}
