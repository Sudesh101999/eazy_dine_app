package com.app.eazydine_in.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Adapter.PlaceOrderAdapter;
import com.app.eazydine_in.Adapter.tableScheduleAdapter;
import com.app.eazydine_in.MainActivity;
import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.Models.tableScheduleModel;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements PaymentResultWithDataListener {
//public class PlaceOrderActivity extends AppCompatActivity {

    DecimalFormat df=new DecimalFormat("0.00");
    private String payAmount, tableNum, customerIdStr, dishCountStr, startTimeStr,endTimeStr;

    private TextView payAmountTv, tableNumTv, placeOrderTv,payNowTv, startDateTv, endDateTv;

    private RecyclerView recyclerView;
    DbHelper dbHelper;
    PlaceOrderAdapter placeOrderAdapter;
    ArrayList<CartItem> shopitems;
    private int mHour, mMinute;

    ArrayList<String> imageAl, nameAl, mrpAl, priceAl, qtyAl, descAl, typeAl, categoryAl;
    JSONObject object;

    private ProgressBar progressBar;
    Cursor res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        Checkout.preload(getApplicationContext());

        payAmount = getIntent().getExtras().getString("pay_amount");
//        tableNum = SharedPrefManager.getInstance(getApplicationContext()).getTableNum(getApplicationContext());

        customerIdStr = SharedPrefManager.getInstance(getApplicationContext()).getValueOfUserId(getApplicationContext());

        initData();

        payAmountTv.setText(" : " + payAmount);
        tableNumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForSelectTable();
            }
        });

        selectTimer();

        DbHelper dbHelper = new DbHelper(this);
        res = dbHelper.getDishList();

        dishCountStr = dbHelper.countDishes();

        imageAl = new ArrayList<>();
        nameAl = new ArrayList<>();
        mrpAl = new ArrayList<>();
        priceAl = new ArrayList<>();
        qtyAl = new ArrayList<>();
        descAl = new ArrayList<>();
        typeAl = new ArrayList<>();
        categoryAl = new ArrayList<>();
        this.shopitems = new ArrayList<>();

        if (res.getCount() != 0) {

            while (res.moveToNext()) {
                Log.e("cart val", "" + res.getInt(0));
                this.shopitems.add(new CartItem(
                        res.getInt(0),//id
                        res.getString(2),//image
                        res.getString(1),//name
                        res.getString(6),//description
                        res.getString(4),//mrp
                        res.getString(3),//price
                        res.getString(7),//type
                        res.getString(8),//Category
                        res.getInt(5)//qty
                ));
                imageAl.add(res.getString(2));
                nameAl.add(res.getString(1));
                mrpAl.add(res.getString(4));
                priceAl.add(res.getString(3));
                qtyAl.add(res.getString(5));
                descAl.add(res.getString(6));
                typeAl.add(res.getString(7));
                categoryAl.add(res.getString(8));
            }

            PlaceOrderAdapter adapter = new PlaceOrderAdapter(this, this.shopitems);
            this.placeOrderAdapter = adapter;
            this.recyclerView.setAdapter(placeOrderAdapter);
        }
        placeOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrderMethod();

            }
        });

        payNowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String payableAmountStr = payAmountTv.getText().toString();
                payNowOperation(payableAmountStr.replace(": ",""));
            }
        });
    }

    private void payNowOperation(String payableAmountStr) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_EvXZMjKBLEZ4D2");

        checkout.setImage(R.mipmap.ic_launcher_foreground);
        final Activity activity = this;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name","Easy Dine-in");
            jsonObject.put("description","Have a good day");
