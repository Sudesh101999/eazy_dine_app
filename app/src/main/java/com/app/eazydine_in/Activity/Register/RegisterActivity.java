package com.app.eazydine_in.Activity.Register;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Activity.Login.LoginActivity;
import com.app.eazydine_in.R;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameTl,emailTl,phoneTl,passTl,rePassTl;
    private Button registerBtn;
    private TextView loginTv;

    private RelativeLayout progressLayout;
    private ProgressBar progressBar;


    private static final int PICKER_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationRegister();
            }
        });


        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void validationRegister() {

        String nameStr= Objects.requireNonNull(nameTl.getEditText()).getText().toString();
        String emailStr=Objects.requireNonNull(emailTl.getEditText().getText().toString());
        String phoneStr= Objects.requireNonNull(phoneTl.getEditText()).getText().toString();
        String passStr= Objects.requireNonNull(passTl.getEditText()).getText().toString();
        String rePassStr= Objects.requireNonNull(rePassTl.getEditText()).getText().toString();

        if (nameStr.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name is getting empty...!", Toast.LENGTH_SHORT).show();
        } else if (emailStr.isEmpty() || !isValidEmail(emailStr)) {
            Toast.makeText(getApplicationContext(), "Email is getting empty...!", Toast.LENGTH_SHORT).show();
        } else if (!isValidMobile(phoneStr) || phoneStr.length()< 10) {
            Toast.makeText(getApplicationContext(), "Phone number is not correct...!", Toast.LENGTH_SHORT).show();
        } else if (passStr.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password is getting empty...!", Toast.LENGTH_SHORT).show();
        } else if (rePassStr.isEmpty() || !rePassStr.matches(passStr)){
            Toast.makeText(getApplicationContext(), "Password not match...!", Toast.LENGTH_SHORT).show();
        }else{
            completedValidation(nameStr,emailStr,phoneStr,passStr);
        }
    }
    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void completedValidation(String nameStr,String emailStr, String phoneStr, String passStr) {
//
        progressBar.setVisibility(View.VISIBLE);
        progressLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.bg_shay));
//        Toast.makeText(getApplicationContext(), "Name: "+nameStr+"\nPhone"+phoneStr+"\nPassword: "+passStr, Toast.LENGTH_SHORT).show();

        String urlStr = "https://jenifer618.000webhostapp.com/EasyDine/authenticate.php?apicall=register";
        StringRequest request = new StringRequest(Request.Method.POST, urlStr, response -> {
            try {
                Log.d("reg Respo",response);

                JSONObject jsonObject = new JSONObject(response);

                String msg = jsonObject.getString("message");
                boolean error = jsonObject.getBoolean("error");

                if (!error) {

                    Toast.makeText(getApplicationContext(),
                            "Message: "+msg,
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), "Exception: "+e.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("Json_Exception: ",e.getMessage());
            }
            progressVisibilityGon();
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.e("response_Error: ",error.getMessage());

                progressVisibilityGon();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("reg_name", nameStr);
                parameters.put("reg_email", emailStr);
                parameters.put("reg_phone", phoneStr);
                parameters.put("reg_password",passStr);
                return parameters;
            }
        };
        Volley.newRequestQueue(RegisterActivity.this).add(request);
    }

    private void progressVisibilityGon() {
        progressBar.setVisibility(View.GONE);
        progressLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private void init() {
        nameTl=findViewById(R.id.reg_et_layout_name);
        emailTl=findViewById(R.id.reg_et_layout_email);
        phoneTl=findViewById(R.id.reg_et_layout_phone);
        passTl=findViewById(R.id.reg_et_layout_password);
        rePassTl=findViewById(R.id.reg_et_layout_re_password);
        registerBtn=findViewById(R.id.reg_btn_register);
        loginTv=findViewById(R.id.reg_text_login);

        progressLayout = findViewById(R.id.reg_progress_layout);
        progressBar = findViewById(R.id.reg_progress_bar);
    }
}