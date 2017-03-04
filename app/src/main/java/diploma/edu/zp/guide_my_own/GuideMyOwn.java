package diploma.edu.zp.guide_my_own;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import diploma.edu.zp.guide_my_own.service.LocationService;
import diploma.edu.zp.guide_my_own.service.RetrofitApi;
import diploma.edu.zp.guide_my_own.service.ServiceGenerator;

/**
 * Created by Val on 2/17/2017.
 */

public class GuideMyOwn extends Application {
    private static final String GOOGLE_MAP_API = "https://maps.googleapis.com/maps/api/directions/";
    private static Intent locService;
    private static Context context;
    private static RetrofitApi sRetrofitApi;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        sRetrofitApi = new ServiceGenerator().createService(RetrofitApi.class, GOOGLE_MAP_API);

        locService = new Intent(this, LocationService.class);
    }

    public static void startLocService() {
        context.startService(locService);
    }

    public static void stopLocService() {
        context.stopService(locService);
    }

    public static RetrofitApi getApi() {
        return sRetrofitApi;
    }
}
