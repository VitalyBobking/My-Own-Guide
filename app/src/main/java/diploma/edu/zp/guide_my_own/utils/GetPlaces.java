package diploma.edu.zp.guide_my_own.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import diploma.edu.zp.guide_my_own.DBHelper.DBHelper;
import diploma.edu.zp.guide_my_own.DBHelper.PlaceScheme;
import diploma.edu.zp.guide_my_own.model.Place;

/**
 * Created by valera on 2/18/17.
 */

public class GetPlaces {

    public static List<Place> getPlaces(Context context, boolean is_group_by, String where) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Place> places = new ArrayList<>();

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

            if (where != null)
                cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME + " WHERE " + DBHelper.FeedEntry.COUNTRY + (" =?"), new String[] {where}, null);
            else if (is_group_by)
                cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME + " GROUP BY " + DBHelper.FeedEntry.COUNTRY, null);
            else
                cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                do {
                    Place place = new Place();
                    place.setId(cursor.getInt(PlaceScheme._ID.getIndex()));
                    place.setTitle(cursor.getString(PlaceScheme.TITLE.getIndex()));
                    place.setDescription(cursor.getString(PlaceScheme.DESCRIPTION.getIndex()));
                    place.setUrl_pic(cursor.getString(PlaceScheme.URL_PIC.getIndex()));
                    place.setLatitude(cursor.getDouble(PlaceScheme.LATITUDE.getIndex()));
                    place.setLongitude(cursor.getDouble(PlaceScheme.LONGITUDE.getIndex()));
                    place.setPlaceName(cursor.getString(PlaceScheme.PLACE.getIndex()));
                    place.setCountry(cursor.getString(PlaceScheme.COUNTRY.getIndex()));

                    places.add(place);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

            if (db != null)
                db.close();

            if (dbHelper != null)
                dbHelper.close();
        }

        return places;


    }
    public static void updateDataBase (String id, String title, String desc, String url_pic, Context context) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        String whereClause = "_id = ?";
        String[] whereArgs = new String[] {"" + id };

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

            ContentValues cv = new ContentValues();

            if(title != null) {
                cv.put("title", title);
            }
            if(desc != null) {
                cv.put("desc", desc);
            }
            if(url_pic != null) {
                cv.put("url_pic",url_pic);
            }

            int count = db.update("my_places", cv, whereClause,whereArgs);
            Log.e("DB", String.valueOf(count));

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (db != null)
                db.close();

            if (dbHelper != null)
                dbHelper.close();
        }

    }

}
