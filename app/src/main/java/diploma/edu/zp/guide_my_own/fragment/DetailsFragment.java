package diploma.edu.zp.guide_my_own.fragment;



import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.concurrent.TimeUnit;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.model.Place;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Val on 3/6/2017.
 */

public class DetailsFragment extends Fragment {
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String SAVE_STATE_PLACE = "SAVE_STATE_PLACE";
    private Place mPlace;
    private BottomSheetBehavior mBottomSheetBehavior;
    public static final int LOGIN_FB = 2107;
    private ImageView ivPhoto;
    private ImageView ivInstagram, ivFaceBook;
    private Handler handler;
    private ProgressBar progressBar;
    private int count;
    private int max = 100;


    public static DetailsFragment newInstance(Place place) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ITEM, place);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        ivFaceBook = v.findViewById(R.id.ivFaceBook);
        ivInstagram = v.findViewById(R.id.ivInstagram);
        ImageView ivCloseBottomSheet = v.findViewById(R.id.ivCloseBottomSheet);

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvDescription = v.findViewById(R.id.tvDescription);
        TextView tvPlaceName = v.findViewById(R.id.tvPlaceName);

        View bottomSheet  = v.findViewById(R.id.bottomSheet);

        FloatingActionsMenu fabMenu = v.findViewById(R.id.fabMenu);
        FloatingActionButton fabShare = v.findViewById(R.id.fabShare);
        FloatingActionButton fabEdit = v.findViewById(R.id.fabEdit);
        FloatingActionButton fabDelete = v.findViewById(R.id.fabDelete);

        Button btnLogOut = v.findViewById(R.id.logOut);

        progressBar = v.findViewById(R.id.progressBar);
        handler = new Handler();

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setPeekHeight(0);

        btnLogOut.setOnClickListener(v4 -> {
           if (AccessToken.getCurrentAccessToken() != null) {
               //LoginManager.getInstance().logOut();

                String id = AccessToken.getCurrentAccessToken().getUserId();
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        id+"/permissions/",
                        null, HttpMethod.DELETE, graphResponse
                        -> LoginManager.getInstance().logOut()).executeAsync();

                Toast toast = Toast.makeText(getApplicationContext(),
                        "you out",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        });

        // настройка возможности скрыть элемент при свайпе вниз
       // mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                    fabMenu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fabShare.setOnClickListener(v1 -> {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            fabMenu.setVisibility(View.INVISIBLE);
            setViewVisibility(btnLogOut,View.VISIBLE);

        });

        ivCloseBottomSheet.setOnClickListener(v2 -> {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fabMenu.setVisibility(View.VISIBLE);
        });
        ivFaceBook.setOnClickListener(v3 -> {
            if (AccessToken.getCurrentAccessToken() == null) {
                FbFragment fbFragment = new FbFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.country_main, fbFragment);
                ft.addToBackStack(FbFragment.class.getName());
                ft.commit();

                Toast toast = Toast.makeText(getApplicationContext(),
                        "please register",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            } else   {
                setViewVisibility(ivFaceBook,View.INVISIBLE);
                setViewVisibility(ivInstagram,View.INVISIBLE);
                progressBarVisibility(View.VISIBLE);
                sharePhotoToFacebook();
            }
        });
        //ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(mPlace.getUrl_pic()));
        if (mPlace.getUrl_pic() != null) {
            ImageLoader.getInstance().displayImage("file:///"+mPlace.getUrl_pic(), ivPhoto);
            Log.e("------->Photo" ,String.valueOf(mPlace.getUrl_pic()));
        }
        tvTitle.setText(mPlace.getTitle());

        String desc = mPlace.getDescription();
        if (desc != null)
            tvDescription.setText(mPlace.getDescription());
        else
            tvDescription.setVisibility(View.GONE);

        tvPlaceName.setText(mPlace.getPlaceName());

        Thread t = new Thread(() -> {
            try {
                for (count = 1; count < max; count++) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    handler.post(updateProgress);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        return v;
    }
    // обновление ProgressBar
    Runnable updateProgress = () -> progressBar.setProgress(count);
    // show info
    Runnable showInfo = new Runnable() {
        public void run() {
            handler.postDelayed(showInfo, 1000);
        }
    };

    private void sharePhotoToFacebook(){
        Bitmap bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "You shared the photo",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                progressBarVisibility(View.GONE);
                setViewVisibility(ivFaceBook,View.VISIBLE);
                setViewVisibility(ivInstagram,View.VISIBLE);
                handler.removeCallbacks(showInfo);

            }

            @Override
            public void onCancel() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You canceled",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                progressBarVisibility(View.GONE);
                setViewVisibility(ivFaceBook,View.VISIBLE);
                setViewVisibility(ivInstagram,View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "please check your internet connection",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                progressBarVisibility(View.GONE);
                setViewVisibility(ivFaceBook,View.VISIBLE);
                setViewVisibility(ivInstagram,View.VISIBLE);
                Log.e("onError ---->", String.valueOf(error.getMessage()));
            }
        });

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
        ActionBar actionBar = ((CountryActivity)getActivity()).getSupportActionBar();

        if (mPlace != null && actionBar != null){
            actionBar.setTitle(mPlace.getTitle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_PLACE, mPlace);
    }


}
