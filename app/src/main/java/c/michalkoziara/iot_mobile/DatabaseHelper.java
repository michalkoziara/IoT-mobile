package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DEVICE_GROUP_TABLE_NAME = "DEVICE_GROUP";
    static final String USER_GROUP_TABLE_NAME = "USER_GROUP";

    static final String _ID = "_id";
    static final String DEVICE_GROUP_ID = "device_group_id";
    static final String PRODUCT_KEY = "product_key";
    static final String NAME = "name";

    private static final String DB_NAME = "IOT_MOBILE.DB";
    private static final int DB_VERSION = 5;

    private static final String CREATE_DEVICE_GROUP_TABLE = "CREATE TABLE " + DEVICE_GROUP_TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRODUCT_KEY + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL);";

    private static final String CREATE_USER_GROUP_TABLE = "CREATE TABLE " + USER_GROUP_TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + DEVICE_GROUP_ID + " INTEGER NOT NULL,"
            + " FOREIGN KEY(" + DEVICE_GROUP_ID + ") REFERENCES " + USER_GROUP_TABLE_NAME + "(" + _ID + "));";

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEVICE_GROUP_TABLE);
        db.execSQL(CREATE_USER_GROUP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DEVICE_GROUP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_TABLE_NAME);
        onCreate(db);
    }
}