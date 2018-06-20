package diploma.edu.zp.guide_my_own;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import diploma.edu.zp.guide_my_own.service.LocationService;
import diploma.edu.zp.guide_my_own.service.RetrofitApi;
import diploma.edu.zp.guide_my_own.service.ServiceGenerator;
import io.fabric.sdk.android.Fabric;


public class GuideMyOwn extends Application {
    private static final String GOOGLE_MAP_API = "https://maps.googleapis.com/maps/api/directions/";
    private static Intent locService;
    private static Context context;
    private static RetrofitApi sRetrofitApi;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        Crashlytics.setUserName("Vitaly");
        Crashlytics.setUserEmail("bobking.vitaly.1987@gmail.com");
        Crashlytics.setUserIdentifier("19872107");



        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .writeDebugLogs()
                .diskCacheSize(50 * 1024 * 1024)
                .build();

        ImageLoader.getInstance().init(config);

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
