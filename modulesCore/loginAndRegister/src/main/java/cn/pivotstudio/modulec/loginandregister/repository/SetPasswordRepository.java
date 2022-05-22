package cn.pivotstudio.modulec.loginandregister.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;

import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;

import cn.pivotstudio.modulec.loginandregister.model.MsgResponse;

import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface;

/**
 * @classname:SetPasswordRepository
 * @description:
 * @date:2022/5/1 21:33
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class SetPasswordRepository {
    public final MutableLiveData<String> failed = new MutableLiveData<>();
    public MutableLiveData<MsgResponse> mSetPassword = new MutableLiveData<>();
    public SetPasswordRepository() {
    }
    public void createAccountForNetwork(String id, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", id);
        map.put("password",password);
        NetworkApi.createService(LRRequestInterface.class, 2).
                register(map).compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse setPasswordResponse) {
                mSetPassword.setValue(setPasswordResponse);

            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);

            }
        }));
    }
}
