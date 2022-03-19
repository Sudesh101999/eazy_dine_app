package com.app.eazydine_in.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder>{

    private Context context;
    private ArrayList<CartItem> listData;
    private String tableNum;
    DbHelper dbHelper;

    public PlaceOrderAdapter(Context context, ArrayList<CartItem> listData) {
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public PlaceOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaceOrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceOrderAdapter.ViewHolder holder, int position) {

        CartItem shopItem = this.listData.get(position);
        holder.nameTv.setText(this.listData.get(position).getName());
        String[] sArr = shopItem.getImage().split("/");
        String link="";
        if (sArr[2].equalsIgnoreCase("drive.google.com")){
            String imgId = sArr[5];
            link = "https://drive.google.com/uc?id=" + imgId;
        }
        Glide.with(this.context).load(link).into(holder.imgIv);
        TextView textView = holder.priceTv;
        textView.setText("₹" + shopItem.getOfferPrice());

        TextView textView2 = holder.mrpTv;
        textView2.setText("₹" + shopItem.getMrp());
        holder.mrpTv.setPaintFlags(holder.mrpTv.getPaintFlags()|16);

        holder.typeTv.setText(this.listData.get(position).getType());
        holder.categoryTv.setText(this.listData.get(position).getCategory());
        String qtyStr = String.valueOf(listData.get(position).getQty());
        holder.qutyTv.setText(qtyStr);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIv;
        TextView nameTv,qutyTv,typeTv,categoryTv,mrpTv,priceTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIv = itemView.findViewById(R.id.item_img_2);
            nameTv = itemView.findViewById(R.id.item_name_2);
            qutyTv = itemView.findViewById(R.id.item_qty_tv_2);
            typeTv = itemView.findViewById(R.id.item_type_2);
            categoryTv = itemView.findViewById(R.id.item_category_2);
            mrpTv = itemView.findViewById(R.id.item_mrp_2);
            priceTv = itemView.findViewById(R.id.item_price_2);

        }
    }
}
