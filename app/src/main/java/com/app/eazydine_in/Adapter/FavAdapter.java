package com.app.eazydine_in.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Activity.HomeDetails.DishDetailsActivity;
import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {


    Context context;
    DbHelper dbHelper;
    /* access modifiers changed from: private */
    public ArrayList<ModelHome> listdata;
    /* access modifiers changed from: private */
    public final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(int i, ArrayList<ModelHome> arrayList);
    }

    public FavAdapter(Context context2, ArrayList<ModelHome> listdata2, OnItemClickListener listener2) {
        this.listdata = listdata2;
        this.context = context2;
        this.listener = listener2;

    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fav_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, @SuppressLint("RecyclerView") int position) {

        dbHelper = new DbHelper(context);
        ModelHome home = listdata.get(position);

        String[] sArr_1 = home.getImage().split("/");
        String imag_link = "https://drive.google.com/uc?export=download&id=" + sArr_1[5];
//        String imag_link = "https://drive.google.com/uc?id="+sArr_1[5];

        Glide.with(context).load(Uri.parse(imag_link)).into(holder.item_img);


        TextView textView = holder.offer_price;
        textView.setText("₹" + home.getPrice());
        this.listdata.size();
        TextView textView2 = holder.mrp;
        textView2.setText("₹" + home.getMrp());
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | 16);

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FavAdapter.this.listener.onItemClick(position, FavAdapter.this.listdata);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishDetailsActivity.class);
                intent.putExtra("dishIdIn", home.getDishId());
                intent.putExtra("imgUrlIn", home.getImage());
                intent.putExtra("titleIn", home.getName());
                intent.putExtra("priceIn", home.getPrice());
                intent.putExtra("mrpIn", home.getMrp());
                intent.putExtra("detailsIn", home.getDescription());
                intent.putExtra("typeIn", home.getType());
                intent.putExtra("categoryIn", home.getCategory());
                intent.putExtra("tableNum", "null");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class FavViewHolder extends RecyclerView.ViewHolder {

        public TextView category;
        public ImageView delete_btn;
        public ImageView item_img;
        public TextView mrp;
        public TextView name;
        public TextView offer_price;
        public RelativeLayout relativeLayout;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            this.item_img = (ImageView) itemView.findViewById(R.id.item_img);
            this.name = (TextView) itemView.findViewById(R.id.item_name);
            this.mrp = (TextView) itemView.findViewById(R.id.item_mrp);
            this.offer_price = (TextView) itemView.findViewById(R.id.item_price);
            this.delete_btn = (ImageView) itemView.findViewById(R.id.delete_ic);
        }
    }
}