//            jsonObject.put("image","");
            jsonObject.put("theme.color","#FF6200EE");
            jsonObject.put("currency", "INR");
            double price=Double.parseDouble(payableAmountStr)*100;
            jsonObject.put("amount", df.format(price));

            checkout.open(activity, jsonObject);

        }catch (Exception e){
            Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialogForSelectTable() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.select_table_dialog,null);

        final CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7, cardView8, cardView9, cardView10, cardView11, cardView12, cardView13, cardView14, cardView15;
        final String[] tableNumberStr = new String[1];
        final RelativeLayout rl1,rl2,rl3,rl4,rl5,rl6,rl7,rl8,rl9,rl10,rl11,rl12,rl13,rl14,rl15;

        cardView1 = view.findViewById(R.id.card_layout_1);
        cardView2 = view.findViewById(R.id.card_layout_2);
        cardView3 = view.findViewById(R.id.card_layout_3);
        cardView4 = view.findViewById(R.id.card_layout_4);
        cardView5 = view.findViewById(R.id.card_layout_5);
        cardView6 = view.findViewById(R.id.card_layout_6);
        cardView7 = view.findViewById(R.id.card_layout_7);
        cardView8 = view.findViewById(R.id.card_layout_8);
        cardView9 = view.findViewById(R.id.card_layout_9);
        cardView10 = view.findViewById(R.id.card_layout_10);
        cardView11 = view.findViewById(R.id.card_layout_11);
        cardView12 = view.findViewById(R.id.card_layout_12);
        cardView13 = view.findViewById(R.id.card_layout_13);
        cardView14 = view.findViewById(R.id.card_layout_14);
        cardView15 = view.findViewById(R.id.card_layout_15);

        rl1 = view.findViewById(R.id.relative_1);
        rl2 = view.findViewById(R.id.relative_2);
        rl3 = view.findViewById(R.id.relative_3);
        rl4 = view.findViewById(R.id.relative_4);
        rl5 = view.findViewById(R.id.relative_5);
        rl6 = view.findViewById(R.id.relative_6);
        rl7 = view.findViewById(R.id.relative_7);
        rl8 = view.findViewById(R.id.relative_8);
        rl9 = view.findViewById(R.id.relative_9);
        rl10 = view.findViewById(R.id.relative_10);
        rl11 = view.findViewById(R.id.relative_11);
        rl12 = view.findViewById(R.id.relative_12);
        rl13 = view.findViewById(R.id.relative_13);
        rl14 = view.findViewById(R.id.relative_14);
        rl15 = view.findViewById(R.id.relative_15);


        alert.setView(view);
        final AlertDialog alertDialog = alert.create();

        //OnClick to card
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 1");
                alertDialog.dismiss();
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 2");
                alertDialog.dismiss();
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 3");
                alertDialog.dismiss();
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 4");
                alertDialog.dismiss();
            }
        });
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 5");
                alertDialog.dismiss();
            }
        });
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 6");
                alertDialog.dismiss();
            }
        });
        cardView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 7");
                alertDialog.dismiss();
            }
        });
        cardView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 8");
                alertDialog.dismiss();
            }
        });
        cardView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 9");
                alertDialog.dismiss();
            }
        });
        cardView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 10");
                alertDialog.dismiss();
            }
        });
        cardView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 11");
                alertDialog.dismiss();
            }
        });
        cardView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 12");
                alertDialog.dismiss();
            }
        });
        cardView13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 13");
                alertDialog.dismiss();
            }
        });
        cardView14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 14");
                alertDialog.dismiss();
            }
        });
        cardView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNumTv.setText(" : 15");
                alertDialog.dismiss();
            }
        });
        //OnClick to card

        //onLongPresss to card
        /*
        cardView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("1");
                return false;
            }
        });
        cardView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("2");
                return true;
            }
        });
        cardView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("3");
                return true;
            }
        });
        cardView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("4");
                return true;
            }
        });
        cardView5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("5");
                return true;
            }
        });
        cardView6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("6");
                return true;
            }
        });
        cardView7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("7");
                return true;
            }
        });
        cardView8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("8");
                return true;
            }
        });
        cardView9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("9");
                return true;
            }
        });
        cardView10.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("10");
                return true;
            }
        });
        cardView11.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("11");
                return true;
            }
        });
        cardView12.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("12");
                return true;
            }
        });
        cardView13.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("13");
                return true;
            }
        });
        cardView14.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("14");
                return true;
            }
        });
        cardView15.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialogForTT("15");
                return true;
            }
        });*/
        //onLongPresss to card

