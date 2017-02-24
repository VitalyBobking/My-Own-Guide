package diploma.edu.zp.guide_my_own.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import diploma.edu.zp.guide_my_own.DBHelper.FillDataBase;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.DeleteFileByPath;

/**
 * Created by Val on 2/16/2017.
 */

public class CreatePlaceFragment extends DialogToastFragment implements View.OnClickListener {

    public static CreatePlaceFragment newInstance(LatLng latLng) {
        CreatePlaceFragment fragment = new CreatePlaceFragment();
        Bundle args = new Bundle();
        args.putParcelable(COORDINATES, latLng);
        fragment.setArguments(args);
        return fragment;
    }
    public static final String COORDINATES = "COORDINATES";
    public static final String CREATER_LISTENER = "CREATER_LISTENER";
    public final static int CAMERA_RQ = 6969;
    private String path;
    private ImageView ivPicture;
    private RelativeLayout rlPicture;
    private TextView tvPlace;
    private EditText etTitle;
    private LatLng latLng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_place, container, false);

        latLng = getArguments().getParcelable(COORDINATES);

        Button btnTakePicture = (Button) v.findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);

        ivPicture = (ImageView) v.findViewById(R.id.ivPicture);
        rlPicture = (RelativeLayout) v.findViewById(R.id.rlPicture);

        Button btnSave = (Button) v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        etTitle = (EditText) v.findViewById(R.id.etTitle);
        etTitle.setOnClickListener(this);

        tvPlace = (TextView) v.findViewById(R.id.tvPlace);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnTakePicture) {
            File saveDir = null;

            final String[] fullPath = {getContext().getFilesDir().getAbsolutePath()};

            try {
                saveDir = new File(fullPath[0]);
                if (!saveDir.exists()) {
                    Log.e("dir mk dir", String.valueOf(saveDir.mkdirs()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (saveDir != null) {
                MaterialCamera materialCamera = new MaterialCamera(getActivity())
                        .saveDir(saveDir)
                        .autoSubmit(false)
                        .showPortraitWarning(false)
                        .defaultToFrontFacing(false)
                        .allowRetry(true)
                        .labelRetry(R.string.retry)
                        .primaryColor(ContextCompat.getColor(getContext(), R.color.white))
                        .maxAllowedFileSize(1024 * 1024)
                        .qualityProfile(MaterialCamera.QUALITY_480P)
                        .labelConfirm(R.string.use_photo)
                        .stillShot().labelConfirm(R.string.use_photo);

                materialCamera.start(CAMERA_RQ);
            }
        } else if (view.getId() == R.id.btnSave) {
            if (etTitle.getText().toString().length() > 1) {
                long res = FillDataBase.fill(getActivity(), makePlace());
                if (res == -1) {
                    showErrorDialog("Something went wrong");
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(getString(R.string.map));
                    if (fragment instanceof MapFragment) {
                        ((MapFragment) fragment).done();
                    }
                    getActivity().getSupportFragmentManager().popBackStack();

                }
            } else {
                showErrorDialog("Please enter place name");
            }
        }
    }

    private Place makePlace() {
        Place place = new Place();
        place.setTitle(etTitle.getText().toString());
        place.setLatitude(latLng.latitude);
        place.setLongitude(latLng.longitude);
        place.setUrl_pic(path);
        place.setPlaceName(getPlaceName());

        return place;
    }

    private String getPlaceName() {
        String placeName = "";
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                placeName += address.getAddressLine(2)!=null?address.getAddressLine(2)  + ", ":"";

                String address0 = address.getAddressLine(0);
                if (address0 != null) {
                    boolean is_code = address0.matches("^-?\\d+$");
                    if (!is_code)
                        placeName += address0;
                }

                String address1 = address.getAddressLine(1);
                if (address1 != null) {
                    boolean is_code = address1.matches("^-?\\d+$");
                    if (!is_code)
                        placeName += ", " + address1;
                }
            } else {
                placeName = "Not defined";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_RQ && data != null) {
            if (path != null) {
                DeleteFileByPath.deleteFile(path);
            }

            path = data.getData().getPath();

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(path, opts);

            rlPicture.setVisibility(View.VISIBLE);
            ivPicture.setImageBitmap(bm);
            tvPlace.setText(getPlaceName());
        }
    }
}
