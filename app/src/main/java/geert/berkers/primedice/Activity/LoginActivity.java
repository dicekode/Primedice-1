package geert.berkers.primedice.Activity;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.util.Base64;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Paint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;
import org.json.JSONException;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import geert.berkers.primedice.R;
import geert.berkers.primedice.Data.URLS;
import geert.berkers.primedice.DataHandler.Encrypter;
import geert.berkers.primedice.DataHandler.LoginTask;
import geert.berkers.primedice.DataHandler.PostToServerTask;

public class LoginActivity extends AppCompatActivity {

    // Change it to secret private_key before building app!
    private static final String PRIVATE_KEY = "GeertsPrivateKey";

    private Button btnLogin;
    private CheckBox cbRememberMe;
    private TextView txtResult, txtRegister;
    private EditText txtUsername, txtPassword, txtTFA;

    private boolean rememberMe = false;
    private static String access_token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
        cbRememberMe = (CheckBox) findViewById(R.id.cbRememberMe);

        txtTFA = (EditText) findViewById(R.id.etTFA);
        txtUsername = (EditText) findViewById(R.id.etUsername);
        txtPassword = (EditText) findViewById(R.id.etPassword);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.primedice);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.primedicecolor)));
        }

        txtRegister.setPaintFlags(txtRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lbl;
                String button;

                if (txtRegister.getText().toString().equals("Register")) {
                    button = "GET STARTED";
                    lbl = "Login";
                    txtPassword.setVisibility(View.GONE);
                    txtTFA.setVisibility(View.GONE);
                } else {
                    button = "LOGIN";
                    lbl = "Register";
                    txtPassword.setVisibility(View.VISIBLE);
                    txtTFA.setVisibility(View.VISIBLE);
                }

                btnLogin.setText(button);
                txtRegister.setText(lbl);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Bundle b = getIntent().getExtras();
            String info = b.getString("info");
            if (info == null) {
                throw new Exception("No info found");
            }
            txtResult.setText(info);
        } catch (Exception ex) {
            // Check if access_token is saved in SharedPreferences from your mobile
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            rememberMe = sharedPref.getBoolean("remember_me", false);
            cbRememberMe.setChecked(rememberMe);

            if (rememberMe) {
                try {
                    Encrypter encrypter = new Encrypter(PRIVATE_KEY);
                    String encryptedString = sharedPref.getString("access_token", null);

                    if (encryptedString != null) {

                        byte[] encrypted = Base64.decode(encryptedString, Base64.DEFAULT);
                        byte[] decrypted = encrypter.decrypt(encrypted);

                        access_token = new String(decrypted, "UTF-8");

                        new LoginTask(this).execute(access_token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //Register a user
    private void register(String username) {
        try {
            String urlParameters = "username=" + URLEncoder.encode(String.valueOf(username), "UTF-8") + "&affiliate=GeertDev";

            PostToServerTask registerTask = new PostToServerTask();
            String result = registerTask.execute(URLS.REGISTER, urlParameters).get();

            if (result != null) {
                getAccessTokenFromLoginResult(result);
                new LoginTask(this).execute(access_token);
            } else {
                String registerFailed = "Registering failed!";
                txtResult.setText(registerFailed);
            }
        } catch (UnsupportedEncodingException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Log in
    public void login(View v) {

        if (v.getId() == btnLogin.getId()) {

            rememberMe = cbRememberMe.isChecked();
            String username = txtUsername.getText().toString();

            if (btnLogin.getText().toString().equals("GET STARTED")) {
                register(username);
            } else {
                String tfa = txtTFA.getText().toString();
                String password = txtPassword.getText().toString();

                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Fill in a username!", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Fill in a password!", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        String urlParameters = "username=" + URLEncoder.encode(username, "UTF-8")
                                + "&password=" + URLEncoder.encode(password, "UTF-8");

                        if (tfa.length() != 0) {
                            urlParameters = urlParameters + "&otp=" + URLEncoder.encode(tfa, "UTF-8");
                        }

                        PostToServerTask loginTask = new PostToServerTask();
                        String loginResult = loginTask.execute(URLS.LOG_IN, urlParameters, tfa).get();

                        if (loginResult == null) {
                            loginResult = "Couldn't log in! Try again.";
                            txtResult.setText(loginResult);
                        } else {
                            getAccessTokenFromLoginResult(loginResult);
                            new LoginTask(this).execute(access_token);
                        }

                        Log.i("LOGIN_RESULT", loginResult);

                    } catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Get the access_token from the server response
    private void getAccessTokenFromLoginResult(String loginResult) {
        try {
            JSONObject oneObject = new JSONObject(loginResult);

            access_token = oneObject.optString("access_token");

            if (rememberMe) {
                if (access_token != null) {
                    // Save access_token in shared preferences for automatic login next time
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.putBoolean("remember_me", rememberMe);

                    try {
                        Encrypter encrypter = new Encrypter(PRIVATE_KEY);
                        byte[] encrypted = encrypter.encrypt(access_token.getBytes("UTF-8"));
                        String encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
                        editor.putString("access_token", encryptedString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    editor.apply();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
