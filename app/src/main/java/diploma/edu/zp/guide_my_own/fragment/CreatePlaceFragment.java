package diploma.edu.zp.guide_my_own.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import diploma.edu.zp.guide_my_own.DBHelper.FillDataBase;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.Camera2Activity;
import diploma.edu.zp.guide_my_own.camera2.Camera2BasicFragment;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.model.Place;

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
    public static final String CURRENT_PATH = "CURRENT_PATH";

    private String path;
    public  ImageView ivPicture;
    public  RelativeLayout rlPicture;
    public  TextView tvPlace;
    private EditText etTitle, etDescr;
    private LatLng latLng;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            path = savedInstanceState.getString(CURRENT_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_place, container, false);

        latLng = getArguments().getParcelable(COORDINATES);

        Button btnTakePicture =  v.findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);

        Button btnSave = v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        ivPicture =  v.findViewById(R.id.ivPicture);
        rlPicture =  v.findViewById(R.id.rlPicture);

        etTitle =  v.findViewById(R.id.etTitle);
        etDescr =  v.findViewById(R.id.etDescr);
        etTitle.setOnClickListener(this);
        etDescr.setOnClickListener(this);

        etDescr.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                fillDB();
            }
            return false;
        });

        tvPlace =  v.findViewById(R.id.tvPlace);

        if (path != null) {
            setImage();
        }

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnTakePicture) {
            Intent intent = new Intent(getActivity(), Camera2Activity.class);
            startActivityForResult(intent, 1888);
        } else if (view.getId() == R.id.btnSave) {
            fillDB();
        }
    }

    private void fillDB() {
        if (etTitle.getText().toString().length() > 1) {
            long res = FillDataBase.fill(getActivity(), makePlace());
            if (res == -1) {
                showErrorDialog(getString(R.string.something_went_wrong));
            } else {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(getString(R.string.map));
                if (fragment instanceof MapFragment) {
                    ((MapFragment) fragment).done();
                }
                getActivity().getSupportFragmentManager().popBackStack();

            }
        } else {
            showErrorDialog(getString(R.string.enter_place_name));
        }
    }

    private Place makePlace() {
        Place place = new Place();
        place.setTitle(etTitle.getText().toString());

        if (etDescr.getText() != null)
            place.setTitle(etDescr.getText().toString());

        place.setLatitude(latLng.latitude);
        place.setLongitude(latLng.longitude);
        place.setUrl_pic(path);
        List<String> pl = getPlaceName();

        place.setPlaceName(pl.get(0));
        place.setCountry(pl.get(1));
        return place;
    }

    private List<String> getPlaceName() {
        String placeName = "";
        String country = "";
        List<String> pl = new ArrayList<>();
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                country = address.getCountryName();
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
        pl.add(placeName);
        pl.add(country);
        return pl;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (path != null) {
            outState.putString(CURRENT_PATH, path);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Camera2BasicFragment.RESULT_PATH){
            path = data.getStringExtra(Camera2BasicFragment.NAME_A_PATH);
            setImage();
        }

    }

    @Override
    public void onDestroy() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onDestroy();
    }

    public void setImage() {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(path, opts);

        rlPicture.setVisibility(View.VISIBLE);
        ivPicture.setImageBitmap(bm);
        tvPlace.setText(getPlaceName().get(0));
    }

}
