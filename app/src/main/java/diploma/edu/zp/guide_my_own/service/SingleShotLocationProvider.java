package diploma.edu.zp.guide_my_own.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class SingleShotLocationProvider{
    public interface LocationCallback {
        void onNewLocationAvailable(GPSCoordinates location);
    }

    public static void requestSingleUpdate(final Context context,
                                           final LocationCallback callback, LocationManager locationManager) {

        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    callback.onNewLocationAvailable(new GPSCoordinates(
                            location.getLatitude(), location.getLongitude()));
                }

                @Override
                public void onStatusChanged(String provider,
                                            int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e("provider", String.valueOf(provider));
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class GPSCoordinates {
        public float longitude = -1;
        public float latitude = -1;

        public GPSCoordinates(double theLongitude, double theLatitude) {
            longitude = (float) theLongitude;
            latitude = (float) theLatitude;
        }
    }
}
