package diploma.edu.zp.guide_my_own.fragment;

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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import diploma.edu.zp.guide_my_own.DBHelper.DBGetPlaceByID;
import diploma.edu.zp.guide_my_own.GuideMyOwn;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.lib.BottomSheetBehaviorGoogleMapsLike;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.service.LocationService;
import diploma.edu.zp.guide_my_own.service.SingleShotLocationProvider;
import diploma.edu.zp.guide_my_own.utils.CreateBitmapFromPath;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;

/**
 * Created by Val on 1/14/2017.
 */

public class MapFragment extends DialogToastFragment implements OnMapReadyCallback, /*LocationListener,*/ GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    public static final String BROADCAST_ACTION = "dk.educaching.location_service";
    public static final String SERVICE_LOCATION = "dk.educaching.SERVICE_LOCATION";
    private static final int REQUEST_LOCATION = 1503;
    private static final int REQUEST_LOCATION_CODE = 1;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private KProgressHUD gettingLocationDialog;
    private List<Place> places;
    private List<Marker> markers;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                        fab.setVisibility(View.VISIBLE);
                        Log.d("bottomsheet-", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                        Log.d("bottomsheet-", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                        fab.setVisibility(View.GONE);
                        Log.d("bottomsheet-", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:
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
        sendLocation();

        mGoogleMap.setOnMapLongClickListener(this);
    }

    private void startMap(Location loc) {
        try {
            if (getActivity() != null) {

                if (gettingLocationDialog != null && gettingLocationDialog.isShowing())
                    gettingLocationDialog.dismiss();

                if (loc != null) {
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));
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

    private void gettingLocationDialog() {
        gettingLocationDialog = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.getting_your_location))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0).show();

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

    /*@Override
    public void onLocationChanged(Location location) {
        Log.e("location", String.valueOf(location.getLatitude()));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }*/

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
        fab.setVisibility(View.GONE);

        TextView tvTitle = (TextView) bottomSheet.findViewById(R.id.tvTitle);
        TextView tvAddress = (TextView) bottomSheet.findViewById(R.id.tvAddress);
        ImageView ivClose = (ImageView) bottomSheet.findViewById(R.id.ivClose);
        ImageView ivPhoto = (ImageView) bottomSheet.findViewById(R.id.ivPhoto);

        ivClose.setOnClickListener(view -> mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED));

        Place p = DBGetPlaceByID.getPlace(getActivity(), Integer.valueOf(marker.getTag().toString()));
        tvTitle.setText(p.getTitle());
        tvAddress.setText(p.getPlaceName());

        if (p.getUrl_pic() != null) {
            ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(p.getUrl_pic()));
        }

        mBottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED);
        return false;
    }
}
