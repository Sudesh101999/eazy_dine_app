package com.app.eazydine_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.app.eazydine_in.Activity.Login.LoginActivity;
import com.app.eazydine_in.Fragments.HomeFragment;
import com.app.eazydine_in.Fragments.OrdersFragment;
import com.app.eazydine_in.Fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.myhexaville.smartimagepicker.ImagePicker;

import java.io.File;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public BottomNavigationView chipNavigationBarCompany;

    public static FragmentManager fm;
    public static FragmentTransaction ft;
    public static Context mContext;
    public static FrameLayout container;

    private String sharePrePhoneStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        mContext = MainActivity.this;

        if (!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        gotDynamicLink();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        chipNavigationBarCompany = findViewById(R.id.bottom_nav_home);
        container = findViewById(R.id.fragment_container_home);
//        chipNavigationBarCompany.setSelectedItemId(R.id.nave_home);

        setBottomNav();
        loadFragment(new HomeFragment());


        SharedPreferences sharedPreferences = getSharedPreferences("simplifiedcodingsharedprefretrofit", Context.MODE_PRIVATE);
        sharePrePhoneStr = sharedPreferences.getString("keyphone", "");
        FirebaseMessaging.getInstance().subscribeToTopic("Eazy_dine" + sharePrePhoneStr);
        FirebaseMessaging.getInstance().subscribeToTopic("Eazy_dine");
        Log.i("topic subscribe Home", "Eazy_dine");

    }

    private void setBottomNav() {
        chipNavigationBarCompany.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nave_home:
                    fragment=new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nave_orders:
                    fragment=new OrdersFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nave_profile:
                    fragment=new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        findViewById(R.id.fragment_container_home).setVisibility(View.VISIBLE);
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
        transaction.replace(R.id.fragment_container_home, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void gotDynamicLink() {
        Log.e("getdynamic link", "came to link");
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String[] arr = deepLink.toString().split("=");
                            String refer_id = arr[1];
                            Log.i("Refer Link", "referID:" + refer_id);
                            //Toast.makeText(MainActivity.this, "linkis: "+deepLink, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "getDynamicLink:onFailure", e);
                    }
                });
    }

   /* public boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_home,fragment)
                    .commitAllowingStateLoss();
        }
        return true;
    }*/

    @Override
    public void onBackPressed() {
        Fragment f = fm.findFragmentById(R.id.fragment_container_home);

        if (f instanceof HomeFragment)
            makeDialog();
        else if (!(f instanceof OrdersFragment))
            loadFragment(new HomeFragment());
        else if (!(f instanceof ProfileFragment))
            loadFragment(new HomeFragment());
        else
            super.onBackPressed();
    }

    private void makeDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        //close();
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

}