package diploma.edu.zp.guide_my_own.service;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Val on 3/2/2017.
 */

public interface RetrofitApi {
    @GET("/api/v2.3/sponsors/")
    Observable<Object> getPath();
}
