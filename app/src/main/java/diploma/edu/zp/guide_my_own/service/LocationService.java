package diploma.edu.zp.guide_my_own.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.MapFragment;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Created by Val on 2/10/2017.
 */

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final Location TODO = new Location("");
    private static GoogleApiClient mGoogleApiClient;
    private static LocationManager locationManager;
    private static AlertDialog myAlertDialog;

    public synchronized static Location getLastKnownLocation(Context context) {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (!gps_enabled && !network_enabled) {
                if (myAlertDialog == null) {
                    showAlert(context);
                }
            } else {
                if (myAlertDialog != null)
                    myAlertDialog.dismiss();

                myAlertDialog = null;
            }
        } catch (Exception e) {
            myAlertDialog = null;
            e.printStackTrace();
        }

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return TODO;
            }
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        GoogleApiClient.ConnectionCallbacks connectionCallbacks = this;
        GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = this;

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void onLocationChanged(final Location location) {

        Intent i = new Intent(MapFragment.BROADCAST_ACTION).putExtra(MapFragment.SERVICE_LOCATION, location);
        this.sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setSmallestDisplacement(0);*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private synchronized static void showAlert(final Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getString(R.string.location_access_disabled))
                .setPositiveButton(context.getString(R.string.open_settings), (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ((Activity)context).startActivityForResult(myIntent, 4);
                    myAlertDialog = null;
                })
                .setNegativeButton(context.getString(R.string.cancel), (paramDialogInterface, paramInt) -> {
                    if (myAlertDialog != null)
                        myAlertDialog.dismiss();

                    myAlertDialog = null;
                });
        dialog.setCancelable(false);
        myAlertDialog = dialog.create();
        dialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }
}
