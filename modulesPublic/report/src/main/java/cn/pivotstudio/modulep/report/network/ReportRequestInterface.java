package cn.pivotstudio.modulep.report.network;

import cn.pivotstudio.modulep.report.model.MsgResponse;
import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @classname:ReportRequestInterface
 * @description:
 * @date:2022/5/18 16:37
 * @version:1.0
 * @author:
 */
public interface ReportRequestInterface {
    @POST
    Observable<MsgResponse> report(@Url String url);
}
