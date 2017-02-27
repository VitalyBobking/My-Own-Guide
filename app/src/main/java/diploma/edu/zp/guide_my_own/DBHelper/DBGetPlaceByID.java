package diploma.edu.zp.guide_my_own.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import diploma.edu.zp.guide_my_own.model.Place;

/**
 * Created by Val on 2/27/2017.
 */

public class DBGetPlaceByID {
    public static Place getPlace(Context context, int id) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Place place = new Place();

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

            cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME + " WHERE " + DBHelper.FeedEntry._ID + " = ?", new String[] {String.valueOf(id)}, null);

            if (cursor.moveToFirst()) {
                do {
                    place.setId(cursor.getInt(PlaceScheme._ID.getIndex()));
                    place.setTitle(cursor.getString(PlaceScheme.TITLE.getIndex()));
                    place.setDescription(cursor.getString(PlaceScheme.DESCRIPTION.getIndex()));
                    place.setUrl_pic(cursor.getString(PlaceScheme.URL_PIC.getIndex()));
                    place.setLatitude(cursor.getDouble(PlaceScheme.LATITUDE.getIndex()));
                    place.setLongitude(cursor.getDouble(PlaceScheme.LONGITUDE.getIndex()));
                    place.setPlaceName(cursor.getString(PlaceScheme.PLACE.getIndex()));
                    place.setCountry(cursor.getString(PlaceScheme.COUNTRY.getIndex()));

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

        return place;
    }
}
