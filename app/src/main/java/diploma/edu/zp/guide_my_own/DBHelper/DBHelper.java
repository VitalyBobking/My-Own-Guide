package diploma.edu.zp.guide_my_own.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Val on 2/17/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myDB";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private static final String SQL_CREATE_PLACES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.ID + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.URL_PIC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    FeedEntry.LONGITUDE + DOUBLE_TYPE + COMMA_SEP + "" + TEXT_TYPE +" )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLACES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("onDowngrade", "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "my_places";
        static final String ID = "id";
        static final String TITLE = "title";
        static final String DESCRIPTION = "desc";
        static final String URL_PIC = "url_pic";
        static final String LATITUDE = "lat";
        static final String LONGITUDE = "lon";
    }
}