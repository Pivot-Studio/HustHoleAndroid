package cn.pivotstudio.modulep.publishhole.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import cn.pivotstudio.modulep.publishhole.BR;

/**
 * @classname:TypeResponse
 * @description:
 * @date:2022/5/6 21:20
 * @version:1.0
 * @author:
 */
public class TypeResponse extends BaseObservable {
    String type;
    @Bindable
    public String getType() {
        return type;
    }

    public void setType(String type) {

        this.type = type;
        notifyPropertyChanged(BR.type);
    }
}
