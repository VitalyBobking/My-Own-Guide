package diploma.edu.zp.guide_my_own.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * craeted by Vitalii
 */

public class DeletePlace {
    public static boolean delete(Context context, int id) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            String table = DBHelper.FeedEntry.TABLE_NAME;
            String whereClause = "_id=?";
            String[] whereArgs = new String[]{String.valueOf(id)};
            return db.delete(table, whereClause, whereArgs) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public static List<Integer> deleteCountry(Context context, String country) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        boolean isDeleted = false;
        List<Integer> ids = new ArrayList<>();
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME + " " +
                    "WHERE " + DBHelper.FeedEntry.COUNTRY + " = ?", new String[] {country}, null);
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getInt(PlaceScheme._ID.getIndex()));
                } while (cursor.moveToNext());
            }
            cursor.close();
            String args = TextUtils.join(", ", ids);
            db.execSQL(String.format("DELETE FROM " + DBHelper.FeedEntry.TABLE_NAME + " WHERE " + DBHelper
                    .FeedEntry._ID + " IN (%s);", args));
            isDeleted = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            if (db != null) {
                db.close();
            }
        }
        if (isDeleted && ids.size() > 0) {
            return ids;
        } else {
            return null;
        }
    }
}
