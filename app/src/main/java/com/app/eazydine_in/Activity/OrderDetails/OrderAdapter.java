package com.app.eazydine_in.Activity.OrderDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Adapter.AdapterOrdersFrg;
import com.app.eazydine_in.Models.CartItem;
import com.app.eazydine_in.Models.ModelOrderedItem;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OrderAdapter  extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    public ArrayList<ModelOrderedItem> listData;

    public OrderAdapter(Context context, ArrayList<ModelOrderedItem> listData) {
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_fg_details_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelOrderedItem shopItem = this.listData.get(position);
        holder.nameTv.setText(this.listData.get(position).getItem_name());

        String[] sArr = shopItem.getItem_img().split("/");
        String link="";
        if (sArr[2].equalsIgnoreCase("drive.google.com")){
            String imgId = sArr[5];
            link = "https://drive.google.com/uc?id=" + imgId;
        }
        Glide.with(this.context).load(link).into(holder.itemImageIv);

        TextView textView = holder.offerPriceTv;
        textView.setText(shopItem.getItem_price());

        holder.typeTv.setText(this.listData.get(position).getItem_type());
        holder.categoryTv.setText(this.listData.get(position).getItem_category());

        this.listData.size();

        TextView textView2 = holder.mrpTv;
        textView2.setText(shopItem.getItem_mrp());
        holder.mrpTv.setPaintFlags(holder.mrpTv.getPaintFlags()|16);

        holder.qryTv.setText(shopItem.getItem_qty());



    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImageIv;
        public TextView nameTv,mrpTv,offerPriceTv,typeTv,categoryTv,qryTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemImageIv = itemView.findViewById(R.id.item_img);
            this.nameTv = itemView.findViewById(R.id.item_name);
            this.mrpTv = itemView.findViewById(R.id.item_mrp);
            this.offerPriceTv = itemView.findViewById(R.id.item_price);
            this.qryTv = itemView.findViewById(R.id.item_qty_tv);
            this.typeTv = itemView.findViewById(R.id.item_type);
            this.categoryTv = itemView.findViewById(R.id.item_category);
        }
    }
}
