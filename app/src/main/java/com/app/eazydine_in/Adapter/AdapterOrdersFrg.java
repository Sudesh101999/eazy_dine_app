package com.app.eazydine_in.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Activity.OrderDetails.OrderDetailsActivity;
import com.app.eazydine_in.Models.ModelOrder;
import com.app.eazydine_in.Models.ModelOrder;
import com.app.eazydine_in.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterOrdersFrg extends RecyclerView.Adapter<AdapterOrdersFrg.ViewHolder> {

    private Context context;
    private ArrayList<ModelOrder> models;

    public AdapterOrdersFrg(Context context, ArrayList<ModelOrder> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public AdapterOrdersFrg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterOrdersFrg.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrdersFrg.ViewHolder holder, int position) {

        ModelOrder order = models.get(position);
        holder.mrpTv.setText(order.getOrderTotal());
        holder.tableNumberTv.setText(order.getTableNo());
        holder.startTimeTv.setText(order.getStartTime());
        holder.endTimeTv.setText(order.getEndTime());
        holder.orderedDateTv.setText(order.getOrderTime());

        holder.orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getOrderStatus().equals("done") || order.getOrderStatus().equals("pending")) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("order_id", order.getId());
                    context.startActivity(intent);
                }
            }
        });

        if (order.getOrderStatus().equals("cancel")) {
            holder.orderLayout.setBackgroundResource(R.drawable.canceled_bg_layout);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        CardView orderLayout;
        TextView mrpTv, tableNumberTv, startTimeTv, endTimeTv, orderedDateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.orders_layout_ll);
            orderLayout = itemView.findViewById(R.id.order_layout_card);
            mrpTv = itemView.findViewById(R.id.total_amount_tv_layout);
            tableNumberTv = itemView.findViewById(R.id.table_num_tv_layout);
            startTimeTv = itemView.findViewById(R.id.start_time_tv_layout);
            endTimeTv = itemView.findViewById(R.id.end_time_tv_layout);
            orderedDateTv = itemView.findViewById(R.id.ordered_date_tv);

        }
    }
}
