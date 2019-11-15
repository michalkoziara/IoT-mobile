package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControllerActivity extends AppCompatActivity {

    private Map<String, Object> deviceInfo = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_controller);

        Toolbar toolbar = findViewById(R.id.controller_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String executiveDeviceName = "";
        String executiveDeviceKey = "";
        String deviceGroupProductKey = "";

        Intent intent = getIntent();
        if (intent != null) {
            executiveDeviceName = intent.getExtras() != null
                    ? intent.getExtras().getString("executiveDeviceName")
                    : "";

            executiveDeviceKey = intent.getExtras() != null
                    ? intent.getExtras().getString("executiveDeviceKey")
                    : "";

            deviceGroupProductKey = intent.getExtras() != null
                    ? intent.getExtras().getString("deviceGroupProductKey")
                    : "";
        }

        toolbar.setTitle(executiveDeviceName);

        String authToken = getToken();
        if (authToken != null) {
//            getExecutiveDevice(authToken, deviceGroupProductKey, executiveDeviceKey);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.action_log_out) {
                            ControllerActivity.this.getSharedPreferences("authorization", 0).edit().clear().apply();

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        return false;
                    }
                }
        );

        Map<String, Object> testData = new HashMap<>();
        testData.put("state", "test state");

        displayDeviceInfo(testData);
    }


    private void getExecutiveDevice(String authToken, String deviceGroupProductKey, String executiveDeviceKey) {
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
                    Map<String, Object> deviceInfo = new HashMap<>();

                    try {
                        JSONObject deviceInfoJson = new JSONObject(output);
                        Iterator<String> keys = deviceInfoJson.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();

                            if (deviceInfoJson.get(key) != null) {
                                deviceInfo.put(key, deviceInfoJson.get(key));
                            }
                        }

                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    setDeviceInfo(deviceInfo);
                }
            }
        });

        String url = Constants.hubs_url + "/" + deviceGroupProductKey
                + "/executive-devices/" + executiveDeviceKey;

        sync.execute(url, authToken);
    }

    private void setDeviceInfo(Map<String, Object> deviceInfo) {
        this.deviceInfo = deviceInfo;
        displayDeviceInfo(deviceInfo);
    }

    private void displayDeviceInfo(Map<String, Object> deviceInfo) {
        ScrollView deviceInfoScrollView = findViewById(R.id.controller_scrollview);
        deviceInfoScrollView.setVisibility(View.VISIBLE);

        if (deviceInfo != null) {
            MaterialTextView infoText = findViewById(R.id.device_state);
            if (infoText != null && deviceInfo.get("state") != null) {
                infoText.setText(String.valueOf(deviceInfo.get("state")));
            }

            infoText = findViewById(R.id.device_is_updated);
            if (infoText != null && deviceInfo.get("isUpdated") != null) {
                Boolean isUpdated = (Boolean) deviceInfo.get("isUpdated");

                if (isUpdated) {
                    infoText.setText(getString(R.string.yes));
                } else {
                    infoText.setText(getString(R.string.no));
                }
            }

            infoText = findViewById(R.id.device_is_active);
            if (infoText != null && deviceInfo.get("isActive") != null) {
                Boolean isActive = (Boolean) deviceInfo.get("isActive");

                if (isActive) {
                    infoText.setText(getString(R.string.yes));
                } else {
                    infoText.setText(getString(R.string.no));
                }
            }

            infoText = findViewById(R.id.device_type_name);
            if (infoText != null && deviceInfo.get("deviceTypeName") != null) {
                infoText.setText(String.valueOf(deviceInfo.get("deviceTypeName")));
            }

            infoText = findViewById(R.id.device_default_state);
            if (infoText != null && deviceInfo.get("defaultState") != null) {
                infoText.setText(String.valueOf(deviceInfo.get("defaultState")));
            }

            infoText = findViewById(R.id.device_is_formula_used);
            if (infoText != null && deviceInfo.get("isFormulaUsed") != null) {
                Boolean isFormulaUsed = (Boolean) deviceInfo.get("isFormulaUsed");

                if (isFormulaUsed) {
                    infoText.setText(getString(R.string.yes));
                } else {
                    infoText.setText(getString(R.string.no));
                }
            }

            infoText = findViewById(R.id.device_formula_name);
            if (infoText != null && deviceInfo.get("formulaName") != null) {
                String formulaName = String.valueOf(deviceInfo.get("formulaName"));

                if (!formulaName.equals("null")) {
                    infoText.setText(formulaName);
                } else {
                    infoText.setText(getString(R.string.none));
                }
            }

            infoText = findViewById(R.id.device_positive_state);
            if (infoText != null && deviceInfo.get("positiveState") != null) {
                String positiveState = String.valueOf(deviceInfo.get("positiveState"));

                if (!positiveState.equals("null")) {
                    infoText.setText(positiveState);
                } else {
                    infoText.setText(getString(R.string.none));
                }
            }

            infoText = findViewById(R.id.device_negative_state);
            if (infoText != null && deviceInfo.get("negativeState") != null) {
                String negativeState = String.valueOf(deviceInfo.get("negativeState"));

                if (!negativeState.equals("null")) {
                    infoText.setText(negativeState);
                } else {
                    infoText.setText(getString(R.string.none));
                }
            }
        }
    }

    private void displaySnackbar(String message) {
        View contextView = findViewById(R.id.controller_scroll_coordinator);
        Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
    }

    private String getToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("authorization", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("authToken", "");

        if (authToken == null || authToken.equals("")) {
            View contextView = findViewById(R.id.controller_scroll_coordinator);
            Snackbar.make(contextView, R.string.main_menu_login_failed_message, Snackbar.LENGTH_LONG).show();
        }
        return authToken;
    }
}
