package diploma.edu.zp.guide_my_own.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by valera on 5/4/17.
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

    public static boolean delete(Context context, String country) {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            String table = DBHelper.FeedEntry.TABLE_NAME;
            String whereClause = DBHelper.FeedEntry.COUNTRY + "=?";
            String[] whereArgs = new String[]{String.valueOf(country)};
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
}
