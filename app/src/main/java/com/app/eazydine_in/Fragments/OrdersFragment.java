package com.app.eazydine_in.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.eazydine_in.Adapter.AdapterOrdersFrg;
import com.app.eazydine_in.Models.ModelOrder;
import com.app.eazydine_in.R;
import com.app.eazydine_in.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrdersFragment extends Fragment {

    RecyclerView orderRecycler;
    ArrayList<ModelOrder> items;
    AdapterOrdersFrg adapterOrdersFrg;
    ProgressBar homeProgress;
    private static final String baseUrl = "https://jenifer618.000webhostapp.com/EasyDine/ordering.php?apicall=order_history";

    LinearLayoutManager linearLayoutManager;

    private String userId;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        orderRecycler = view.findViewById(R.id.order_recycler);
        homeProgress = view.findViewById(R.id.order_progressBar);

        userId = SharedPrefManager.getInstance(getContext()).getValueOfUserId(getContext());
        getAllData(userId);
    }

    public void getAllData(String userIdStr) {
//        Toast.makeText(getContext(), "User id : " + userIdStr, Toast.LENGTH_SHORT).show();
        items = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, baseUrl, response -> {
            try {
                JSONArray array = new JSONArray(response);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);

                    items.add(new ModelOrder(
                            json.getString("id"),
                            json.getString("cust_id"),
                            json.getString("table_num"),
                            json.getString("order_details"),
                            json.getString("order_total"),
                            json.getString("num_orders"),
                            json.getString("order_status"),
                            json.getString("start_time"),
                            json.getString("end_time"),
                            json.getString("order_time")
                    ));
                }

                if (items.size() == 0) {
                    Toast.makeText(getContext(), "Data getting empty...!", Toast.LENGTH_SHORT).show();
                    homeProgress.setVisibility(View.GONE);
                } else {
                    LinearLayoutManager llm = new LinearLayoutManager(getContext());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    orderRecycler.setLayoutManager(llm);

//                    linearLayoutManager = new LinearLayoutManager(getContext());
//                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                    orderRecycler.setLayoutManager(linearLayoutManager);

                    Collections.reverse(items);
                    adapterOrdersFrg = new AdapterOrdersFrg(getContext(), items);
                    orderRecycler.setAdapter(adapterOrdersFrg);
                    homeProgress.setVisibility(View.GONE);
                    Log.i("respo Data List: ", String.valueOf(items));
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Exception: ", e.getMessage());
                homeProgress.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Volley Error: ", "Volley error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id", userIdStr);
                return parameters;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}