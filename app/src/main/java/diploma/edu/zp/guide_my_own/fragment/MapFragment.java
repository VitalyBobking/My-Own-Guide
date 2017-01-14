package diploma.edu.zp.guide_my_own.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import diploma.edu.zp.guide_my_own.R;

/**
 * Created by Val on 1/14/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION = 1503;
    private static final int REQUEST_LOCATION_CODE = 1;
    private MapView mapView;
    private GoogleMap mGoogleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                requestSinglePermission();
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }
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

    private void setGoogleLocEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                requestSinglePermission();
            }
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestSinglePermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), locationPermission);
        String[] permissions = new String[] {locationPermission};

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
                                showRationale = shouldShowRequestPermissionRationale( permission );
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
                switch (which){
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
                switch (which){
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
}
