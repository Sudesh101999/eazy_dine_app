package com.app.eazydine_in.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Activity.Register.RegisterActivity;
import com.app.eazydine_in.MainActivity;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout phoneTl, passTl;
    private Button loginBtn;
    private TextView registerTv;

    private RelativeLayout progressLayout;
    private ProgressBar progressBar;

    String urlStr = "https://jenifer618.000webhostapp.com/EasyDine/authenticate.php?apicall=login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void validation() {
        String phoneStr = phoneTl.getEditText().getText().toString();
        String passStr = passTl.getEditText().getText().toString();

        String regexStr = "^[0-9]$";

        if (!isValidMobile(phoneStr) || phoneStr.length() < 10) {
            Toast.makeText(getApplicationContext(), "Phone number is not correct...!", Toast.LENGTH_SHORT).show();
        } else if (passStr.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password is getting empty...!", Toast.LENGTH_SHORT).show();
        } else {
            completedValidation(phoneStr, passStr);
        }
    }

    private void completedValidation(String phoneStr, String passStr) {
//        Toast.makeText(getApplicationContext(), "Phone: "+phoneStr+"\nPassword: "+passStr, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        progressLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_shay));

        StringRequest request = new StringRequest(Request.Method.POST, urlStr, response -> {
            try {
                Log.d("reg Respo", response);

                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("message");

                String userId = "", userName = "", userPhone = "";

                if (!jsonObject.getBoolean("error")) {

                    userId = jsonObject.getString("user_id");
                    userName = jsonObject.getString("username");
                    userPhone = jsonObject.getString("phone");

                    /*Toast.makeText(getApplicationContext(),
                            "Message: "+msg+
                                    "\nId: "+userId+
                                    "\nName: "+userName+
                                    "\nPhone: "+userPhone, Toast.LENGTH_SHORT).show();*/

                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(userName, userPhone, userId);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    Log.e("Error: ",msg);
                }
                progressVisibilityGon();

            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("Json_Exception: ", e.getMessage());
                progressVisibilityGon();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.e("response_Error: ", error.getMessage());
                progressVisibilityGon();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("log_phone", phoneStr);
                parameters.put("log_password", passStr);
                return parameters;
            }
        };
        Volley.newRequestQueue(LoginActivity.this).add(request);
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private void progressVisibilityGon() {
        progressBar.setVisibility(View.GONE);
        progressLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    private void init() {
        phoneTl = findViewById(R.id.login_et_layout_phone);
        passTl = findViewById(R.id.login_et_layout_password);
        loginBtn = findViewById(R.id.btn_login);
        registerTv = findViewById(R.id.login_text_register);


        progressLayout = findViewById(R.id.log_progress_layout);
        progressBar = findViewById(R.id.log_progress_bar);
    }
}