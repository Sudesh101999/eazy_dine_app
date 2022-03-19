package com.app.eazydine_in.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Activity.DishList.DishListActivity;
import com.app.eazydine_in.Adapter.AdapterHome;
import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.dish_page_indian_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Indian");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.dish_page_chinese_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Chinese");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.dish_page_mexican_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Mexican");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.dish_page_thai_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Thai");
                startActivity(intent);
            }
        });






        view.findViewById(R.id.dish_page_veg_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Veg");
                startActivity(intent);
            }
        });

        view.findViewById(R.id.dish_page_non_veg_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Non-Veg");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.dish_page_italian_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DishListActivity.class);
                intent.putExtra("categories", "Italian");
                startActivity(intent);
            }
        });

    }

}