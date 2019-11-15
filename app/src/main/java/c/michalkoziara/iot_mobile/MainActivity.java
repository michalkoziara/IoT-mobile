package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

//        getDeviceGroups(authToken);

        Map<String, String> testDeviceGroupProductKeyByNames = new HashMap<>();
        for (int i = 0; i < 350; i++) {
            testDeviceGroupProductKeyByNames.put(String.valueOf(i), String.valueOf(i));
        }
        DeviceGroupFragment deviceGroupFragment =
                (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                        viewPager,
                        0
                );

        deviceGroupFragment.setDeviceGroupProductKeyByNames(testDeviceGroupProductKeyByNames);
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
        setIsSensorOrExecutive(null);
        userGroupName = null;

        UserGroupFragment userGroupFragment =
                (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                        viewPager,
                        1
                );
        userGroupFragment.resetSelectedPosition();

        mainFragmentPageAdapter.isSensorOrExecutive = this.isSensorOrExecutive;
        mainFragmentPageAdapter.notifyDataSetChanged();
    }

    //
    //
    //        UserGroupListener
    //
    //

    @Override
    public void createUserGroups() {
        String authToken = getToken();
//
//        if (deviceGroupProductKey != null) {
//            getUserGroups(authToken, deviceGroupProductKey);
//        }

        List<String> testUserGroups = new ArrayList<>();
        for (int i = 0; i < 350; i++) {
            testUserGroups.add(String.valueOf(i));
        }


        if (mainFragmentPageAdapter.instantiateItem(
                viewPager,
                1
        ) instanceof UserGroupFragment) {
            UserGroupFragment userGroupFragment =
                    (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                            viewPager,
                            1
                    );

            userGroupFragment.setUserGroupNames(testUserGroups);
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

//        if (deviceGroupProductKey != null
//        && userGroupName != null
//        && isSensorOrExecutive != null
//        && isSensorOrExecutive.equals("executive")) {
//            getExecutiveDevices(authToken, deviceGroupProductKey, userGroupName);
//        }

        if ((isSensorOrExecutive != null && isSensorOrExecutive.equals("executive"))) {
            Map<String, String> testDeviceKeyByNames = new HashMap<>();
            for (int i = 0; i < 350; i++) {
                testDeviceKeyByNames.put(String.valueOf(i), String.valueOf(i));
            }

            mainFragmentPageAdapter.isSensorOrExecutive = "executive";
            setIsSensorOrExecutive("executive");
            ExecutiveDeviceFragment executiveDeviceFragment =
                    (ExecutiveDeviceFragment) mainFragmentPageAdapter.instantiateItem(
                            viewPager,
                            2
                    );
            executiveDeviceFragment.setExecutiveDeviceKeyByNames(testDeviceKeyByNames);
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

        startActivity(intent);
    }

    //
    //
    //        SensorListener
    //
    //

    @Override
    public void createSensors() {
        String authToken = getToken();

//        if (deviceGroupProductKey != null && userGroupName != null && isTimerOn()) {
//            getSensors(authToken, deviceGroupProductKey, userGroupName);
//        }

        if (isTimerOn()) {
            mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
            setIsSensorOrExecutive("sensor");

            SensorFragment sensorFragment =
                    (SensorFragment) mainFragmentPageAdapter.instantiateItem(
                            viewPager,
                            2
                    );

            Map<String, String> testSensorValuesByNames = new HashMap<>();
            for (int i = 0; i < 350; i++) {
                testSensorValuesByNames.put(String.valueOf(i), String.valueOf(i));
            }


            sensorFragment.setSensorValuesByNames(testSensorValuesByNames);
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

    private void getUserGroups(String authToken, String deviceGroupProductKey) {
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

                    UserGroupFragment userGroupFragment =
                            (UserGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                    viewPager,
                                    1
                            );
                    userGroupFragment.setUserGroupNames(userGroupNames);
                }
            }
        });

        sync.execute(Constants.hubs_url + "/" + deviceGroupProductKey + "/user-groups", authToken);
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

                    mainFragmentPageAdapter.isSensorOrExecutive = "executive";
                    ExecutiveDeviceFragment executiveDeviceFragment =
                            (ExecutiveDeviceFragment) mainFragmentPageAdapter.instantiateItem(
                                    viewPager,
                                    2
                            );
                    executiveDeviceFragment.setExecutiveDeviceKeyByNames(executiveDeviceKeyByNames);
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

                            sensorValuesByNames.put(
                                    sensorInfoJson.getString("name"),
                                    sensorInfoJson.getString("sensorReadingValue")
                            );
                        }
                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
                    SensorFragment sensorFragment =
                            (SensorFragment) mainFragmentPageAdapter.instantiateItem(
                                    viewPager,
                                    2
                            );
                    sensorFragment.setSensorValuesByNames(sensorValuesByNames);
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
