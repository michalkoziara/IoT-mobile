package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ControllerActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    private Map<String, Object> deviceInfo = new HashMap<>();
    private Map<String, Object> typeInfo = new HashMap<>();

    private String executiveDeviceName = "";
    private String executiveDeviceKey = "";
    private String deviceGroupProductKey = "";

    private Object lastState;
    private Boolean lastIsFormulaUsed = false;

    private Object newState;
    private Boolean newIsFormulaUsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            TransitionInflater transitionInflater = TransitionInflater.from(this);
            Transition transition = transitionInflater.inflateTransition(R.transition.transition_controller);

            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }

        setContentView(R.layout.activity_controller);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.bottom_controller_frame).setTransitionName("frame_layout");
        }

        Toolbar toolbar = findViewById(R.id.controller_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.change_enum_state_row).setVisibility(View.GONE);
                findViewById(R.id.change_bool_state_row).setVisibility(View.GONE);
                findViewById(R.id.device_state_input_row).setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

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

                        if (item.getItemId() == R.id.action_reload) {
                            createExecutiveDevice();
                        }
                        return false;
                    }
                }
        );


        if (adapter != null) {
            adapter.getFilter().filter(null);
        }

        createExecutiveDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.getFilter().filter(null);
        }
    }

    public Object getLastState() {
        return lastState;
    }

    public void setLastState(Object lastState) {
        this.lastState = lastState;
    }

    public Boolean getLastIsFormulaUsed() {
        return lastIsFormulaUsed;
    }

    public void setLastIsFormulaUsed(Boolean lastIsFormulaUsed) {
        this.lastIsFormulaUsed = lastIsFormulaUsed;
    }

    private void createExecutiveDevice() {
        String authToken = getToken();
        if (authToken != null) {
            getExecutiveDevice(authToken, deviceGroupProductKey, executiveDeviceKey);
        }

        //        Map<String, Object> testData = new HashMap<>();
//        testData.put("state", 1.0);
//        testData.put("formulaName", "testF");
//        testData.put("isFormulaUsed", false);
//
//        setDeviceInfo(testData);
//
//        Map<String, Object> testTypeData = new HashMap<>();
//        List<String> enumerators = new ArrayList<>();
//        enumerators.add("test");
//        enumerators.add("test1");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test23");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("test3");
//        enumerators.add("te11111111111111fffffffwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwfffffffffff11");
//        enumerators.add("test3");
//        testTypeData.put("enumerator", enumerators);
//        testTypeData.put("stateRangeMin", 13);
//        testTypeData.put("stateRangeMax", 23);
//        testTypeData.put("stateType", "Decimal");
//        setTypeInfo(testTypeData);
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

    private void displaySnackbar(String message) {
        View contextView = findViewById(R.id.controller_scroll_coordinator);
        Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
    }

    private void getExecutiveDevice(String authToken, final String deviceGroupProductKey, String executiveDeviceKey) {
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

                            if (!deviceInfoJson.isNull(key)) {
                                deviceInfo.put(key, deviceInfoJson.get(key));
                            } else {
                                deviceInfo.put(key, null);
                            }
                        }

                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    setDeviceInfo(deviceInfo);
                    getSensorTypeByDeviceInfo(deviceInfo, deviceGroupProductKey);
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
                if (deviceInfo.get("state").equals(true)) {
                    infoText.setText(getString(R.string.device_altern_state));
                } else if (deviceInfo.get("state").equals(false)) {
                    infoText.setText(getString(R.string.device_base_state));
                } else {
                    infoText.setText(String.valueOf(deviceInfo.get("state")));
                }

                setLastState(deviceInfo.get("state"));

                findViewById(R.id.change_state_btn).setEnabled(false);
                findViewById(R.id.change_state_bool_btn).setEnabled(false);
                findViewById(R.id.device_state_filled_exposed_dropdown).clearFocus();
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
                    setLastIsFormulaUsed(true);
                } else {
                    infoText.setText(getString(R.string.no));
                    setLastIsFormulaUsed(false);
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

    private void getSensorTypeByDeviceInfo(Map<String, Object> deviceInfo, String deviceGroupProductKey) {
        String deviceTypeName = null;
        if (deviceInfo != null && deviceInfo.get("deviceTypeName") != null) {
            deviceTypeName = String.valueOf(deviceInfo.get("deviceTypeName"));
        }

        String authToken = getToken();
        if (authToken != null
                && deviceGroupProductKey != null
                && deviceTypeName != null) {
            getSensorType(authToken, deviceGroupProductKey, deviceTypeName);
        }
    }

    private void getSensorType(String authToken, String deviceGroupProductKey, String deviceTypeName) {
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
                    Map<String, Object> typeInfo = new HashMap<>();

                    try {
                        JSONObject typeInfoJson = new JSONObject(output);
                        Iterator<String> keys = typeInfoJson.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();

                            if (typeInfoJson.get(key) != null) {
                                if (key.equals("enumerator")) {
                                    JSONArray enumeratorJson = typeInfoJson.getJSONArray(key);
                                    List<String> enumNames = new ArrayList<>();

                                    if (enumeratorJson != null) {
                                        for (int i = 0; i < enumeratorJson.length(); i++) {
                                            JSONObject enumJson = enumeratorJson.getJSONObject(i);
                                            enumNames.add(enumJson.getString("text"));
                                        }
                                    }
                                    typeInfo.put(key, enumNames);
                                } else {
                                    typeInfo.put(key, typeInfoJson.get(key));
                                }
                            }
                        }

                    } catch (JSONException e) {
                        displaySnackbar(getString(R.string.main_menu_load_failed_message));
                        e.printStackTrace();
                    }

                    setTypeInfo(typeInfo);
                }
            }
        });

        String url = Constants.hubs_url + "/" + deviceGroupProductKey
                + "/executive-types/" + deviceTypeName;

        sync.execute(url, authToken);
    }

    private void setTypeInfo(Map<String, Object> typeInfo) {
        this.typeInfo = typeInfo;
        displayController(typeInfo, deviceInfo);
    }

    private void displayController(Map<String, Object> typeInfo, Map<String, Object> deviceInfo) {
        if (typeInfo != null && deviceInfo != null) {
            if (typeInfo.get("stateType") != null) {
                if (typeInfo.get("stateType").equals("Decimal")) {
                    MaterialTextView materialTextView = findViewById(R.id.device_min_value);
                    if (materialTextView != null
                            && typeInfo.get("stateRangeMin") != null) {
                        materialTextView.setText(String.valueOf(typeInfo.get("stateRangeMin")));
                    }

                    materialTextView = findViewById(R.id.device_max_value);
                    if (materialTextView != null
                            && typeInfo.get("stateRangeMax") != null) {
                        materialTextView.setText(String.valueOf(typeInfo.get("stateRangeMax")));
                    }

                    TextInputEditText textInputEditText = findViewById(R.id.device_state_edit_text);
                    if (deviceInfo.get("state") != null && deviceInfo.get("state") instanceof Double) {
                        textInputEditText.setText(String.valueOf(deviceInfo.get("state")));

                        textInputEditText.addTextChangedListener(
                                new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        MaterialButton updateBtn = findViewById(R.id.change_state_btn);

                                        MaterialTextView deviceMinTextView = findViewById(R.id.device_min_value);
                                        MaterialTextView deviceMaxTextView = findViewById(R.id.device_max_value);

                                        double deviceMinState;
                                        double deviceMaxState;

                                        try {
                                            deviceMinState = Double.valueOf(deviceMinTextView.getText().toString());
                                            deviceMaxState = Double.valueOf(deviceMaxTextView.getText().toString());
                                        } catch (NumberFormatException e) {
                                            deviceMinState = 1.0;
                                            deviceMaxState = 0.0;
                                        }

                                        if (getLastState() != null
                                                && getLastState() instanceof Double
                                                && s != null
                                                && !s.toString().equals(String.valueOf(getLastState()))
                                                && findViewById(R.id.device_min_value) != null
                                                && findViewById(R.id.device_max_value) != null
                                                && !s.toString().isEmpty()
                                                && !"-".equals(s.toString())
                                                && Double.valueOf(s.toString()) >= deviceMinState
                                                && Double.valueOf(s.toString()) <= deviceMaxState) {
                                            updateBtn.setEnabled(true);
                                            newState = s.toString();
                                        } else if (newIsFormulaUsed == null) {
                                            newState = null;
                                            updateBtn.setEnabled(false);
                                        } else {
                                            newState = null;
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                }
                        );

                        SwitchMaterial formulaSwitch = findViewById(R.id.device_formula_switch);
                        if (formulaSwitch != null) {
                            if (deviceInfo.get("formulaName") == null) {
                                formulaSwitch.setVisibility(View.INVISIBLE);
                            }

                            formulaSwitch.setChecked(getLastIsFormulaUsed());

                            formulaSwitch.setOnCheckedChangeListener(
                                    new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            MaterialButton updateBtn = findViewById(R.id.change_state_btn);

                                            if (getLastIsFormulaUsed() != isChecked) {
                                                updateBtn.setEnabled(true);
                                                newIsFormulaUsed = isChecked;
                                            } else if (newState == null) {
                                                updateBtn.setEnabled(false);
                                                newIsFormulaUsed = null;
                                            } else {
                                                newIsFormulaUsed = null;
                                            }
                                        }
                                    }
                            );
                        }
                    }

                    MaterialButton materialButton = findViewById(R.id.change_state_btn);
                    createUpdateOnClickListenerForButton(materialButton);

                    TableRow tableRow = findViewById(R.id.device_min_value_row);
                    tableRow.setVisibility(View.VISIBLE);

                    tableRow = findViewById(R.id.device_max_value_row);
                    tableRow.setVisibility(View.VISIBLE);

                    tableRow = findViewById(R.id.device_state_input_row);
                    tableRow.setVisibility(View.VISIBLE);

                    tableRow = findViewById(R.id.change_state_btn_row);
                    tableRow.setVisibility(View.VISIBLE);
                }

                if (typeInfo.get("stateType").equals("Enum")
                        && typeInfo.get("enumerator") != null) {
                    List<String> enumerators = (List<String>) typeInfo.get("enumerator");

                    createDropdownAdapter(enumerators);

                    AutoCompleteTextView editTextFilledExposedDropdown =
                            findViewById(R.id.device_state_filled_exposed_dropdown);

                    if (editTextFilledExposedDropdown != null) {
                        if (deviceInfo.get("state") != null && deviceInfo.get("state") instanceof String) {
                            editTextFilledExposedDropdown.setText(String.valueOf(deviceInfo.get("state")));
                            adapter.getFilter().filter(null);
                        }
                    }

                    SwitchMaterial formulaSwitch = findViewById(R.id.device_formula_switch);
                    if (formulaSwitch != null) {
                        if (deviceInfo.get("formulaName") == null) {
                            formulaSwitch.setVisibility(View.INVISIBLE);
                        }

                        formulaSwitch.setChecked(getLastIsFormulaUsed());

                        formulaSwitch.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        MaterialButton updateBtn = findViewById(R.id.change_state_btn);

                                        if (getLastIsFormulaUsed() != isChecked) {
                                            updateBtn.setEnabled(true);
                                            newIsFormulaUsed = isChecked;
                                        } else if (newState == null) {
                                            updateBtn.setEnabled(false);
                                            newIsFormulaUsed = null;
                                        } else {
                                            newIsFormulaUsed = null;
                                        }
                                    }
                                }
                        );
                    }

                    MaterialButton materialButton = findViewById(R.id.change_state_btn);
                    createUpdateOnClickListenerForButton(materialButton);

                    TableRow tableRow = findViewById(R.id.change_state_btn_row);
                    tableRow.setVisibility(View.VISIBLE);

                    tableRow = findViewById(R.id.change_enum_state_row);
                    tableRow.setVisibility(View.VISIBLE);
                }

                if (typeInfo.get("stateType").equals("Boolean")) {

                    SwitchMaterial stateSwitch = findViewById(R.id.device_state_bool_switch);
                    if (stateSwitch != null) {
                        Boolean state;
                        if (deviceInfo.get("state") != null && deviceInfo.get("state") instanceof Boolean) {
                            state = (Boolean) deviceInfo.get("state");
                            stateSwitch.setChecked(state);
                        }

                        stateSwitch.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        MaterialButton updateBtn = findViewById(R.id.change_state_bool_btn);

                                        if ((getLastState() instanceof Boolean
                                                && (Boolean) getLastState() != isChecked)) {
                                            updateBtn.setEnabled(true);
                                            newState = isChecked;
                                        } else if (newIsFormulaUsed == null) {
                                            newState = null;
                                            updateBtn.setEnabled(false);
                                        } else {
                                            newState = null;
                                        }
                                    }
                                }
                        );
                    }

                    SwitchMaterial formulaSwitch = findViewById(R.id.device_formula_bool_switch);
                    if (formulaSwitch != null) {
                        if (deviceInfo.get("formulaName") == null) {
                            formulaSwitch.setVisibility(View.INVISIBLE);
                        }

                        formulaSwitch.setChecked(getLastIsFormulaUsed());

                        formulaSwitch.setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        MaterialButton updateBtn = findViewById(R.id.change_state_bool_btn);

                                        if (getLastIsFormulaUsed() != isChecked) {
                                            updateBtn.setEnabled(true);
                                            newIsFormulaUsed = isChecked;
                                        } else if (newState == null) {
                                            updateBtn.setEnabled(false);
                                            newIsFormulaUsed = null;
                                        } else {
                                            newIsFormulaUsed = null;
                                        }
                                    }
                                }
                        );
                    }

                    MaterialButton materialButton = findViewById(R.id.change_state_bool_btn);
                    createUpdateOnClickListenerForButton(materialButton);

                    TableRow tableRow = findViewById(R.id.change_state_bool_btn_row);
                    tableRow.setVisibility(View.VISIBLE);

                    tableRow = findViewById(R.id.change_bool_state_row);
                    tableRow.setVisibility(View.VISIBLE);

                    ((AutoCompleteTextView) findViewById(R.id.device_state_filled_exposed_dropdown)).clearListSelection();
                    ((AutoCompleteTextView) findViewById(R.id.device_state_filled_exposed_dropdown)).dismissDropDown();
                }
            }
        }
    }

    private void createDropdownAdapter(List<String> dropdownList) {
        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.device_state_filled_exposed_dropdown);

        if (editTextFilledExposedDropdown != null) {
            adapter =
                    new ArrayAdapter<>(
                            this,
                            R.layout.layout_dropdown_state_menu,
                            R.id.device_state_dropdown_item,
                            dropdownList);

            editTextFilledExposedDropdown.setAdapter(adapter);
            editTextFilledExposedDropdown.setKeyListener(null);
            editTextFilledExposedDropdown.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            ((AutoCompleteTextView) v).showDropDown();
                            return false;
                        }
                    }
            );

            editTextFilledExposedDropdown.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = (String) parent.getItemAtPosition(position);
                            MaterialButton updateBtn = findViewById(R.id.change_state_btn);

                            if (selected != null
                                    && getLastState() instanceof String
                                    && !selected.equals((String) getLastState())) {
                                updateBtn.setEnabled(true);
                                newState = selected;
                            } else if (newIsFormulaUsed == null) {
                                newState = null;
                                updateBtn.setEnabled(false);
                            } else {
                                newState = null;
                            }
                        }
                    }
            );
        }
    }

    private void createUpdateOnClickListenerForButton(MaterialButton materialButton) {
        if (materialButton != null) {
            materialButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateDevice();
                        }
                    }
            );
        }
    }

    private void updateDevice() {
        Map<String, Object> requestData = new HashMap<>();

        if (this.deviceInfo != null) {
            requestData.put("name", deviceInfo.get("name"));
            requestData.put("typeName", deviceInfo.get("deviceTypeName"));
            requestData.put("state", deviceInfo.get("state"));
            requestData.put("positiveState", deviceInfo.get("positiveState"));
            requestData.put("negativeState", deviceInfo.get("negativeState"));
            requestData.put("formulaName", deviceInfo.get("formulaName"));
            requestData.put("userGroupName", deviceInfo.get("deviceUserGroup"));
            requestData.put("isFormulaUsed", deviceInfo.get("isFormulaUsed"));
        }

        if (this.newState != null) {
            requestData.put("state", this.newState);
        }

        if (this.newIsFormulaUsed != null) {
            requestData.put("isFormulaUsed", this.newIsFormulaUsed);
        }

        String authToken = getToken();
        setExecutiveDevice(
                authToken,
                this.deviceGroupProductKey,
                this.executiveDeviceKey,
                requestData
        );
    }

    private void setExecutiveDevice(
            final String authToken,
            final String deviceGroupProductKey,
            final String executiveDeviceKey,
            final Map<String, Object> requestData) {

        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public String createRequest(String[] params) {
                JSONObject requestDataJson = new JSONObject();

                try {
                    requestDataJson = new JSONObject(requestData);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                return HttpConnectionFactory.createPutConnection(params[0], authToken, requestDataJson);
            }

            @Override
            public void processFinish(String output) {
                if (output == null) {
                    displaySnackbar("Błąd aktualizacji, proszę spróbować ponownie");
                } else {
                    displaySnackbar("Zaktualizowano");

                    getExecutiveDevice(authToken, deviceGroupProductKey, executiveDeviceKey);
                }
            }
        });

        String url = Constants.hubs_url + "/" + deviceGroupProductKey
                + "/executive-devices/" + executiveDeviceKey;

        sync.execute(url, authToken);
    }
}
