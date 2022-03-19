package com.app.eazydine_in.Activity.OrderDetails;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.Models.ModelOrder;
import com.app.eazydine_in.Models.ModelOrderedItem;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    String userId,id;

    DecimalFormat df = new DecimalFormat("0.00");
    private RecyclerView recyclerView;
    private TextView tableNoTv,totalAmountTv,timeTv,numOrdersTv,startTimeTv,endTimeTv;
    private Button editBtn,cancelBtn;
    ImageView back_btn;
    ProgressBar progressBar;

    private String orderDateStr,orderTotalStr,tableNumStr,numOrdersStr,startTimeStr,endTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        userId = SharedPrefManager.getInstance(getApplicationContext()).getValueOfUserId(getApplicationContext());

        Intent intent = getIntent();
        id = intent.getStringExtra("order_id");

        Toast.makeText(this, "Order Id: "+id, Toast.LENGTH_SHORT).show();

        initFunction();

        listener();
        getOrderDetail();
    }

    private void initFunction() {
        progressBar = findViewById(R.id.order_details_progressBar);
        recyclerView = findViewById(R.id.ordered_item_recycle);
        tableNoTv = findViewById(R.id.table_num_tv);
        totalAmountTv = findViewById(R.id.total_tv);
        editBtn = findViewById(R.id.order_layout_update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        back_btn = findViewById(R.id.back_btn);
        timeTv = findViewById(R.id.time_tv);
        numOrdersTv = findViewById(R.id.num_orders_tv);
        startTimeTv = findViewById(R.id.start_time_tv);
        endTimeTv = findViewById(R.id.end_time_tv);
    }


    private void listener() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogForCancel();
            }
        });
    }

    private void createDialogForCancel() {
        new AlertDialog.Builder(OrderDetailsActivity.this).setMessage((CharSequence) "Are you sure to delete?")
                .setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateDataToDatabase();
                    }
                }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        }).show();
    }

    private void UpdateDataToDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                "https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=cancel_orders",
                response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);


            } catch (JSONException e) {
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Order_Details_Activity", "fetchLocationListing: " + e.getMessage());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Order_Details_Activity", "Volley error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userId);
                parameters.put("order_id", id);
                return parameters;
            }
        };
        requestQueue.add(request);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                progressBar.setVisibility(View.GONE);
                onBackPressed();
            }
        });
    }


    public void getOrderDetail() {
        ArrayList<ModelOrderedItem> orderItems = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                "https://jenifer618.000webhostapp.com/EasyDine/order_history_details.php",
                response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray courses = jsonObject.getJSONArray("my_order");
                int status = jsonObject.getInt("success_code");
                orderDateStr = jsonObject.getString("order_date");
                orderTotalStr = jsonObject.getString("order_total");
                tableNumStr = jsonObject.getString("table_num");
                numOrdersStr = jsonObject.getString("num_orders");
                startTimeStr = jsonObject.getString("start_time");
                endTimeStr = jsonObject.getString("end_time");

                timeTv.setText(orderDateStr);
                tableNoTv.setText(tableNumStr);
                totalAmountTv.setText("₹ " + df.format(Double.parseDouble(orderTotalStr))); //set total Orders amount
                numOrdersTv.setText(numOrdersStr);
                startTimeTv.setText(startTimeStr);
                endTimeTv.setText(endTimeStr);


                String error_msg = jsonObject.getString("error_msg");
                Log.d("respo MyOrders", response);
                if (status == 1) {
                    try {
                        for (int i = 0; i < courses.length(); i++) {
                            JSONObject json = courses.getJSONObject(i);
                            orderItems.add(new ModelOrderedItem(
                                    json.getString("item_img"),
                                    json.getString("item_name"),
                                    json.getString("item_price"),
                                    json.getString("item_mrp"),
                                    json.getString("item_qty"),
                                    json.getString("item_disc"),
                                    json.getString("item_type"),
                                    json.getString("item_category")
                            ));
                        }
                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Order Exception: ",e.getMessage());
                    }
                } else if (status == 0) {
                    //Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
                    Log.e("status: ",error_msg);
                } else {
                    //Toast.makeText(getApplicationContext(), "No internet connection. Try again!", Toast.LENGTH_LONG).show();
                    Log.e("Internet error: ","Internet Error");
                }
            } catch (JSONException e) {
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Order_Details_Activity", "fetchLocationListing: " + e.getMessage());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Order_Details_Activity", "Volley error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userId);
                parameters.put("order_id", id);
                return parameters;
            }
        };
        requestQueue.add(request);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                progressBar.setVisibility(View.GONE);
                OrderAdapter OrderAdapter = new OrderAdapter(getApplicationContext(), orderItems);
                recyclerView.setAdapter(OrderAdapter);
            }
        });
    }
}