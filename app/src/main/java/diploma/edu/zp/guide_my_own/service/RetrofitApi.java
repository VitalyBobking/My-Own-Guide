package diploma.edu.zp.guide_my_own.service;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Val on 3/2/2017.
 */

public interface RetrofitApi {
    @GET("json")
    Observable<Object> getPath(@Query("origin") String origin,
                               @Query("destination") String destination,
                               @Query("sensor") boolean sensor);
}
