package cn.pivotstudio.modulec.loginandregister.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.example.libbase.constant.Constant;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;

import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulec.loginandregister.model.MsgResponse;
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface;

/**
 * @classname:ModifyPasswordRepository
 * @description:
 * @date:2022/5/2 1:07
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class ModifyPasswordRepository {
    public final MutableLiveData<String> failed = new MutableLiveData<>();
    public MutableLiveData<MsgResponse> mModifyPassword= new MutableLiveData<>();
    public ModifyPasswordRepository() {
    }
    public void modifyPasswordForNetwork(String id, String verify_code,String password) {
        NetworkApi.createService(LRRequestInterface.class, 2).
                resetPassword(Constant.BASE_URL +"auth/mobileChangePassword?email="+id+"&verify_code="+verify_code+"&new_password="+password).compose(NetworkApi.applySchedulers(new BaseObserver<MsgResponse>() {
            @Override
            public void onSuccess(MsgResponse msgResponse) {
                mModifyPassword.setValue(msgResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);

            }
        }));
    }
}
