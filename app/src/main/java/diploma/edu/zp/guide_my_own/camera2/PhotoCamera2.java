package diploma.edu.zp.guide_my_own.camera2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import diploma.edu.zp.guide_my_own.R;

public class PhotoCamera2 extends Fragment {

    private ImageView ivPhoto;
    private String photo;
    private Bundle args;
    private Bitmap bm;
    public static final String BUNDLE_DATA = "photo";
    private static final String SAVE_IMAGE = "saveImage";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_photo_camera2, container, false);
        ivPhoto =  v.findViewById(R.id.imagePhoto);
        args = getArguments();
        photo = args.getString(BUNDLE_DATA);

        if (photo != null ) {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photo, bounds);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            bm = BitmapFactory.decodeFile(photo, opts);

            ivPhoto.setAdjustViewBounds(true);
            ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            ivPhoto.setImageBitmap(bm);
        } else {
            Log.e("------->Unfortunately", photo);
        }
        return v;
    }

   @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photo != null) {
            outState.putString(SAVE_IMAGE, photo);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            photo = savedInstanceState.getString(SAVE_IMAGE);
        }
    }


}
