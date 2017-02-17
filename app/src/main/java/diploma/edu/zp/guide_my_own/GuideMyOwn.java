package diploma.edu.zp.guide_my_own;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import diploma.edu.zp.guide_my_own.service.LocationService;

/**
 * Created by Val on 2/17/2017.
 */

public class GuideMyOwn extends Application {
    private static Intent locService;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        locService = new Intent(this, LocationService.class);
    }

    public static void startLocService() {
        context.startService(locService);
    }
}
