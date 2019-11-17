package c.michalkoziara.iot_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }

    DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        dbHelper.close();
    }

    void insertDeviceGroup(String productKey, String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.PRODUCT_KEY, productKey);
        contentValue.put(DatabaseHelper.NAME, name);
        database.insert(DatabaseHelper.DEVICE_GROUP_TABLE_NAME, null, contentValue);
    }

    Cursor fetchDeviceGroup() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.PRODUCT_KEY, DatabaseHelper.NAME};
        return database.query(
                DatabaseHelper.DEVICE_GROUP_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }

    Cursor fetchDeviceGroupByProductKey(String productKey) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.PRODUCT_KEY, DatabaseHelper.NAME};
        return database.query(
                DatabaseHelper.DEVICE_GROUP_TABLE_NAME,
                columns,
                DatabaseHelper.PRODUCT_KEY + "=\"" + productKey + "\"",
                null,
                null,
                null,
                null
        );
    }

    void deleteDeviceGroups() {
        database.delete(DatabaseHelper.DEVICE_GROUP_TABLE_NAME, null, null);
    }

    void insertUserGroup(String name, long _device_group_id) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.DEVICE_GROUP_ID, _device_group_id);
        database.insert(DatabaseHelper.USER_GROUP_TABLE_NAME, null, contentValue);
    }

    Cursor fetchUserGroupByDeviceGroupId(long _id) {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.NAME, DatabaseHelper.DEVICE_GROUP_ID};
        return database.query(
                DatabaseHelper.USER_GROUP_TABLE_NAME,
                columns,
                DatabaseHelper.DEVICE_GROUP_ID + "=" + _id,
                null,
                null,
                null,
                null
        );
    }

    void deleteUserGroupsByDeviceGroupId(long _id) {
        database.delete(
                DatabaseHelper.USER_GROUP_TABLE_NAME,
                DatabaseHelper.DEVICE_GROUP_ID + "=" + _id,
                null
        );
    }

    void deleteUserGroups() {
        database.delete(DatabaseHelper.USER_GROUP_TABLE_NAME, null, null);
    }
}