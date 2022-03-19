package com.app.eazydine_in.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Activity.AboutUsActivity;
import com.app.eazydine_in.Activity.FavActivity;
import com.app.eazydine_in.Activity.Login.LoginActivity;
import com.app.eazydine_in.MainActivity;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;
import com.myhexaville.smartimagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class ProfileFragment extends Fragment {

    private static final String baseUrl="https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=user_profile";
    private static final String baseUrlUpdate="https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=";
    public static final int PICK_IMAGE = 1;
    private TextView nameTv,phoneTv,emailTv,editTv,aboutUsTv,likedListTv,shareTv;
    private TextView orderCompletedTv,orderPendingTv,orderCancelTv;
    private String link="";
    SharedPreferences sharedPreferences;
    private FloatingActionButton logout;
    private String userId;
    private ProgressBar progressBar;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public File file;
    public File compressedImageFile;
    public ImagePicker imagePickerProfile;



    private String sharePrePhoneStr;

    private SharedPrefManager sharedPrefManager;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseApp.initializeApp(getContext());

        progressBar = view.findViewById(R.id.order_progressBar_profile);
        nameTv = view.findViewById(R.id.profile_user_name);
        emailTv = view.findViewById(R.id.profile_user_email);
        phoneTv = view.findViewById(R.id.profile_user_phone);
        editTv = view.findViewById(R.id.profile_edit);
        aboutUsTv = view.findViewById(R.id.about_us_tv);
        shareTv = view.findViewById(R.id.share_app_tv);
        likedListTv = view.findViewById(R.id.fav_list_tv);
        orderCompletedTv = view.findViewById(R.id.profile_Completed_Orders);
        orderPendingTv=view.findViewById(R.id.profile_Pending_Orders);
        orderCancelTv = view.findViewById(R.id.profile_Cancel_Orders);
        logout=view.findViewById(R.id.profile_log_out);

        userId = SharedPrefManager.getInstance(getContext()).getValueOfUserId(getContext());


        sharedPreferences= getActivity().getSharedPreferences("simplifiedcodingsharedprefretrofit",Context.MODE_PRIVATE);
        link = sharedPreferences.getString("invite_link","NA");

        getUserInfo(userId);

        aboutUsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
            }
        });
        likedListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FavActivity.class));
            }
        });
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameTv.getText().toString();
                String emailStr = emailTv.getText().toString();
                String phoneStr = phoneTv.getText().toString();

                openEditLayout(nameStr,emailStr,phoneStr);
            }
        });
        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openDialogForLogOut();
            }
        });
    }

    private void shareApp() {
        progressBar.setVisibility(View.VISIBLE);

        if(link.equals("NA")||link.equals(""))
            createlink();
        else {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "EazyDine.in");
            String shareMessage = "\nSchedule For Break Fast/Lunch/Dinar within a minutes:\n";
            shareMessage = shareMessage + link;
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            intent.setType("text/plain");
            startActivity(intent);

            progressBar.setVisibility(View.GONE);
        }
    }

    private void createlink() {

        try {

        Log.e("main", "create link ");
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"))
                .setDynamicLinkDomain("https://example.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();
//click -- link -- google play store -- inistalled/ or not  ----
        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "  Long refer "+ dynamicLink.getUri());
        //   https://referearnpro.page.link?apn=blueappsoftware.referearnpro&link=https%3A%2F%2Fwww.blueappsoftware.com%2F
        // apn  ibi link
        // manuall link
        String sharelinktext  = "https://easydine.page.link"+
                "link=https://jenifer618.000webhostapp.com/"+
                "&apn="+ getActivity().getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Schedule For Break Fast/Lunch/Dinar"+
                "&si="+"https://www.pinclipart.com/picdir/middle/422-4226248_cutlery-clipart-restaurant-logo-restaurant-app-logo-png.png";
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener((Executor) this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            progressBar.setVisibility(View.GONE);
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            // share app dialog
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("invite_link", shortLink.toString());
                            editor.apply();
                            link=shortLink.toString();

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "EazyDine.in");
                            String shareMessage= "\nSchedule For Break Fast/Lunch/Dinar within a minutes:\n";
                            shareMessage = shareMessage + link;
                            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                            intent.setType("text/plain");
                            startActivity(intent);
                        } else {
                            // Error
                            // ...
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Exception of share: ",e.getMessage());

        }
    }

    private void openDialogForLogOut() {
            new AlertDialog.Builder(getContext())
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        // do something when the button is clicked
                        public void onClick(DialogInterface arg0, int arg1) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("simplifiedcodingsharedprefretrofit", Context.MODE_PRIVATE);
                            sharePrePhoneStr = sharedPreferences.getString("keyphone", "");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("Eazy_dine");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("Eazy_dine" + sharePrePhoneStr);

                            SharedPrefManager.getInstance(getContext()).logout();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        // do something when the button is clicked
                        public void onClick(DialogInterface arg0, int arg1) {
                            //finish();
                        }
                    })
                    .show();
        }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void openEditLayout(String nameStr, String emailStr, String phoneStr) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.edit_profile_layout,null);
        final TextInputLayout nameTil = (TextInputLayout) mView.findViewById(R.id.reg_et_editing_name);
        final TextInputLayout emailTil = (TextInputLayout) mView.findViewById(R.id.reg_et_editing_email);
        final TextInputLayout phoneTil = (TextInputLayout) mView.findViewById(R.id.reg_et_editing_phone);
        Button btn_cancel = (Button)mView.findViewById(R.id.reg_btn_editing_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.reg_btn_editing_edit);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);

        nameTil.getEditText().setText(nameStr);
        emailTil.getEditText().setText(emailStr);
        phoneTil.getEditText().setText(phoneStr);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameEdit = nameTil.getEditText().getText().toString();
                String emailEdit = emailTil.getEditText().getText().toString();

                nameTv.setText(nameEdit);
                emailTv.setText(emailEdit);

                Toast.makeText(getContext(), "Edited Name: "+nameEdit+"\nEdited email: "+emailEdit, Toast.LENGTH_SHORT).show();

                updateNameEmail(nameEdit,emailEdit);

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //api for update name and email
    private void updateNameEmail(String nameEdit, String emailEdit) {

        StringRequest request = new StringRequest(Request.Method.POST, baseUrlUpdate, response -> {
            try {
                Log.d("reg Respo",response);

                JSONObject jsonObject = new JSONObject(response);

                String msg = jsonObject.getString("message");
                boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    Toast.makeText(getContext(), "Message: \n"+msg, Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("update_name", nameEdit);
                parameters.put("update_email", emailEdit);
                return parameters;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void getUserInfo(String userId) {

        StringRequest request = new StringRequest(Request.Method.POST, baseUrl, response -> {
            try {
                Log.d("reg Respo",response);

                JSONObject jsonObject = new JSONObject(response);

                String msg = jsonObject.getString("message");
                String UserId = jsonObject.getString("user_id");
                boolean error = jsonObject.getBoolean("error");

                if (!error) {

//                    Toast.makeText(getContext(),
//                            "Message: "+msg+
//                                    "\nId: "+UserId, Toast.LENGTH_SHORT).show();
                    String name = jsonObject.getString("username");
                    String phone = jsonObject.getString("phone");
                    String email = jsonObject.getString("email");
                    String orderCancelStr = jsonObject.getString("dish_cancel");
                    String orderPendingStr = jsonObject.getString("dish_pending");
                    String orderDoneStr = jsonObject.getString("dish_done");

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    orderCompletedTv.setText(orderDoneStr);
                    orderPendingTv.setText(orderPendingStr);
                    orderCancelTv.setText(orderCancelStr);

                }

            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userId);
                return parameters;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

}