//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        //have to change at 19/01/2022
    }

    private void createDialogForTT(String s) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getParent());

        View mView = getLayoutInflater().inflate(R.layout.home_dialog_table_shedule,null);
        final ArrayList<tableScheduleModel> timeList = new ArrayList<>();
        final ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.table_shedule_progressBar);
        final TextView emptyTv = (TextView) mView.findViewById(R.id.home_tv_dialog_table_empty);
        final RecyclerView recyclerViewTime = (RecyclerView) mView.findViewById(R.id.home_recycler_dialog_table_schedule);

        Button btn_okay = (Button)mView.findViewById(R.id.reg_btn_editing_ok);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        //
        //

        String baseUrl="https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=read_table_schedule";

        StringRequest request = new StringRequest(Request.Method.POST, baseUrl, response -> {
            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);

                    timeList.add(new tableScheduleModel(
                            json.getString("start_time"),
                            json.getString("end_time")
                    ));
                }
                if (timeList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Data getting empty...!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    emptyTv.setVisibility(View.VISIBLE);
                }else {
                    tableScheduleAdapter adapterSchedule = new tableScheduleAdapter(timeList);
                    recyclerViewTime.setAdapter(adapterSchedule);
                    recyclerViewTime.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    emptyTv.setVisibility(View.GONE);
                    Log.d("respo Data List: ", String.valueOf(timeList));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Exception: ", e.getMessage());
            }
            progressBar.setVisibility(View.GONE);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("table_no", s);
                return parameters;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(request);

        //
        //

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private void selectTimer() {
        startDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlaceOrderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = hourOfDay + ":" + minute;

                                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                                Date date = null;
                                try {
                                    date = fmt.parse(time );
                                } catch (ParseException e) {

                                    e.printStackTrace();
                                }

                                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");

                                String formattedTime=fmtOut.format(date);
//                                startTimeStr = formattedTime;

                                startDateTv.setText(Html.fromHtml("<font color='black'>: "+formattedTime+"</font>"));
                                startDateTv.setError("", getDrawable(R.drawable.ic_baseline_done_24));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        endDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlaceOrderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = hourOfDay + ":" + minute;

                                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                                Date date = null;
                                try {
                                    date = fmt.parse(time );
                                } catch (ParseException e) {

                                    e.printStackTrace();
                                }

                                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");

                                String formattedTime=fmtOut.format(date);
//                                endTimeStr = formattedTime;

                                endDateTv.setText(Html.fromHtml("<font color='black'>: "+formattedTime+"</font>"));
                                endDateTv.setError("", getDrawable(R.drawable.ic_baseline_done_24));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }


    private void placeOrderMethod() {


        try {
            object = new JSONObject();
            object.put("images", imageAl);
            object.put("names", nameAl);
            object.put("mrp", mrpAl);
            object.put("price", priceAl);
            object.put("qty", qtyAl);
            object.put("disc", descAl);
            object.put("type", typeAl);
            object.put("category", categoryAl);
        } catch (Exception e) {
            Log.e("JSON exception", e.getMessage());
        }

        GsonBuilder gsonBuilder;
        gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        tableNum = tableNumTv.getText().toString().replace(" : ","");
        String userId = customerIdStr;
        String numberOrder = dishCountStr;
        String orderTotal = payAmount;
        String dishDataList = gson.toJson(object);
        String startTime = startDateTv.getText().toString().replace(": ","");
        String endTime = endDateTv.getText().toString().replace(": ","");

        String DateTime = SimpleDateFormat.getDateTimeInstance().format(new Date());

        if (startTime.equals("") || startTime.equals("Start Time")) {
            Toast.makeText(getApplicationContext(), "Select Starting Time", Toast.LENGTH_SHORT).show();
            startDateTv.setError("Select time");
        } else if (endTime.equals("") || endTime.equals("End Time")) {
            Toast.makeText(getApplicationContext(), "Select End Time", Toast.LENGTH_SHORT).show();
            endDateTv.setError("Select time");
        } else {
            /*Toast.makeText(getApplicationContext(),
                    "start time: "+startTime+
                            "\nEnd Time: "+endTime+ "\nTable n: "+tableNum,
                    Toast.LENGTH_SHORT).show();*/
            insertData(userId,tableNum,numberOrder,orderTotal,dishDataList,startTime,endTime,DateTime);
        }
    }

    private void insertData(String userId, String tableNum, String numberOrder, String orderTotal, String dishDataList, String startTime, String endTime, String DateTimeStr) {

        progressBar.setVisibility(View.VISIBLE);
        String urlStr = "https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=user_orders_details";
        StringRequest request = new StringRequest(Request.Method.POST, urlStr, response -> {
            try {
                Log.d("reg Respo", response);

                JSONObject jsonObject = new JSONObject(response);

                String msg = jsonObject.getString("message");
                boolean error = jsonObject.getBoolean("error");

                if (!error) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    showThanksOrder();
                } else {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userId);
                parameters.put("table_num", tableNum);
                parameters.put("order_details", dishDataList);
                parameters.put("order_total", orderTotal);
                parameters.put("num_orders", numberOrder);
                parameters.put("order_status", "pending");
                parameters.put("start_time", startTime);
                parameters.put("end_time", endTime);
                parameters.put("date_time", DateTimeStr);
                return parameters;
            }
        };
        Volley.newRequestQueue(PlaceOrderActivity.this).add(request);

    }

    private void showThanksOrder() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        //ViewGroup viewGroup = mContext.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialog = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.thanks_for_order, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialog);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();

        ImageView close = dialog.findViewById(R.id.close_btn);
        TextView go_home = dialog.findViewById(R.id.back_to_home);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.dismiss();
                startActivity(new Intent(PlaceOrderActivity.this, MainActivity.class));
                finish();
            }
        });
        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceOrderActivity.this, MainActivity.class));
                finish();
            }
        });
        alertDialog.show();
    }

    private void initData() {
        progressBar = findViewById(R.id.place_order_progressBar);
        tableNumTv = findViewById(R.id.t_num_tv);
        payAmountTv = findViewById(R.id.payable_amount_tv);
        startDateTv = findViewById(R.id.start_time_tv);
        endDateTv = findViewById(R.id.end_time_tv);

        recyclerView = findViewById(R.id.cart_item_recycle_place_order);
        placeOrderTv = findViewById(R.id.pay_leter_tv);
        payNowTv = findViewById(R.id.pay_now_tv);

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Payment Id");
        builder.setMessage(s);
        builder.show();
        Toast.makeText(this, "Payment Result: "+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(this, "Payment Error: "+s, Toast.LENGTH_SHORT).show();
    }
}