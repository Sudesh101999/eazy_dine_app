package com.app.eazydine_in.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public static double sumMrp;
    public static double sumPrice;
    Context context;
    public ArrayList<CartItem> listData;
    public final OnItemClickeListener listener;

    public interface OnItemClickeListener {
        void onDeleteClick(int i, ArrayList<CartItem> arrayList);
        void onSelect(int i, ArrayList<CartItem> arrayList, AdapterView<?> adapterView, int i2);
    }

    public CartAdapter(Context context2, ArrayList<CartItem> listData2, OnItemClickeListener listener2) {
        this.listData = listData2;
        this.context = context2;
        this.listener = listener2;
        sumMrp = 0.0d;
        sumPrice = 0.0d;
        for (int i = 0; i< listData2.size(); i++){
            sumMrp += (Double.parseDouble(listData2.get(i).getMrp())*listData2.get(i).getQty());
            sumPrice += (Double.parseDouble(listData2.get(i).getOfferPrice())*listData2.get(i).getQty());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,@SuppressLint("RecyclerView") final int position) {

        CartItem shopItem = this.listData.get(position);
        holder.nameTv.setText(this.listData.get(position).getName());

        String[] sArr = shopItem.getImage().split("/");
        String link="";
        if (sArr[2].equalsIgnoreCase("drive.google.com")){
            String imgId = sArr[5];
            link = "https://drive.google.com/uc?id=" + imgId;
        }
        Glide.with(this.context).load(link).into(holder.itemImageIv);

        TextView textView = holder.offerPriceTv;
        textView.setText("₹" + shopItem.getOfferPrice());

        holder.typeTv.setText(this.listData.get(position).getType());
        holder.categoryTv.setText(this.listData.get(position).getCategory());

        this.listData.size();

        TextView textView2 = holder.mrpTv;
        textView2.setText("₹" + shopItem.getMrp());
        holder.mrpTv.setPaintFlags(holder.mrpTv.getPaintFlags()|16);


        List<String> categories = new ArrayList<>();
        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        categories.add("6");
        categories.add("7");
        categories.add("8");
        categories.add("9");
        categories.add("10");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_list_item_1,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.quantitySp.setAdapter(dataAdapter);
        holder.quantitySp.setSelection(shopItem.getQty() - 1);

        holder.quantitySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionIn, long id) {
                CartAdapter.this.listener.onSelect(positionIn, CartAdapter.this.listData,parent,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartAdapter.this.listener.onDeleteClick(position, CartAdapter.this.listData);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImageIv,deleteIv;
        public TextView nameTv,mrpTv,offerPriceTv,typeTv,categoryTv;
        public Spinner quantitySp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemImageIv = itemView.findViewById(R.id.item_img);
            this.nameTv = itemView.findViewById(R.id.item_name);
            this.mrpTv = itemView.findViewById(R.id.item_mrp);
            this.offerPriceTv = itemView.findViewById(R.id.item_price);
            this.deleteIv = itemView.findViewById(R.id.delete_ic);
            this.quantitySp = itemView.findViewById(R.id.spinner);
            this.typeTv = itemView.findViewById(R.id.item_type);
            this.categoryTv = itemView.findViewById(R.id.item_category);

        }
    }
}
