package diploma.edu.zp.guide_my_own.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.CreatePlaceFragment;
import diploma.edu.zp.guide_my_own.lib.BottomSheetBehaviorGoogleMapsLike;
import diploma.edu.zp.guide_my_own.lib.MergedAppBarLayoutBehavior;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.service.SingleShotLocationProvider;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;

/**
 * Created by Val on 2/27/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    public static final String BROADCAST_ACTION = "dk.educaching.location_service";
    public static final String SERVICE_LOCATION = "dk.educaching.SERVICE_LOCATION";
    private static final int REQUEST_LOCATION = 1503;
    private static final int REQUEST_LOCATION_CODE = 1;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private KProgressHUD gettingLocationDialog;
    private List<Place> places;
    private List<Marker> markers;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);

        final BottomSheetBehaviorGoogleMapsLike behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                        Log.d("bottomsheet-", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                        Log.d("bottomsheet-", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                        Log.d("bottomsheet-", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:
                        Log.d("bottomsheet-", "STATE_ANCHOR_POINT");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN:
                        Log.d("bottomsheet-", "STATE_HIDDEN");
                        break;
                    default:
                        Log.d("bottomsheet-", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

        AppBarLayout mergedAppBarLayout = (AppBarLayout) findViewById(R.id.merged_appbarlayout);
        MergedAppBarLayoutBehavior mergedAppBarLayoutBehavior = MergedAppBarLayoutBehavior.from(mergedAppBarLayout);
        mergedAppBarLayoutBehavior.setToolbarTitle("Title Dummy");

        mergedAppBarLayoutBehavior.setNavigationOnClickListener(v -> behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED));

        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        MapsInitializer.initialize(this);
        mGoogleMap = googleMap;
        places = GetPlaces.getPlaces(getApplicationContext(), false, null);
        addMarkers();

        setGoogleLocEnabled();
        sendLocation();
        mGoogleMap.setOnMapLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_CODE) {
            setGoogleLocEnabled();
        }
    }

    private void addMarkers() {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        for (Place p : places) {
            markers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLatitude(), p.getLongitude()))
                    .title(p.getTitle())
                    .snippet(p.getPlaceName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp))));

        }
    }

    private void startMap(Location loc) {
        try {
            if (gettingLocationDialog != null && gettingLocationDialog.isShowing())
                gettingLocationDialog.dismiss();

            if (loc != null) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SingleShotLocationProvider.requestSingleUpdate(this,
                location -> {
                    if (location != null) {
                        Location targetLocation = new Location("");
                        targetLocation.setLatitude(location.longitude);
                        targetLocation.setLongitude(location.latitude);

                        startMap(targetLocation);
                    }
                }, locationManager);
    }

    private void setGoogleLocEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
                sendLocation();
            } else {
                requestSinglePermission();
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
            sendLocation();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestSinglePermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(this, locationPermission);
        String[] permissions = new String[]{locationPermission};

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0, len = permissions.length; i < len; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            boolean showRationale = false;

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                showRationale = shouldShowRequestPermissionRationale(permission);
                            }

                            if (!showRationale) {
                                startDialogOpenAppDetails(getString(R.string.permission_denied), getString(R.string.permissions_denied_message));
                            } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                                startDialogPermissionsDenied(getString(R.string.permission_denied), getString(R.string.permissions_denied_message));
                            }
                        }
                    }
                } else {
                    setGoogleLocEnabled();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startDialogOpenAppDetails(String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).setMessage(message)
                    .setPositiveButton(getString(R.string.exit_app), dialogAppDetails)
                    .setNegativeButton(getString(R.string.open), dialogAppDetails)
                    .setCancelable(false).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DialogInterface.OnClickListener dialogAppDetails =
            (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_LOCATION_CODE);
                        break;
                }
            };

    private void startDialogPermissionsDenied(String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title).setMessage(message)
                    .setPositiveButton(getString(R.string.im_sure), dialogPermissionsDenied)
                    .setNegativeButton(getString(R.string.retry), dialogPermissionsDenied)
                    .setCancelable(false).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DialogInterface.OnClickListener dialogPermissionsDenied =
            (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        requestSinglePermission();
                        break;
                }
            };

    /*@Override
    public void onLocationChanged(Location location) {
        Log.e("location", String.valueOf(location.getLatitude()));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }*/

    @Override
    public void onMapLongClick(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Create new place")
                .setMessage("Do you want to create?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.content_main, CreatePlaceFragment.newInstance(latLng), CreatePlaceFragment.class.getName());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }).setNegativeButton(android.R.string.no, (dialog, which) -> {

                }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void done() {
        mGoogleMap.clear();
        places = GetPlaces.getPlaces(getApplicationContext(), false, null);
        addMarkers();
    }
}
