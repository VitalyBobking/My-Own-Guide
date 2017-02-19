package diploma.edu.zp.guide_my_own.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import diploma.edu.zp.guide_my_own.model.Place;

/**
 * Created by Val on 2/17/2017.
 */

public class FillDataBase {
    public static void fill(Context context, Place place) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHelper.FeedEntry.TITLE, place.getTitle());
            values.put(DBHelper.FeedEntry.DESCRIPTION, place.getDescription());
            values.put(DBHelper.FeedEntry.URL_PIC, place.getUrl_pic());
            values.put(DBHelper.FeedEntry.CITY, place.getPlaceName());
            values.put(DBHelper.FeedEntry.LATITUDE, place.getLatitude());
            values.put(DBHelper.FeedEntry.LONGITUDE, place.getLongitude());

            db.insert(DBHelper.FeedEntry.TABLE_NAME, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }
}
