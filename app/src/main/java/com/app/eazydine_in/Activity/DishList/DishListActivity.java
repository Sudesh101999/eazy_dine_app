package com.app.eazydine_in.Activity.DishList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Activity.CartDetailActivity;
import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Activity.Login.LoginActivity;
import com.app.eazydine_in.Adapter.AdapterHome;
import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DishListActivity extends AppCompatActivity {

    private RecyclerView homeRecycler;
    private ArrayList<ModelHome> items;
    private AdapterHome adapterHome;
    private ProgressBar dishPageProgress;

    private String tableNum;

    private static String baseUrl = "https://jenifer618.000webhostapp.com/EasyDine/homePage.php?apicall=fetch_all_dishes";

    private FloatingActionButton floatingActionButton, floatingActionButtonSort;
    private TextView cartCountTv;

    private DbHelper dbHelper;
    int cartCount;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);


        dbHelper = new DbHelper(getApplicationContext());

        homeRecycler = findViewById(R.id.home_recycler);
        dishPageProgress = findViewById(R.id.dish_page_progressBar);
        floatingActionButton = findViewById(R.id.cart_float);
        floatingActionButtonSort = findViewById(R.id.sort_float);
        cartCountTv = findViewById(R.id.cart_count_tv);

        countLoopMethod();

        String categoryStr = getIntent().getStringExtra("categories");

        getAllDataWithCategories(categoryStr);

//        getAllData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartDetailActivity.class));
            }
        });

        floatingActionButtonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSortLayout();
            }
        });

    }

    private void popupSortLayout() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(DishListActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.sort_custm_layout, null);
        final TextView tvLowToHigh = (TextView) mView.findViewById(R.id.sort_layout_low_to_high);
        final TextView tvHighToLow = (TextView) mView.findViewById(R.id.sort_layout_high_to_low);
        final TextView tvDishName = (TextView) mView.findViewById(R.id.sort_layout_dish_name);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);


        tvLowToHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //short by price
                Collections.sort(items);
                adapterHome.notifyDataSetChanged();

                alertDialog.dismiss();
            }
        });
        tvHighToLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //short by price
                Collections.sort(items, Collections.reverseOrder());
                adapterHome.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        tvDishName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //short by name
                Collections.sort(items, ModelHome.shortByName);
                adapterHome.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void getAllDataWithCategories(String cateStr) {
        Toast.makeText(DishListActivity.this, "Categories: " + cateStr, Toast.LENGTH_SHORT).show();

        String baseUrlCategories = "https://jenifer618.000webhostapp.com/EasyDine/homePage.php?apicall=fetch_all_dishes_with_dish_categories";
        items = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                baseUrlCategories,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);

                            items.add(new ModelHome(
                                    json.getInt("dishId"),
                                    json.getString("image"),
                                    json.getString("name"),
                                    json.getString("price"),
                                    json.getString("mrp"),
                                    json.getString("description"),
                                    json.getString("type"),
                                    json.getString("category")
                            ));
                        }

                        if (items.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Data getting empty...!", Toast.LENGTH_SHORT).show();
                            dishPageProgress.setVisibility(View.GONE);
                        } else {
                            adapterHome = new AdapterHome(DishListActivity.this, items, tableNum);
                            homeRecycler.setAdapter(adapterHome);
                            dishPageProgress.setVisibility(View.GONE);
                            Log.d("respo Data List: ", String.valueOf(items));
                        }
                    } catch (Exception e) {
                        //Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Exception: ", e.getMessage());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Volley Error: ", "Volley error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("categories", cateStr);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private void countLoopMethod() {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
//                fevCountMethod();

                extractedFavCount();
            }
        }, delay);

    }

    private void extractedFavCount() {
        cartCount = dbHelper.countCart();
        if (cartCount == 0) {
            cartCountTv.setVisibility(View.GONE);
        } else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(String.valueOf(cartCount));
        }
    }


    public void getAllData() {

        items = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET,
                baseUrl,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);

                            items.add(new ModelHome(
                                    json.getInt("dishId"),
                                    json.getString("image"),
                                    json.getString("name"),
                                    json.getString("price"),
                                    json.getString("mrp"),
                                    json.getString("description"),
                                    json.getString("type"),
                                    json.getString("category")
                            ));
                        }

                        if (items.size() == 0) {
                            Toast.makeText(getApplicationContext(), "Data getting empty...!", Toast.LENGTH_SHORT).show();
                            dishPageProgress.setVisibility(View.GONE);
                        } else {
                            Collections.sort(items);
                            adapterHome = new AdapterHome(getApplicationContext(), items, tableNum);
                            homeRecycler.setAdapter(adapterHome);
                            dishPageProgress.setVisibility(View.GONE);
                            Log.d("respo Data List: ", String.valueOf(items));
                        }
                    } catch (Exception e) {
                        //Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Exception: ", e.getMessage());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Volley Error: ", "Volley error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                return parameters;
            }
        };
        requestQueue.add(request);
    }
}