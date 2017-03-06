package diploma.edu.zp.guide_my_own.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import diploma.edu.zp.guide_my_own.DBHelper.DBGetPlaceByID;
import diploma.edu.zp.guide_my_own.GuideMyOwn;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.lib.BottomSheetBehaviorGoogleMapsLike;
import diploma.edu.zp.guide_my_own.model.MapTypes;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.service.LocationService;
import diploma.edu.zp.guide_my_own.service.PathJSONParser;
import diploma.edu.zp.guide_my_own.service.SingleShotLocationProvider;
import diploma.edu.zp.guide_my_own.utils.CreateBitmapFromPath;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Val on 1/14/2017.
 */

public class MapFragment extends DialogToastFragment implements OnMapReadyCallback, /*LocationListener,*/ GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    public static final String BROADCAST_ACTION = "diploma.edu.zp.guide_my_own.location_service";
    public static final String SERVICE_LOCATION = "diploma.edu.zp.guide_my_own.SERVICE_LOCATION";
    public static final String CURRENT_PLACE = "CURRENT_PLACE";
    public static final String CURRENT_ZOOM = "CURRENT_ZOOM";
    private static final int REQUEST_LOCATION = 1503;
    private static final int REQUEST_LOCATION_CODE = 1;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private KProgressHUD buildingPath;
    private List<Place> places;
    private List<Marker> markers;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton fab;
    private Place mPlace;
    private TextView tvTitle;
    private TextView tvAddress;
    private ImageView ivClose;
    private ImageView ivPhoto;
    private float currentZoom = 0;
    private View bottomSheet;
    private Subscription mSubscription;
    private Polyline mPolyline;
    private String mapType;
    private View popupView;
    private RelativeLayout rootLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mPlace = (Place) savedInstanceState.getSerializable(CURRENT_PLACE);
            currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(view2 -> {
            Location loc = LocationService.getLastKnownLocation(getContext());
            if (loc != null) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                createPlace(latLng);
            } else {
                showErrorDialog("Please wait when your location will be gotten");
            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.main_content);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);

        tvTitle = (TextView) bottomSheet.findViewById(R.id.tvTitle);
        tvAddress = (TextView) bottomSheet.findViewById(R.id.tvAddress);
        ivClose = (ImageView) bottomSheet.findViewById(R.id.ivClose);
        ivPhoto = (ImageView) bottomSheet.findViewById(R.id.ivPhoto);

        if (mPlace != null) {
            fillBottomSheet(mPlace);
        }

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                        if (popupView == null)
                            fab.setVisibility(View.VISIBLE);
                        Log.d("bottomsheet-", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                        Log.d("bottomsheet-", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                        clearPolyline();
                        removePopUp();
                        fab.setVisibility(View.GONE);
                        Log.d("bottomsheet-", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:
                        clearPolyline();
                        removePopUp();
                        fab.setVisibility(View.GONE);
                        Log.d("bottomsheet-", "STATE_ANCHOR_POINT");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN:
                        fab.setVisibility(View.VISIBLE);
                        Log.d("bottomsheet-", "STATE_HIDDEN");
                        break;
                    default:
                        Log.d("bottomsheet-", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);

        createPopUpCachesWindow();

        return view;
    }

    private void addMarkers() {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        for (Place p : places) {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLatitude(), p.getLongitude()))
                    .title(p.getTitle())
                    .snippet(p.getPlaceName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp)));
            marker.setTag(p.getId());
            markers.add(marker);
        }
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        MapsInitializer.initialize(this.getActivity());
        mGoogleMap = googleMap;
        places = GetPlaces.getPlaces(getContext(), false, null);
        addMarkers();
        setGoogleLocEnabled();

        Location loc = LocationService.getLastKnownLocation(getContext());
        if (loc == null) sendLocation();
        else startMap(loc);

        mGoogleMap.setOnMapLongClickListener(this);
    }

    private void startMap(Location loc) {
        try {
            if (getActivity() != null) {
                if (loc != null) {
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom == 0?6:currentZoom));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        SingleShotLocationProvider.requestSingleUpdate(getActivity(),
                location -> {
                    if (location != null) {
                        Location targetLocation = new Location("");
                        targetLocation.setLatitude(location.longitude);
                        targetLocation.setLongitude(location.latitude);

                        startMap(targetLocation);
                    }
                }, locationManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.map_type:
                chooseMapTypeDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chooseMapTypeDialog() {
        Dialog dialogMapType = new Dialog(getActivity());
        dialogMapType.setContentView(R.layout.map_dialog);
        dialogMapType.setTitle(getString(R.string.choose_map_type));
        dialogMapType.setCancelable(true);
        getElementOfDialog(dialogMapType);
    }

    private void getElementOfDialog(final Dialog dialog) {
        Button btnDialogOk = (Button) dialog.findViewById(R.id.btnDialogOk);

        RadioButton rbNormal = (RadioButton) dialog.findViewById(R.id.rbNormal);
        RadioButton rbHybrid = (RadioButton) dialog.findViewById(R.id.rbHybrid);
        RadioButton rbSatellite = (RadioButton) dialog.findViewById(R.id.rbSatellite);
        RadioButton rbTerrain = (RadioButton) dialog.findViewById(R.id.rbTerrain);

        RadioButton[] radioButtons = new RadioButton[]{rbNormal, rbSatellite, rbTerrain, rbHybrid};

        for (RadioButton rb : radioButtons) {
            String textRB = rb.getText().toString();
            if (textRB.equalsIgnoreCase(mapType)) {
                rb.setChecked(true);
            }
        }

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbChecked = (RadioButton) dialog.findViewById(checkedId);
            if (!rbChecked.getText().toString().equals(mapType)) {
                mapType = rbChecked.getText().toString();
                dialog.dismiss();
                chooseMapType();
            }
        });

        btnDialogOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void chooseMapType() {
        if (mGoogleMap != null) {
            switch (mapType) {
                case MapTypes.NORMAL:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case MapTypes.SATELLITE:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case MapTypes.TERRAIN:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                case MapTypes.HYBRID:
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
            }
        }
    }

    @SuppressLint("InflateParams")
    private void createPopUpCachesWindow() {
        popupView = getActivity().getLayoutInflater().inflate(
                R.layout.pop_up_marker, null, false);
    }

    private void showPopUpWindow(PolylineOptions options) {
        removePopUp();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        List<LatLng> coords = options.getPoints();
        double totalDistance = 0;
        for (int i = 0; i + 1 < coords.size(); i++) {
            totalDistance += calcDistance(coords.get(i), coords.get(i+1));
        }

        int distance;
        String unit;
        if (totalDistance > 1000) {
            distance = (int) totalDistance / 1000;
            unit = " km";
        } else {
            distance = (int) totalDistance * 10;
            unit = " m";
        }
        ((TextView)popupView.findViewById(R.id.tvDistanceToCacheInfo)).setText(String.valueOf(distance + unit));
        ((TextView)popupView.findViewById(R.id.tvTitle)).setText(mPlace.getTitle());

        rootLayout.addView(popupView, layoutParams);

        popupView.findViewById(R.id.ivDisposePopUp).setOnClickListener(view -> {
            removePopUp();
            clearPolyline();
        });
        fab.setVisibility(View.GONE);
    }

    private void clearPolyline() {
        if (mPolyline != null) {
            mPolyline.remove();
            mPolyline = null;
        }
    }

    private void removePopUp() {
        if (popupView != null) {
            fab.setVisibility(View.VISIBLE);
            rootLayout.removeView(popupView);
        }
    }

    private double calcDistance(LatLng a, LatLng b) {
        float[] results = new float[1];
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results);
        return results[0];
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        GuideMyOwn.startLocService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        GuideMyOwn.stopLocService();
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
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

    private void setGoogleLocEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), locationPermission);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        getActivity().finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_LOCATION_CODE);
                        break;
                }
            };

    private void startDialogPermissionsDenied(String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        getActivity().finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        requestSinglePermission();
                        break;
                }
            };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mPlace != null) {
                outState.putSerializable(CURRENT_PLACE, mPlace);
                outState.putFloat(CURRENT_ZOOM, mGoogleMap.getCameraPosition().zoom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        createPlace(latLng);
    }

    public void done() {
        showSuccess("Place was added successful!");
        mGoogleMap.clear();
        places = GetPlaces.getPlaces(getContext(), false, null);
        addMarkers();
    }

    private void createPlace(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Create new place!")
                .setMessage("Do you want to create?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.content_main, CreatePlaceFragment.newInstance(latLng), CreatePlaceFragment.class.getName());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }).setNegativeButton(android.R.string.no, (dialog, which) -> {

                }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mPlace = DBGetPlaceByID.getPlace(getActivity(), Integer.valueOf(marker.getTag().toString()));

        fillBottomSheet(mPlace);
        mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED);

        FloatingActionButton fab1 = (FloatingActionButton) bottomSheet.findViewById(R.id.fab1);
        fab1.setOnClickListener(view -> {
            mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);

            Location loc = LocationService.getLastKnownLocation(getActivity());
            if (loc != null) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                mSubscription = GuideMyOwn.getApi().getPath(String.valueOf((marker.getPosition().latitude + "," +
                        marker.getPosition().longitude)), String.valueOf((latLng.latitude + "," + latLng.longitude)), false)
                        .map(this::convertObjToString)
                        .map(this::parsePath)
                        .map(this::createPolylinePoints)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<PolylineOptions>() {
                            @Override
                            public void onCompleted() {}

                            @Override
                            public void onError(Throwable e) {
                                Log.e("onError ---->", String.valueOf(e.getMessage()));
                            }

                            @Override
                            public void onNext(PolylineOptions options) {
                                showPopUpWindow(options);
                                mPolyline = mGoogleMap.addPolyline(options);
                                moveCamera(options);
                            }
                        });
            } else {
                showErrorDialog("Can't get your location");
            }
        });
        return false;
    }

    private void moveCamera(PolylineOptions p) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < p.getPoints().size();i++){
            builder.include(p.getPoints().get(i));
        }
        LatLngBounds bounds = builder.build();
        int padding = (int) getResources().getDimension(R.dimen.camera_padding);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cu);
    }

    private PolylineOptions createPolylinePoints(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;

        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<>();
            polyLineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(4);
            polyLineOptions.color(Color.BLUE);
        }
        return polyLineOptions;
    }

    private String convertObjToString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    private List<List<HashMap<String, String>>> parsePath(String s) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(s);
            PathJSONParser parser = new PathJSONParser();
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    private void fillBottomSheet(Place place) {
        try {
            fab.setVisibility(View.GONE);
            ivClose.setOnClickListener(view -> {
                removePopUp();
                mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
            });
            tvTitle.setText(place.getTitle());
            tvAddress.setText(place.getPlaceName());

            ivPhoto.setImageBitmap(null);

            if (place.getUrl_pic() != null) {
                ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(place.getUrl_pic()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
