package c.michalkoziara.iot_mobile;

import android.app.ProgressDialog;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    TextInputEditText _emailText;
    TextInputLayout _emailTextLayout;
    TextInputEditText _passwordText;
    TextInputLayout _passwordTextLayout;
    MaterialButton _loginButton;
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = findViewById(R.id.input_email);
        _emailTextLayout = findViewById(R.id.input_layout_email);
        _passwordText = findViewById(R.id.input_password);
        _passwordTextLayout = findViewById(R.id.input_layout_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_sign_up);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authorization_dialog));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
//                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        finish();
    }

    public void onLoginFailed() {
        View contextView = findViewById(R.id.login_scrollview);
        Snackbar.make(contextView, R.string.login_failed_message, Snackbar.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailTextLayout.setError(getString(R.string.invalid_email_message));
            _emailTextLayout.setErrorEnabled(true);
            valid = false;
        } else {
            _emailTextLayout.setError(null);
            _emailTextLayout.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            _passwordTextLayout.setError(getString(R.string.invalid_password_message));
            _passwordTextLayout.setErrorEnabled(true);
            valid = false;
        } else {
            _passwordTextLayout.setError(null);
            _passwordTextLayout.setErrorEnabled(false);
        }

        return valid;
    }
}