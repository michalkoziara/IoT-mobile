package c.michalkoziara.iot_mobile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        UserGroupFragment.UserGroupListener,
        DeviceGroupFragment.DeviceGroupListener,
        ExecutiveDeviceFragment.ExecutiveDeviceListener,
        SensorFragment.SensorListener {
    MainFragmentPageAdapter mainFragmentPageAdapter;
    ViewPager viewPager;

    Map<String, String> deviceGroupProductKeyByNames;
    String deviceGroupProductKey;
    Boolean isDeviceGroupSelected = false;

    List<String> userGroupNames;
    String userGroupName;
    Boolean isUserGroupSelected = false;

    String isSensorOrExecutive;

    Map<String, String> executiveDeviceKeyByNames;

    Boolean isCurrentlyReloading = false;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (deviceGroupProductKey != null) {
            outState.putString("deviceGroupProductKey", deviceGroupProductKey);
            outState.putBoolean("isDeviceGroupSelected", isDeviceGroupSelected);

            outState.putString("userGroupName", userGroupName);
            outState.putBoolean("isUserGroupSelected", isUserGroupSelected);

            outState.putString("isSensorOrExecutive", isSensorOrExecutive);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            deviceGroupProductKey = savedInstanceState.getString("deviceGroupProductKey");
            isDeviceGroupSelected = savedInstanceState.getBoolean("isDeviceGroupSelected");

            userGroupName = savedInstanceState.getString("userGroupName");
            isUserGroupSelected = savedInstanceState.getBoolean("isUserGroupSelected");

            isSensorOrExecutive = savedInstanceState.getString("isSensorOrExecutive");
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.action_log_out) {
                            MainActivity.this.getSharedPreferences("authorization", 0).edit().clear().apply();

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        if (item.getItemId() == R.id.action_reload) {
                            int currentPage = viewPager.getCurrentItem();
                            isCurrentlyReloading = true;

                            if (currentPage == 0) {
                                resetUserGroupAndIsExecutiveOrSensor();
                                if (mainFragmentPageAdapter != null) {
                                    DeviceGroupFragment deviceGroupFragment =
                                            (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                                    viewPager,
                                                    0
                                            );
                                    deviceGroupFragment.resetSelectedPosition();

                                    isDeviceGroupSelected = false;
                                    mainFragmentPageAdapter.isDeviceGroupSelected = false;
                                    mainFragmentPageAdapter.notifyDataSetChanged();
                                }
                                DBManager dbManager = new DBManager(MainActivity.this);
                                dbManager.open();
                                dbManager.deleteUserGroups();
                                dbManager.deleteDeviceGroups();
                                dbManager.close();

                                createDeviceGroups();
                            } else if (currentPage == 1) {
                                if (mainFragmentPageAdapter != null) {
                                    UserGroupFragment userGroupFragment =
                                            (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                                    viewPager,
                                                    1
                                            );
                                    userGroupFragment.resetSelectedPosition();

                                    isSensorOrExecutive = null;
                                    mainFragmentPageAdapter.isSensorOrExecutive = null;
                                    mainFragmentPageAdapter.notifyDataSetChanged();
                                }
                                DBManager dbManager = new DBManager(MainActivity.this);
                                dbManager.open();
                                dbManager.deleteUserGroups();
                                dbManager.close();

                                createUserGroups();
                            } else if (currentPage == 2 && "executive".equals(isSensorOrExecutive)) {
                                createExecutiveDevices();
                            } else if (currentPage == 2 && "sensor".equals(isSensorOrExecutive)) {
                                createSensors();
                            }

                            isCurrentlyReloading = false;
                        }
                        return false;
                    }
                }
        );

        viewPager = findViewById(R.id.tab_layout_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mainFragmentPageAdapter = new MainFragmentPageAdapter(
                fragmentManager,
                MainActivity.this
        );
        mainFragmentPageAdapter.isDeviceGroupSelected = isDeviceGroupSelected;
        mainFragmentPageAdapter.isSensorOrExecutive = isSensorOrExecutive;

        viewPager.setAdapter(
                mainFragmentPageAdapter
        );

        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof UserGroupFragment) {
            UserGroupFragment userGroupFragment = (UserGroupFragment) fragment;
            userGroupFragment.setUserGroupListener(this);
        }
        if (fragment instanceof DeviceGroupFragment) {
            DeviceGroupFragment deviceGroupFragment = (DeviceGroupFragment) fragment;
            deviceGroupFragment.setDeviceGroupListener(this);
        }
        if (fragment instanceof ExecutiveDeviceFragment) {
            ExecutiveDeviceFragment executiveDeviceFragment = (ExecutiveDeviceFragment) fragment;
            executiveDeviceFragment.setExecutiveDeviceListener(this);
        }
        if (fragment instanceof SensorFragment) {
            SensorFragment sensorFragment = (SensorFragment) fragment;
            sensorFragment.setSensorListener(this);
        }
    }

    //
    //
    //        DeviceGroupListener
    //
    //

    @Override
    public void createDeviceGroups() {
        String authToken = getToken();

        getDeviceGroups(authToken);
    }

    @Override
    public void onDeviceGroupClick(String deviceGroupName) {
        if (deviceGroupProductKeyByNames != null && deviceGroupProductKeyByNames.containsKey(deviceGroupName)) {
            isDeviceGroupSelected = true;
            deviceGroupProductKey = deviceGroupProductKeyByNames.get(deviceGroupName);

            if (mainFragmentPageAdapter != null) {
                mainFragmentPageAdapter.isDeviceGroupSelected = true;
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public void passDeviceGroupProductKeyByNamesToMain(Map<String, String> deviceGroupProductKeyByNames) {
        this.deviceGroupProductKeyByNames = deviceGroupProductKeyByNames;
    }

    @Override
    public void resetUserGroupAndIsExecutiveOrSensor() {
        if (isDeviceGroupSelected) {
            setIsSensorOrExecutive(null);
            userGroupName = null;

            UserGroupFragment userGroupFragment =
                    (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                            viewPager,
                            1
                    );
            userGroupFragment.resetSelectedPosition();

            mainFragmentPageAdapter.isSensorOrExecutive = null;
            mainFragmentPageAdapter.notifyDataSetChanged();
        }
    }

    //
    //
    //        UserGroupListener
    //
    //

    @Override
    public void createUserGroups() {
        String authToken = getToken();

        if (deviceGroupProductKey != null) {
            getUserGroups(authToken, deviceGroupProductKey);
        }
    }

    @Override
    public void passUserGroupNamesToMain(List<String> userGroupNames) {
        this.userGroupNames = userGroupNames;
    }

    @Override
    public void onUserGroupClick(String userGroupName) {
        if (userGroupNames != null && userGroupNames.contains(userGroupName)) {
            this.isUserGroupSelected = true;
            this.userGroupName = userGroupName;
        }
    }

    @Override
    public View.OnClickListener createSensorButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
                setIsSensorOrExecutive("sensor");
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(2);
            }
        };
    }

    @Override
    public View.OnClickListener createExecutiveButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainFragmentPageAdapter.isSensorOrExecutive = "executive";
                setIsSensorOrExecutive("executive");
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(2);
            }
        };
    }

    //
    //
    //        ExecutiveDeviceListener
    //
    //

    @Override
    public void createExecutiveDevices() {
        String authToken = getToken();

        if (deviceGroupProductKey != null
                && userGroupName != null
                && isSensorOrExecutive != null
                && isSensorOrExecutive.equals("executive")) {
            getExecutiveDevices(authToken, deviceGroupProductKey, userGroupName);
        }
    }

    @Override
    public void passExecutiveDeviceKeyByNamesToMain(Map<String, String> executiveDeviceKeyByNames) {
        this.executiveDeviceKeyByNames = executiveDeviceKeyByNames;
    }

    @Override
    public void openControllerActivity(String executiveDeviceName, String executiveDeviceKey) {
        Intent intent = new Intent(getApplicationContext(), ControllerActivity.class);
        intent.putExtra("executiveDeviceKey", executiveDeviceKey);
        intent.putExtra("executiveDeviceName", executiveDeviceName);
        intent.putExtra("deviceGroupProductKey", deviceGroupProductKey);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.frame_layout).setTransitionName("frame_layout");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    Pair.create(findViewById(R.id.frame_layout), "frame_layout")
            );
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    //
    //
    //        SensorListener
    //
    //

    @Override
    public void createSensors() {
        String authToken = getToken();

        if (deviceGroupProductKey != null && userGroupName != null && isTimerOn()) {
            getSensors(authToken, deviceGroupProductKey, userGroupName);
        }
    }

    @Override
    public Boolean isTimerOn() {
        return (isSensorOrExecutive != null && isSensorOrExecutive.equals("sensor"));
    }

    //
    //
    //  MainActivity
    //
    //

    private void setIsSensorOrExecutive(String isSensorOrExecutive) {
        this.isSensorOrExecutive = isSensorOrExecutive;
    }

    private void getDeviceGroups(String authToken) {
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetchDeviceGroup();
        if (!isCurrentlyReloading && cursor != null && cursor.getCount() > 0) {
            Map<String, String> deviceGroupProductKeyByNames = new HashMap<>();

            cursor.moveToFirst();

            String product_key = cursor.getString(cursor.getColumnIndex("product_key"));
            String name = cursor.getString(cursor.getColumnIndex("name"));

            deviceGroupProductKeyByNames.put(name, product_key);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                product_key = cursor.getString(cursor.getColumnIndex("product_key"));

                deviceGroupProductKeyByNames.put(name, product_key);
                cursor.moveToNext();
            }

            cursor.close();
            dbManager.close();

            DeviceGroupFragment deviceGroupFragment =
                    (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                            viewPager,
                            0
                    );
            deviceGroupFragment.setDeviceGroupProductKeyByNames(deviceGroupProductKeyByNames);
        } else {
            if (cursor != null) {
                cursor.close();
            }
            dbManager.close();

            AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
                @Override
                public String createRequest(String[] params) {
                    return HttpConnectionFactory.createGetConnection(params[0], params[1]);
                }

                @Override
                public void processFinish(String output) {
                    if (output == null) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                    } else {
                        Map<String, String> deviceGroupProductKeyByNames = new HashMap<>();
                        try {
                            JSONArray deviceGroupListJson = new JSONArray(output);
                            for (int i = 0; i < deviceGroupListJson.length(); i++) {
                                JSONObject deviceGroupInfoJson = deviceGroupListJson.getJSONObject(i);

                                deviceGroupProductKeyByNames.put(
                                        deviceGroupInfoJson.getString("name"),
                                        deviceGroupInfoJson.getString("productKey")
                                );
                            }
                        } catch (JSONException e) {
                            displaySnackbar(getString(R.string.main_menu_load_failed_message));
                            e.printStackTrace();
                        }

                        DBManager dbManager = new DBManager(MainActivity.this);
                        dbManager.open();

                        Cursor cursor = dbManager.fetchDeviceGroup();
                        if (cursor == null || cursor.getCount() == 0) {
                            if (cursor != null) {
                                cursor.close();
                            }

                            for (Map.Entry<String, String> entry : deviceGroupProductKeyByNames.entrySet()) {
                                String name = entry.getKey();
                                String product_key = entry.getValue();

                                dbManager.insertDeviceGroup(product_key, name);
                            }
                        }
                        dbManager.close();

                        DeviceGroupFragment deviceGroupFragment =
                                (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                        viewPager,
                                        0
                                );
                        deviceGroupFragment.setDeviceGroupProductKeyByNames(deviceGroupProductKeyByNames);
                    }
                }
            });

            sync.execute(Constants.hubs_url, authToken);
        }
    }

    private void getUserGroups(String authToken, final String deviceGroupProductKey) {
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursorDeviceGroups = dbManager.fetchDeviceGroupByProductKey(deviceGroupProductKey);
        Long deviceGroupId = null;
        if (cursorDeviceGroups != null && cursorDeviceGroups.getCount() > 0) {
            cursorDeviceGroups.moveToFirst();

            deviceGroupId = cursorDeviceGroups.getLong(cursorDeviceGroups.getColumnIndex("_id"));
            cursorDeviceGroups.close();
        }

        Cursor cursorUserGroups = null;
        if (deviceGroupId != null) {
            cursorUserGroups = dbManager.fetchUserGroupByDeviceGroupId(deviceGroupId);
        }

        if (!isCurrentlyReloading && cursorUserGroups != null && cursorUserGroups.getCount() > 0) {
            List<String> userGroupNames = new ArrayList<>();

            cursorUserGroups.moveToFirst();

            String name = cursorUserGroups.getString(cursorUserGroups.getColumnIndex("name"));
            userGroupNames.add(name);

            cursorUserGroups.moveToNext();

            while (!cursorUserGroups.isAfterLast()) {
                name = cursorUserGroups.getString(cursorUserGroups.getColumnIndex("name"));
                userGroupNames.add(name);
                cursorUserGroups.moveToNext();
            }

            cursorUserGroups.close();
            dbManager.close();

            if (mainFragmentPageAdapter != null
                    && mainFragmentPageAdapter.isDeviceGroupSelected) {
                UserGroupFragment userGroupFragment =
                        (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                viewPager,
                                1
                        );
                userGroupFragment.setUserGroupNames(userGroupNames);
            }
        } else {
            if (cursorUserGroups != null) {
                cursorUserGroups.close();
            }
            dbManager.close();

            AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
                @Override
                public String createRequest(String[] params) {
                    return HttpConnectionFactory.createGetConnection(params[0], params[1]);
                }

                @Override
                public void processFinish(String output) {
                    if (output == null) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                    } else {
                        List<String> userGroupNames = new ArrayList<>();
                        try {
                            JSONArray userGroupListJson = new JSONArray(output);
                            for (int i = 0; i < userGroupListJson.length(); i++) {
                                userGroupNames.add(userGroupListJson.getString(i));
                            }
                        } catch (JSONException e) {
                            displaySnackbar(getString(R.string.main_menu_load_failed_message));
                            e.printStackTrace();
                        }

                        DBManager dbManager = new DBManager(MainActivity.this);
                        dbManager.open();

                        Cursor cursorDeviceGroups = dbManager.fetchDeviceGroupByProductKey(deviceGroupProductKey);
                        Long deviceGroupId = null;
                        if (cursorDeviceGroups != null && cursorDeviceGroups.getCount() > 0) {
                            cursorDeviceGroups.moveToFirst();

                            deviceGroupId = cursorDeviceGroups.getLong(cursorDeviceGroups.getColumnIndex("_id"));
                            cursorDeviceGroups.close();
                        }

                        Cursor cursorUserGroups = null;
                        if (deviceGroupId != null) {
                            cursorUserGroups = dbManager.fetchUserGroupByDeviceGroupId(deviceGroupId);
                        }

                        if (deviceGroupId != null && (cursorUserGroups == null || cursorUserGroups.getCount() == 0)) {
                            if (cursorUserGroups != null) {
                                cursorUserGroups.close();
                            }

                            for (String userGroupName : userGroupNames) {
                                dbManager.insertUserGroup(userGroupName, deviceGroupId);
                            }
                        }
                        dbManager.close();

                        if (mainFragmentPageAdapter != null
                                && mainFragmentPageAdapter.isDeviceGroupSelected) {
                            UserGroupFragment userGroupFragment =
                                    (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                            viewPager,
                                            1
                                    );
                            userGroupFragment.setUserGroupNames(userGroupNames);
                        }
                    }
                }
            });

            sync.execute(Constants.hubs_url + "/" + deviceGroupProductKey + "/user-groups", authToken);
        }
    }

    private void getExecutiveDevices(String authToken, String deviceGroupProductKey, String userGroupName) {
        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public String createRequest(String[] params) {
                return HttpConnectionFactory.createGetConnection(params[0], params[1]);
            }

            @Override
            public void processFinish(String output) {
                if (output == null) {
                    displaySnackbar(getString(R.string.main_menu_load_failed_message));
                } else {
                    Map<String, String> executiveDeviceKeyByNames = new HashMap<>();
                    try {
                        JSONArray executiveDeviceListJson = new JSONArray(output);
                        for (int i = 0; i < executiveDeviceListJson.length(); i++) {
                            JSONObject executiveDeviceInfoJson = executiveDeviceListJson.getJSONObject(i);

                            executiveDeviceKeyByNames.put(
                                    executiveDeviceInfoJson.getString("name"),
                                    executiveDeviceInfoJson.getString("deviceKey")
                            );
                        }
                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    if ("executive".equals(mainFragmentPageAdapter.isSensorOrExecutive)) {
                        mainFragmentPageAdapter.isSensorOrExecutive = "executive";
                        ExecutiveDeviceFragment executiveDeviceFragment =
                                (ExecutiveDeviceFragment) mainFragmentPageAdapter.instantiateItem(
                                        viewPager,
                                        2
                                );
                        executiveDeviceFragment.setExecutiveDeviceKeyByNames(executiveDeviceKeyByNames);
                    }
                }
            }
        });

        String url = Constants.hubs_url + "/" + deviceGroupProductKey
                + "/user-groups/" + userGroupName
                + "/executive-devices";

        sync.execute(url, authToken);
    }

    private void getSensors(String authToken, String deviceGroupProductKey, String userGroupName) {
        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public String createRequest(String[] params) {
                return HttpConnectionFactory.createGetConnection(params[0], params[1]);
            }

            @Override
            public void processFinish(String output) {
                if (output == null) {
                    displaySnackbar(getString(R.string.main_menu_load_failed_message));
                } else {
                    Map<String, String> sensorValuesByNames = new HashMap<>();
                    try {
                        JSONArray sensorListJson = new JSONArray(output);
                        for (int i = 0; i < sensorListJson.length(); i++) {
                            JSONObject sensorInfoJson = sensorListJson.getJSONObject(i);

                            if (!sensorInfoJson.getBoolean("isActive")) {
                                sensorValuesByNames.put(
                                        sensorInfoJson.getString("name"),
                                        getString(R.string.sensor_not_active)
                                );
                            } else {
                                sensorValuesByNames.put(
                                        sensorInfoJson.getString("name"),
                                        sensorInfoJson.getString("sensorReadingValue")
                                );
                            }
                        }
                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    if ("sensor".equals(mainFragmentPageAdapter.isSensorOrExecutive)) {
                        mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
                        SensorFragment sensorFragment =
                                (SensorFragment) mainFragmentPageAdapter.instantiateItem(
                                        viewPager,
                                        2
                                );
                        sensorFragment.setSensorValuesByNames(sensorValuesByNames);
                    }
                }
            }
        });

        String url = Constants.hubs_url + "/" + deviceGroupProductKey
                + "/user-groups/" + userGroupName
                + "/sensors";

        sync.execute(url, authToken);
    }

    private void displaySnackbar(String message) {
        View contextView = findViewById(R.id.tab_layout_view_pager);
        Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
    }

    private String getToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("authorization", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("authToken", "");

        if (authToken == null || authToken.equals("")) {
            View contextView = findViewById(R.id.tab_layout_view_pager);
            Snackbar.make(contextView, R.string.main_menu_login_failed_message, Snackbar.LENGTH_LONG).show();
        }
        return authToken;
    }
}
