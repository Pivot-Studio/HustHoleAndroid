package cn.pivotstudio.modulep.publishhole.network;



import java.util.List;

import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse;
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse;
import cn.pivotstudio.modulep.publishhole.model.MsgResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @classname:PHRequestInterface
 * @description:发布树洞网络请求接口
 * @date:2022/5/5 22:52
 * @version:1.0
 * @author:
 */
public interface PHRequestInterface{
    @POST
    Observable<MsgResponse> publishHole(@Url String url);
    @GET("forests/joined?")
    Observable<DetailTypeForestResponse> joined(@Query("list_size") int list_size,
                                              @Query("start_id") int start_id);
    @HTTP(method = "GET", path = "forests/type/{forest_type}?", hasBody = false)
    Observable<DetailTypeForestResponse> getDetailTypeForest(@Path("forest_type") String forest_type,
                                                                                  @Query("start_id") int start_id,
                                                                                  @Query("list_size") int list_size,
                                                                                  @Query("is_last_active") Boolean is_last_active);

    @GET("forests/types?")
    Observable<ForestTypeResponse> getType(@Query("start_id") int start_id,
                                           @Query("list_size") int list_size);
    @GET("forests/hot?")
    Observable<DetailTypeForestResponse> getHotForest(@Query("start_id") int start_id,
                                    @Query("list_size") int list_size);
}
