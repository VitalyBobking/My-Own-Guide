package diploma.edu.zp.guide_my_own.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import rx.Observable;

/**
 * Created by Val on 2/28/2017.
 */

public class CreateBitmapFromPath {
    public static Observable<Bitmap> loadImage(String path) {
        Bitmap b = null;
        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Observable.just(b);
        //return b;
    }
}
