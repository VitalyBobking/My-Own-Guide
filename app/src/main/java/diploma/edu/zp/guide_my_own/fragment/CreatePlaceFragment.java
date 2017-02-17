package diploma.edu.zp.guide_my_own.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import diploma.edu.zp.guide_my_own.DBHelper.FillDataBase;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.DeleteFileByPath;

/**
 * Created by Val on 2/16/2017.
 */

public class CreatePlaceFragment extends Fragment implements View.OnClickListener {

    public static CreatePlaceFragment newInstance(LatLng latLng) {
        CreatePlaceFragment fragment = new CreatePlaceFragment();
        Bundle args = new Bundle();
        args.putParcelable(COORDINATES, latLng);
        fragment.setArguments(args);
        return fragment;
    }

    public static final String COORDINATES = "COORDINATES";
    public final static int CAMERA_RQ = 6969;
    private String path;
    private ImageView ivPicture;
    private Button btnSave;
    private LatLng latLng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_place, container, false);

        latLng = getArguments().getParcelable(COORDINATES);

        if (latLng != null) {
            Log.e("latLng.latitude", String.valueOf(latLng.latitude));
            Log.e("latLng.longitude", String.valueOf(latLng.longitude));
        }

        TextView tvTakePicture = (TextView) v.findViewById(R.id.tvTakePicture);
        tvTakePicture.setOnClickListener(this);

        ivPicture = (ImageView) v.findViewById(R.id.ivPicture);
        btnSave = (Button) v.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvTakePicture) {
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
            FillDataBase.fill(getActivity(), makePlace());
        }
    }

    private Place makePlace() {
        Place place = new Place();
        place.setTitle("title");
        place.setLatitude(latLng.latitude);
        place.setLongitude(latLng.longitude);
        place.setUrl_pic(path);

        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    place.setCity(addresses.get(0).getLocality());
                } else {
                    place.setCity(addresses.get(0).getAdminArea());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return place;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_RQ && data != null) {
            if (path != null) {
                DeleteFileByPath.deleteFile(path);
            }

            path = data.getData().getPath();

            /*ExifInterface exif = null;
            try {
                exif = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(path, opts);

            /*if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                bm = rotateBitmap(bm, orientation);
            }*/

            //Bitmap bmp2 = bm.copy(bm.getConfig(), true);

            //bm = scaleBitmap(bm, width);

            ivPicture.setVisibility(View.VISIBLE);
            ivPicture.setImageBitmap(bm);
        }
    }
}
