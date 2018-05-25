package diploma.edu.zp.guide_my_own.fragment;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;

import diploma.edu.zp.guide_my_own.DBHelper.DeletePlace;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.Camera2Activity;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.camera2.Camera2BasicFragment;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Val on 3/6/2017.
 */

public class DetailsFragment extends Fragment {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String SAVE_STATE_PLACE = "SAVE_STATE_PLACE";
    public static final String RESULT_REGISTER = "REGISTER_IN_FACEBOOK";
    final String BUTTON_OUT_ACTIVITY = "SAVED_BUTTON_OUT_STATE";
    private Place mPlace;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageView ivPhoto,ivPhotoEdit;
    private ImageView ivFacebook;
    private FloatingActionsMenu fabMenu;
    private ProgressBar progressBar;
    private boolean btnOutIsActivated;
    private Bitmap bitmap;
    private SharePhotoContent content;
    private String photoEdit;
    private ActionBar actionBar;



    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    public static final String USER_NAME = "user_posts";
    public LoginButton loginButton;
    public ElementsUpdated elementsUpdated;

    public static DetailsFragment newInstance(Place place) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ITEM, place);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    public interface ElementsUpdated {
        void elementSelected();
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            elementsUpdated = (ElementsUpdated) context;
        } catch (ClassCastException e) {
           e.getMessage();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mPlace = (Place) getArguments().get(EXTRA_ITEM);
        } else {
            mPlace = (Place) savedInstanceState.get(SAVE_STATE_PLACE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_details, container, false);

        ivPhoto = v.findViewById(R.id.ivPhoto);
        ivPhotoEdit = v.findViewById(R.id.ivPhotoEdit);
        ivFacebook = v.findViewById(R.id.ivFaceBook);
        ImageView ivCloseBottomSheet = v.findViewById(R.id.ivCloseBottomSheet);

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvDescription = v.findViewById(R.id.tvDescription);
        EditText etDescriptionEdit = v.findViewById(R.id.etDescriptionEdit);
        TextView tvPlaceName = v.findViewById(R.id.tvPlaceName);
        EditText etPlaceNameEdit = v.findViewById(R.id.etPlaceNameEdit);

        View bottomSheet  = v.findViewById(R.id.bottomSheet);

        fabMenu = v.findViewById(R.id.fabMenu);
        FloatingActionButton fabShare = v.findViewById(R.id.fabShare);
        FloatingActionButton fabEdit = v.findViewById(R.id.fabEdit);
        FloatingActionButton fabDelete = v.findViewById(R.id.fabDelete);

        Button btnSaveEdit = v.findViewById(R.id.btnSaveEdit);

        loginButton =  v.findViewById(R.id.login_button);
        progressBar = v.findViewById(R.id.progressBar);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(0);

        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, USER_NAME));

        ivFacebook.setOnClickListener(v12 -> {
            if(AccessToken.getCurrentAccessToken() != null) {
                sharePhotoToFacebook();
            } else {
                setViewVisibility(ivFacebook,View.INVISIBLE);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "please register",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                    fabMenu.setVisibility(View.VISIBLE);
                    fabMenu.toggle();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fabDelete.setOnClickListener(v16 -> {
            DeletePlace.delete(getContext(),mPlace.getId());
             elementsUpdated.elementSelected();
            ((CountryActivity)getActivity()).setWasEdited(true);

            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.deleted_success,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            getActivity().getSupportFragmentManager().popBackStackImmediate();

        });


        ivPhotoEdit.setOnClickListener(v15 -> {
            Intent intent = new Intent(getActivity(), Camera2Activity.class);
            startActivityForResult(intent, 2018);
        });
        btnSaveEdit.setOnClickListener(v14 -> {

             if(etPlaceNameEdit.getText().toString().length() > 1) {
                tvTitle.setText(etPlaceNameEdit.getText().toString());
                actionBar.setTitle(etPlaceNameEdit.getText().toString());

                tvDescription.setText(etDescriptionEdit.getText().toString());

                setViewVisibility(tvTitle,View.VISIBLE);
                setViewVisibility(tvDescription,View.VISIBLE);
                setViewVisibility(tvPlaceName,View.VISIBLE);

                setViewVisibility(etDescriptionEdit,View.GONE);
                setViewVisibility(etPlaceNameEdit,View.GONE);
                setViewVisibility(ivPhotoEdit,View.GONE);
                setViewVisibility(btnSaveEdit,View.GONE);

                GetPlaces.updateDataBase(String.valueOf(mPlace.getId()),
                        String.valueOf(etPlaceNameEdit.getText()),
                        String.valueOf(etDescriptionEdit.getText()),
                        photoEdit,getContext());

                 elementsUpdated.elementSelected();

                 ((CountryActivity)getActivity()).setWasEdited(true);
                 fabMenu.toggle();
                 Toast toast = Toast.makeText(getApplicationContext(),
                         "editing was successful",
                         Toast.LENGTH_SHORT);
                 toast.setGravity(Gravity.CENTER, 0, 0);
                 toast.show();
             } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.enter_place_name,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
             }
        });

        fabEdit.setOnClickListener(v13 -> {

            setViewVisibility(tvTitle,View.GONE);
            setViewVisibility(tvDescription,View.GONE);
            setViewVisibility(tvPlaceName,View.GONE);

            setViewVisibility(etDescriptionEdit,View.VISIBLE);
            setViewVisibility(etPlaceNameEdit,View.VISIBLE);
            setViewVisibility(ivPhotoEdit,View.VISIBLE);
            setViewVisibility(btnSaveEdit,View.VISIBLE);

            fabMenu.toggle();

            if(mPlace.getTitle() != null | mPlace.getDescription() != null) {
                etPlaceNameEdit.setText(mPlace.getTitle());
                etDescriptionEdit.setText(mPlace.getDescription());
            }


        });

        fabShare.setOnClickListener(v1 -> {

            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            setViewVisibility(fabMenu,View.INVISIBLE);
            setViewVisibility(loginButton,View.VISIBLE);

        });

        ivCloseBottomSheet.setOnClickListener(v2 -> {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            setViewVisibility(fabMenu,View.VISIBLE);

        });

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    if(loginResult.getAccessToken().getUserId() != null) {
                        setViewVisibility(ivFacebook,View.VISIBLE);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "you have successfully registered",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        setViewVisibility(loginButton, View.INVISIBLE);
                        setViewVisibility(ivFacebook, View.INVISIBLE);
                        progressBarVisibility(View.VISIBLE);
                        sharePhotoToFacebook();
                    } else {
                        setViewVisibility(ivFacebook,View.INVISIBLE);
                    }
                }
                @Override
                public void onCancel() {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You canceled",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                @Override
                public void onError(FacebookException exception) {
                    isOnline();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            exception.getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
        });

        //ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(mPlace.getUrl_pic()));
        if (mPlace.getUrl_pic() != null) {
            ImageLoader.getInstance().displayImage("file:///"+mPlace.getUrl_pic(), ivPhoto);
        }
        tvTitle.setText(mPlace.getTitle());

        String desc = mPlace.getDescription();
        if (desc != null)
            tvDescription.setText(mPlace.getDescription());
        else
            tvDescription.setVisibility(View.GONE);

        tvPlaceName.setText(mPlace.getPlaceName());

        return v;
    }


    public void sharePhotoToFacebook(){

        setViewVisibility(loginButton,View.INVISIBLE);
        setViewVisibility(ivFacebook,View.GONE);
        progressBarVisibility(View.VISIBLE);

            bitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                progressBarVisibility(View.GONE);
                setViewVisibility(loginButton,View.VISIBLE);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You shared the photo",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                setViewVisibility(ivFacebook,View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You canceled",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                progressBarVisibility(View.GONE);
                setViewVisibility(loginButton,View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {

                isOnline();
                Toast toast = Toast.makeText(getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                progressBarVisibility(View.VISIBLE);
                setViewVisibility(loginButton,View.VISIBLE);
                Log.e("onError ---->", String.valueOf(error.getMessage()));
            }
        });

    }
    private boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No internet connection",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    private void progressBarVisibility(int visibilityState) {
        progressBar.setVisibility(visibilityState);
    }
    //visibility for different view
    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar = ((CountryActivity)getActivity()).getSupportActionBar();
        if (mPlace != null && actionBar != null){
            actionBar.setTitle(mPlace.getTitle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SAVE_STATE_PLACE, mPlace);
        if(loginButton.isShown()) {
            outState.putBoolean("btnOutVisibility", true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            btnOutIsActivated = savedInstanceState.getBoolean("button_out_activated");
            buttonOutIsActivated();
        }
    }
    private void buttonOutIsActivated() {
        SharedPreferences sp = getActivity().getSharedPreferences(BUTTON_OUT_ACTIVITY, Context.MODE_PRIVATE);

        if (btnOutIsActivated) {
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("button_out_activated", true);
            e.apply();
            setViewVisibility(loginButton,View.INVISIBLE);
        } else {
            setViewVisibility(loginButton,View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == Camera2BasicFragment.RESULT_PATH){
                photoEdit = data.getStringExtra(Camera2BasicFragment.NAME_A_PATH);
                saveImagePhotoEdited();
            }

    }
    private void saveImagePhotoEdited() {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoEdit, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoEdit, opts);
        ivPhoto.setImageBitmap(bm);
    }

}
