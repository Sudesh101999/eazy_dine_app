package com.app.eazydine_in.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Adapter.CartAdapter;
import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartDetailActivity extends AppCompatActivity {

    private static DecimalFormat f4df = new DecimalFormat("0.00");

    private ImageView backIv;
    private TextView noItemTv,priceCartTotalTv,priceCartDiscount,productCostPriceTv,gstAmountTv,
            subTotalPriceTv,payableAmountTv,checkoutBtnTv;
    private RecyclerView cartItemRv;

    CartAdapter.OnItemClickeListener listener;

    DbHelper dbHelper;
    int f5in = 0;
    CartAdapter shopAdapter;
    ArrayList<CartItem> shopitems;

    double sum1 = 0.0d;
    double sum2 = 0.0d;
    int preQty = 1;
    double payable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);


        this.sum1 = CartAdapter.sumMrp;
        this.sum2 = CartAdapter.sumPrice;
        DbHelper dbHelper = new DbHelper(this);
        this.dbHelper = dbHelper;
        Cursor res = dbHelper.getDishList();

        initFunction();

        if (res.getCount() != 0) {
            noItemTv.setVisibility(View.GONE);
            cartItemRv.setVisibility(View.VISIBLE);

            this.shopitems = new ArrayList<>();
            while (res.moveToNext()){
                Log.e("cart val",""+res.getInt(0));
                this.shopitems.add(new CartItem(
                        res.getInt(0),//id
                        res.getString(2),//image
                        res.getString(1),//name
                        res.getString(6),//description
                        res.getString(4),//mrp
                        res.getString(3),//price
                        res.getString(7),//type
                        res.getString(8),//category
                        res.getInt(5)//qty
                        ));
            }

            CartAdapter.OnItemClickeListener r1 = new CartAdapter.OnItemClickeListener() {
                @Override
                public void onSelect(int positionIn, ArrayList<CartItem> list, AdapterView<?> parent, int upPosition) {

                    int qty = Integer.parseInt(parent.getItemAtPosition(positionIn).toString());
                    CartDetailActivity.this.dbHelper.updateDishList(list.get(upPosition).getId(), ""+qty);

//                    int qtyIt = Integer.parseInt(parent.getItemAtPosition(positionIn).toString());
//                    CartDetailActivity.this.dbHelper.updateDishList(Integer.valueOf(list.get(upPosition).getId()),""+qtyIt);

                    double rateP = Double.parseDouble(list.get(upPosition).getOfferPrice()) * ((double) qty);
                    double rateM = Double.parseDouble(list.get(upPosition).getMrp()) * ((double) qty);

                    for (int i = 0; i<list.size(); i++){
                        if (i != upPosition) {
                            rateP += Double.parseDouble(list.get(i).getOfferPrice())*((double) list.get(i).getQty());
                            rateM += Double.parseDouble(list.get(i).getMrp())*((double) list.get(i).getQty());
                        }
                    }
                    Log.e("qty val",""+rateP);

                    sum1 = rateM;
                    sum2 = rateP;
                    Log.e("on sum plus vals", "mrp: "+CartDetailActivity.this.sum1+"\nprice: "+CartDetailActivity.this.sum2);

                    CartDetailActivity.this.UpdateData();
                    CartDetailActivity.this.preQty = qty;

                }

                @Override
                public void onDeleteClick(final int position, final ArrayList<CartItem> list) {
                    Log.e("befor delete vals: ", "mrp: "+CartDetailActivity.this.sum1+"\nprice: "+CartDetailActivity.this.sum2);

                    new AlertDialog.Builder(CartDetailActivity.this).setMessage((CharSequence) "Are you sure to delete?")
                            .setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CartDetailActivity.this.sum2 -= Double.parseDouble(((CartItem) list.get(position)).getOfferPrice());
                                    CartDetailActivity.this.sum1 -= Double.parseDouble(((CartItem) list.get(position)).getMrp());
                                    CartDetailActivity.this.dbHelper.deleteContact(Integer.valueOf(((CartItem) list.get(position)).getId()));
                                    list.remove(position);
                                    CartDetailActivity.this.shopAdapter.notifyDataSetChanged();
                                    Log.e("deleted after vals","mrp: "+CartDetailActivity.this.sum1+"\nPrice: "+CartDetailActivity.this.sum2);

                                    startActivity(new Intent(CartDetailActivity.this,CartDetailActivity.class));
                                    finish();

                                    UpdateData();

                                }
                            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).show();
                }
            };
            this.listener = r1;
            CartAdapter cartAdapter = new CartAdapter(this,this.shopitems,r1);
            this.shopAdapter = cartAdapter;
            this.cartItemRv.setAdapter(cartAdapter);

            new CountDownTimer(100,100){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    CartDetailActivity.this.sum1 = CartAdapter.sumMrp;
                    CartDetailActivity.this.sum2 = CartAdapter.sumPrice;
                    UpdateData();
                }
            }.start();
        } else {
            noItemTv.setVisibility(View.VISIBLE);
            cartItemRv.setVisibility(View.GONE);
            checkoutBtnTv.setEnabled(false);
        }

        this.backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkoutBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartDetailActivity.this,PlaceOrderActivity.class);
                intent.putExtra("pay_amount",String.valueOf(payable));
                startActivity(intent);
            }
        });


    }

    private void UpdateData() {
        payable = this.sum2;

        TextView textView = this.priceCartTotalTv;
        textView.setText("₹ " + this.sum1);
        double cartDisc = this.sum1 - this.sum2;
        TextView textView2 = this.priceCartDiscount;
        textView2.setText("-₹ " + f4df.format(cartDisc));

        double subTot = (this.sum2 * 100.0d) / 118.0d;
        TextView textView3 = this.productCostPriceTv;
        textView3.setText("₹ " + f4df.format(subTot));

        TextView textView4 = this.subTotalPriceTv;
        textView4.setText("₹ " + this.sum2);
        TextView textView5 = this.gstAmountTv;
        textView5.setText("₹ " + f4df.format(this.sum2 - subTot));
        f4df.format(payable);
        TextView textView6 = this.payableAmountTv;
        textView6.setText("₹ " + payable);
        Log.e("Updated vals", "mrp: " + this.sum1 + "\nprice: " + this.sum2);
    }

    private void initFunction() {

        backIv = findViewById(R.id.back_btn);
        noItemTv = findViewById(R.id.no_items);
        cartItemRv = findViewById(R.id.cart_item_recycle);
        priceCartTotalTv = findViewById(R.id.price_cart_total);
        priceCartDiscount = findViewById(R.id.price_cart_discount);
        productCostPriceTv = findViewById(R.id.product_cost_price);
        gstAmountTv = findViewById(R.id.gst_amount);
        subTotalPriceTv = findViewById(R.id.subtotal_price);
        payableAmountTv = findViewById(R.id.payable_amnt);
        checkoutBtnTv = findViewById(R.id.checkout_btn);
    }
}