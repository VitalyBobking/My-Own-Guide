package diploma.edu.zp.guide_my_own.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.getbase.floatingactionbutton.FloatingActionButton;


import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.model.Place;
/**
 * Created by Val on 3/6/2017.
 */

public class DetailsFragment extends Fragment {
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String SAVE_STATE_PLACE = "SAVE_STATE_PLACE";
    private Place mPlace;
    private BottomSheetBehavior mBottomSheetBehavior;


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

        ImageView ivPhoto = v.findViewById(R.id.ivPhoto);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvDescription = v.findViewById(R.id.tvDescription);
        TextView tvPlaceName = v.findViewById(R.id.tvPlaceName);

        FloatingActionButton fabShare = v.findViewById(R.id.fabShare);
        FloatingActionButton fabEdit = v.findViewById(R.id.fabEdit);
        FloatingActionButton fabDelete = v.findViewById(R.id.fabDelete);

        View bottomSheet  = v.findViewById(R.id.bottomSheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(250);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // настройка возможности скрыть элемент при свайпе вниз
       // mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });


        //ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(mPlace.getUrl_pic()));
        if (mPlace.getUrl_pic() != null) {
            ImageLoader.getInstance().displayImage("file:///"+mPlace.getUrl_pic(), ivPhoto);
            Log.e("------->Photo" ,String.valueOf(mPlace.getUrl_pic()));
            Log.e("------->Photo" ,String.valueOf(ivPhoto));
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
