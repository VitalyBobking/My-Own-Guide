package diploma.edu.zp.guide_my_own.fragment;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private static final String SHOW_PHOTO_AFTER_EDIT = "SHOW_PHOTO_AFTER_EDIT";
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
    private Toast toast;
    private boolean isEditPhoto;
    private TextView tvTitle,tvDescription,tvPlaceName;
    private EditText etDescriptionEdit,etPlaceNameEdit;
    private Button btnSaveEdit;
    private Bitmap bm;
    private RelativeLayout rlPhotoEdit;

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

        rlPhotoEdit = v.findViewById(R.id.rlPhotoEdit);
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        ivPhoto = v.findViewById(R.id.ivPhoto);
        ivPhotoEdit = v.findViewById(R.id.ivPhotoEdit);
        ivFacebook = v.findViewById(R.id.ivFaceBook);
        ImageView ivCloseBottomSheet = v.findViewById(R.id.ivCloseBottomSheet);

        tvTitle = v.findViewById(R.id.tvTitle);
        tvDescription = v.findViewById(R.id.tvDescription);
        etDescriptionEdit = v.findViewById(R.id.etDescriptionEdit);
        tvPlaceName = v.findViewById(R.id.tvPlaceName);
        etPlaceNameEdit = v.findViewById(R.id.etPlaceNameEdit);

        View bottomSheet  = v.findViewById(R.id.bottomSheet);

        fabMenu = v.findViewById(R.id.fabMenu);
        FloatingActionButton fabShare = v.findViewById(R.id.fabShare);
        FloatingActionButton fabEdit = v.findViewById(R.id.fabEdit);
        FloatingActionButton fabDelete = v.findViewById(R.id.fabDelete);

        btnSaveEdit = v.findViewById(R.id.btnSaveEdit);

        loginButton =  v.findViewById(R.id.login_button);
        progressBar = v.findViewById(R.id.progressBar);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(0);

        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, USER_NAME));


        if(savedInstanceState != null) {
            photoEdit = savedInstanceState.getString("photoEdit");
            if(photoEdit == null) {
                ImageLoader.getInstance().displayImage("file:///" + mPlace.getUrl_pic(), ivPhoto);
            } else {
                saveImagePhotoEdited();
            }
        } else {
            ImageLoader.getInstance().displayImage("file:///" + mPlace.getUrl_pic(), ivPhoto);
        }

        ivFacebook.setOnClickListener(v12 -> {
            if(AccessToken.getCurrentAccessToken() != null) {
                sharePhotoToFacebook();
            } else {
                showToastText(getString(R.string.please_register));
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

            showToastText(getString(R.string.deleted_success));
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

                visibilitySaveView();

                GetPlaces.updateDataBase(String.valueOf(mPlace.getId()),
                        String.valueOf(etPlaceNameEdit.getText()),
                        String.valueOf(etDescriptionEdit.getText()),
                        photoEdit,getContext());

                 elementsUpdated.elementSelected();
                 ((CountryActivity)getActivity()).setWasEdited(true);
                 showToastText(getString(R.string.editing_was_successful));
             } else {
                 showToastText(getString(R.string.enter_place_name));
             }
        });

        fabEdit.setOnClickListener(v13 -> {
            visibilityEditView();
            fabMenu.toggle();

            if(mPlace.getTitle() != null | mPlace.getDescription() != null) {
                etPlaceNameEdit.setText(mPlace.getTitle());
                etDescriptionEdit.setText(mPlace.getDescription());
            }
        });

        fabShare.setOnClickListener(v1 -> {

            if(AccessToken.getCurrentAccessToken() == null) {
                setViewVisibility(ivFacebook,View.INVISIBLE);
            }

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
                        showToastText("you have successfully registered");

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
                    showToastText(getString(R.string.you_canceled));
                }

                @Override
                public void onError(FacebookException exception) {
                    isOnline();
                    showToastText(String.valueOf(exception.getMessage()));
                }
        });

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

        try {
                bitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();

                content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
        }
        catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Alert!")
                    .setMessage(getString(R.string.please_take_a_picture))
                    .setCancelable(false)
                    .setNegativeButton("Ok",
                            (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
            Log.e("sharePhotoToFacebook",String.valueOf(e.getMessage()));
        }
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                progressBarVisibility(View.GONE);
                setViewVisibility(loginButton,View.VISIBLE);

                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.you_shared_the_photo),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                setViewVisibility(ivFacebook,View.VISIBLE);
            }

            @Override
            public void onCancel() {
                showToastText(getString(R.string.you_canceled));
                progressBarVisibility(View.GONE);
                setViewVisibility(loginButton,View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {

                isOnline();
                showToastText(String.valueOf(error.getMessage()));
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
            showToastText(getString(R.string.no_internet_connection));
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

        if(photoEdit != null) {
            outState.putString("photoEdit", photoEdit);
        }
        if(loginButton.isShown()) {
            outState.putBoolean("btnOutVisibility", true);
        }
        if(ivPhotoEdit.isShown()) {
            outState.putBoolean(SHOW_PHOTO_AFTER_EDIT, true);
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            btnOutIsActivated = savedInstanceState.getBoolean("button_out_activated");
            buttonOutIsActivated();

            isEditPhoto = savedInstanceState.getBoolean(SHOW_PHOTO_AFTER_EDIT);
            photoEdit = savedInstanceState.getString("photoEdit");
            showPhotoEdit();
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
    public void saveImagePhotoEdited() {
        ImageLoader.getInstance().displayImage("file:///" +photoEdit, ivPhoto);
    }
    private void showPhotoEdit() {

        if (isEditPhoto) {
            visibilityEditView();
        } else {
            visibilitySaveView();
        }
    }
    private void visibilityEditView() {
        setViewVisibility(tvTitle,View.GONE);
        setViewVisibility(tvDescription,View.GONE);
        setViewVisibility(tvPlaceName,View.GONE);

        setViewVisibility(etDescriptionEdit,View.VISIBLE);
        setViewVisibility(etPlaceNameEdit,View.VISIBLE);
        setViewVisibility(btnSaveEdit,View.VISIBLE);
        setViewVisibility(rlPhotoEdit,View.VISIBLE);
    }
    private void visibilitySaveView() {
        setViewVisibility(tvTitle,View.VISIBLE);
        setViewVisibility(tvDescription,View.VISIBLE);
        setViewVisibility(tvPlaceName,View.VISIBLE);

        setViewVisibility(etDescriptionEdit,View.GONE);
        setViewVisibility(etPlaceNameEdit,View.GONE);
        setViewVisibility(rlPhotoEdit,View.GONE);
        setViewVisibility(btnSaveEdit,View.GONE);
    }
    private void showToastText(String message) {
        toast.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
