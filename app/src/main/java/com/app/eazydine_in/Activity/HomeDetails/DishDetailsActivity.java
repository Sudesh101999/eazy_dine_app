package com.app.eazydine_in.Activity.HomeDetails;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;
import com.bumptech.glide.Glide;

public class DishDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView imageView,backIv;
    private TextView priceTv,mrpTv, dishTitle, detailsTv, dishTypeTv, dishCategoryTv;
//    private Spinner quentitySpinner;
    private Button orderNowCv;

    private int dishIdStr;
    private String imgStr,titleStr,priceStr,mrpStr,detailStr,typeStr,categoryStr,tableNumstr;

    String quentityStr;
    private DbHelper dbHelper;

//    private TextView timeStartTv,timeEndTv;
    private int mYear, mMonth, mDay, mHour, mMinute;

    String[] tableNumArr = {
            "1", "2", "3", "4", "5","6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15","16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25","26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35","36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45","46", "47", "48", "49", "50",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        dbHelper = new DbHelper(DishDetailsActivity.this);

//        opentablesDialog();

        getDataFromIntent();

        initFunction();

        setData();

        orderNowCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = SharedPrefManager.getInstance(getApplicationContext()).getValueOfUserId(getApplicationContext());
//                insertDataIntoDatabase(userId,dishIdStr,tbNum,quentityStr,startTime,endTime);

                addToCard(dishIdStr,imgStr,titleStr,priceStr,mrpStr,detailStr,typeStr,categoryStr,tableNumstr);
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addToCard(int dishIdStr, String imgStr, String titleStr, String priceStr, String mrpStr, String detailStr, String typeStr, String categoryStr, String tableNumstr) {
        //add to cart operation
        if (!dbHelper.isInCart(dishIdStr)) {
            String insertedStr = dbHelper.insertDishList(
                    dishIdStr,
                    titleStr,
                    imgStr,
                    mrpStr,
                    priceStr,
                    "1",
                    detailStr,
                    typeStr,
                    categoryStr
            );
            if (insertedStr == "Inserted") {
                Toast.makeText(DishDetailsActivity.this, "Item has been added in your cart.", Toast.LENGTH_SHORT).show();
                
            } else {
                Toast.makeText(DishDetailsActivity.this, "insert error...!" + insertedStr, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DishDetailsActivity.this, "already there in database", Toast.LENGTH_SHORT).show();
        }
    }
/*

    private void insertDataIntoDatabase(String userId, String dishIdStr, String tbNum, String quentityStr, String startTime, String endTime) {
        String urlStr = "https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=order_now";
        StringRequest request = new StringRequest(Request.Method.POST, urlStr, response -> {
            try {
                Log.d("reg Respo",response);

                JSONObject jsonObject = new JSONObject(response);

                String msg = jsonObject.getString("message");
                boolean error = jsonObject.getBoolean("error");

                if (!error) {

                    Toast.makeText(getApplicationContext(),
                            "Message: "+msg, Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userId);
                parameters.put("product_id", dishIdStr);
                parameters.put("table_number",tbNum);
                parameters.put("quantity_number", quentityStr);
                parameters.put("start_time", startTime);
                parameters.put("end_time", endTime);
                parameters.put("order_status","false");
                return parameters;
            }
        };
        Volley.newRequestQueue(DishDetailsActivity.this).add(request);
    }
*/

    private void setData() {

        String[] sArr_1=imgStr.split("/");
        String imag_link = "https://drive.google.com/uc?export=download&id="+sArr_1[5];
        Glide.with(DishDetailsActivity.this).load(imag_link).placeholder(R.drawable.dish).into(imageView);

//        Toast.makeText(DishDetailsActivity.this, "Image: "+imgStr, Toast.LENGTH_SHORT).show();

        TextView textView2_1 = mrpTv;
        textView2_1.setText("₹" + mrpStr);
        mrpTv.setPaintFlags(mrpTv.getPaintFlags() | 16);


        priceTv.setText("₹" + priceStr);
        dishTitle.setText(titleStr);
        dishTypeTv.setText(typeStr);
        dishCategoryTv.setText(categoryStr);
        detailsTv.setText(detailStr);

/*
        ArrayAdapter ad1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tableNumArr);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quentitySpinner.setAdapter(ad1);

        quentitySpinner.setOnItemSelectedListener(this);*/
    }

    private void getDataFromIntent() {
        dishIdStr = getIntent().getExtras().getInt("dishIdIn");
        imgStr = getIntent().getExtras().getString("imgUrlIn");
        titleStr = getIntent().getExtras().getString("titleIn");
        priceStr = getIntent().getExtras().getString("priceIn");
        mrpStr = getIntent().getExtras().getString("mrpIn");
        detailStr = getIntent().getExtras().getString("detailsIn");
        typeStr = getIntent().getExtras().getString("typeIn");
        categoryStr = getIntent().getExtras().getString("categoryIn");
        tableNumstr = getIntent().getExtras().getString("tableNum");
    }

    private void initFunction() {
        imageView=findViewById(R.id.dish_details_image);
        backIv=findViewById(R.id.back_btn);
        priceTv=findViewById(R.id.dish_details_price);
        mrpTv=findViewById(R.id.dish_details_mrp);
        dishTitle=findViewById(R.id.dish_details_title);
        detailsTv=findViewById(R.id.dish_details_details_tv);
        dishTypeTv=findViewById(R.id.dish_details_type);
        dishCategoryTv=findViewById(R.id.dish_details_category);
        orderNowCv=findViewById(R.id.card_order_btn);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//        Toast.makeText(getApplicationContext(), "Table number: "+tableNumArr[i], Toast.LENGTH_SHORT).show();

        /*switch (adapterView.getId()){
            case  R.id.dish_details_quantity:
                quentityStr=tableNumArr[i];
                break;
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}