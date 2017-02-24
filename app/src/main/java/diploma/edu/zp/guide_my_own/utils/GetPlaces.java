package diploma.edu.zp.guide_my_own.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import diploma.edu.zp.guide_my_own.DBHelper.DBHelper;
import diploma.edu.zp.guide_my_own.DBHelper.PlaceScheme;
import diploma.edu.zp.guide_my_own.model.Place;

/**
 * Created by valera on 2/18/17.
 */

public class GetPlaces {
    public static List<Place> getPlaces(Context context) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Place> places = new ArrayList<>();

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME + " GROUP BY " + DBHelper.FeedEntry.CITY, null);

            if (cursor.moveToFirst()) {
                do {
                    Place place = new Place();
                    place.setId(cursor.getInt(PlaceScheme.ID.getIndex()));
                    place.setTitle(cursor.getString(PlaceScheme.TITLE.getIndex()));
                    place.setDescription(cursor.getString(PlaceScheme.DESCRIPTION.getIndex()));
                    place.setUrl_pic(cursor.getString(PlaceScheme.URL_PIC.getIndex()));
                    place.setLatitude(cursor.getDouble(PlaceScheme.LATITUDE.getIndex()));
                    place.setLongitude(cursor.getDouble(PlaceScheme.LONGITUDE.getIndex()));
                    place.setPlaceName(cursor.getString(PlaceScheme.CITY.getIndex()));

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
}
