package com.app.eazydine_in.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Activity.HomeDetails.DishDetailsActivity;
import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder> {

    private Context context;
    private ArrayList<ModelHome> models;
    private ArrayList<ModelHome> modelsfilter;
    private String tableNum;
    DbHelper dbHelper;
    long insertId = 0;

    public AdapterHome(Context context, ArrayList<ModelHome> models, String tableNum) {
        this.context = context;
        this.models = models;
        this.tableNum = tableNum;
        modelsfilter = new ArrayList<>(models);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dbHelper = new DbHelper(context);
        ModelHome home = models.get(position);

        String[] sArr_1 = home.getImage().split("/");
        String imag_link = "https://drive.google.com/uc?export=download&id=" + sArr_1[5];
//        String imag_link = "https://drive.google.com/uc?id="+sArr_1[5];

        Glide.with(context).load(Uri.parse(imag_link)).into(holder.imageView);

        TextView textView2_1 = holder.mrpTv;
        textView2_1.setText("₹" + home.getPrice());
        holder.mrpTv.setPaintFlags(holder.mrpTv.getPaintFlags() | 16);

        holder.nameTv.setText(home.getName());
        holder.offerPriceTv.setText("₹" + home.getMrp());
        holder.descriptionTv.setText(home.getDescription());
        holder.typeTv.setText(home.getType());
        holder.categoryTv.setText(home.getCategory());

        holder.addIv.setVisibility(View.GONE);
        holder.removeIv.setVisibility(View.GONE);

        holder.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DishDetailsActivity.class);
                intent.putExtra("dishIdIn", home.getDishId());
                intent.putExtra("imgUrlIn", home.getImage());
                intent.putExtra("titleIn", home.getName());
                intent.putExtra("priceIn", home.getPrice());
                intent.putExtra("mrpIn", home.getMrp());
                intent.putExtra("detailsIn", home.getDescription());
                intent.putExtra("typeIn", home.getType());
                intent.putExtra("categoryIn", home.getCategory());
                intent.putExtra("tableNum", tableNum);
                Toast.makeText(context, "Image URL: "+home.getImage(), Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
/*
        ArrayList<String> cartList = dbHelper.getAllCart();
        if (cartList.contains(String.valueOf(home.getDishId()))) {
//            favFlag = false;
//            holder.wishListCard.setImageResource(R.drawable.heart_filled);
            holder.removeIv.setVisibility(View.VISIBLE);
            holder.addIv.setVisibility(View.GONE);
        }else {
            holder.addIv.setVisibility(View.VISIBLE);
            holder.removeIv.setVisibility(View.GONE);
        }
        */

        ArrayList<String> favList = dbHelper.getAllFav();
        if (favList.contains(String.valueOf(home.getDishId()))) {
            holder.fillFavIv.setVisibility(View.VISIBLE);
            holder.emptyFavIv.setVisibility(View.GONE);
        }else {
            holder.fillFavIv.setVisibility(View.GONE);
            holder.emptyFavIv.setVisibility(View.VISIBLE);
        }

        holder.addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, "adding data...!", Toast.LENGTH_SHORT).show();
                holder.removeIv.setVisibility(View.VISIBLE);
                holder.addIv.setVisibility(View.GONE);

                //add to cart operation
                if (!dbHelper.isInCart(home.getDishId())) {
                    String insertedStr = dbHelper.insertDishList(
                            home.getDishId(),
                            home.getName(),
                            home.getImage(),
                            home.getMrp(),
                            home.getPrice(),
                            "1",
                            home.getDescription(),
                            home.getType(),
                            home.getCategory()
                    );
                    if (insertedStr == "Inserted") {
                        Toast.makeText(context, "Item has been added in your cart.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "insert error...!" + insertedStr, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "already there in database", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.emptyFavIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, "adding data...!", Toast.LENGTH_SHORT).show();
                holder.fillFavIv.setVisibility(View.VISIBLE);
                holder.emptyFavIv.setVisibility(View.GONE);

                //add to cart operation
                if (!dbHelper.isInFav(home.getDishId())) {
                    String insertedStr = dbHelper.insertDishListFav(
                            home.getDishId(),
                            home.getName(),
                            home.getImage(),
                            home.getMrp(),
                            home.getPrice(),
                            home.getDescription(),
                            home.getType(),
                            home.getCategory()
                    );
                    if (insertedStr == "Inserted") {
                        Toast.makeText(context, "Item has been added in your favorite list.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "insert error...!" + insertedStr, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "already there in database", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.removeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "removing data...!", Toast.LENGTH_SHORT).show();
                holder.removeIv.setVisibility(View.GONE);
                holder.addIv.setVisibility(View.VISIBLE);
                //remvove from cart operation
                dbHelper.deleteContact(Integer.valueOf(home.getDishId()));
            }
        });
        holder.fillFavIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "removing data...!", Toast.LENGTH_SHORT).show();
                holder.fillFavIv.setVisibility(View.GONE);
                holder.emptyFavIv.setVisibility(View.VISIBLE);
                //remvove from cart operation
                dbHelper.deleteContactFav(Integer.valueOf(home.getDishId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.models.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView homeLayout;
        ImageView imageView, removeIv, addIv,emptyFavIv,fillFavIv;
        TextView nameTv, offerPriceTv, mrpTv, descriptionTv, typeTv, categoryTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            homeLayout = itemView.findViewById(R.id.home_layout_card);
            imageView = itemView.findViewById(R.id.home_layout_image);
            nameTv = itemView.findViewById(R.id.home_layout_name);
            offerPriceTv = itemView.findViewById(R.id.home_layout_offer_price);
            mrpTv = itemView.findViewById(R.id.home_layout_mrp);
            descriptionTv = itemView.findViewById(R.id.home_layout_description);
            typeTv = itemView.findViewById(R.id.home_layout_dish_type);
            emptyFavIv = itemView.findViewById(R.id.fav_empty_iv);
            fillFavIv = itemView.findViewById(R.id.fav_fill_iv);
            addIv = itemView.findViewById(R.id.add_iv);
            removeIv = itemView.findViewById(R.id.remove_iv);
            categoryTv = itemView.findViewById(R.id.home_layout_dish_category);

        }
    }
}
