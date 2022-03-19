package com.app.eazydine_in.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eazydine_in.Activity.DB.DbHelper;
import com.app.eazydine_in.Adapter.FavAdapter;
import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.R;

import java.util.ArrayList;

public class FavActivity extends AppCompatActivity {

    public ArrayList<ModelHome> listdata;
    ArrayList<ModelHome> favItems;
    FavAdapter favAdapter;
    RecyclerView favRecycler;
    TextView empty_list;
    ImageView back_bnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);


        DbHelper dbHelper = new DbHelper(getApplicationContext());
        Cursor resCursor = dbHelper.getFavData();

        favRecycler=findViewById(R.id.fav_item_recycle);
        empty_list=findViewById(R.id.empty_wishlist);
        back_bnt=findViewById(R.id.back_btn);


        if (resCursor.getCount() != 0) {
            Toast.makeText(getApplicationContext(), "Fav have data.", Toast.LENGTH_SHORT).show();

            empty_list.setVisibility(View.GONE);
            favRecycler.setVisibility(View.VISIBLE);
            favItems = new ArrayList<>();
            while (resCursor.moveToNext()) {
                Log.e("fav val", "" + resCursor.getInt(0));
                this.favItems.add(
                        new ModelHome(
                                resCursor.getInt(0),//id
                                resCursor.getString(2),//image
                                resCursor.getString(1),//name
                                resCursor.getString(3),//mrp
                                resCursor.getString(4),//price
                                resCursor.getString(5),//description
                                resCursor.getString(7),//type
                                resCursor.getString(6)//category
                        )
                );
            }


            FavAdapter.OnItemClickListener r1 = new FavAdapter.OnItemClickListener() {

                public void onItemClick(final int positon, final ArrayList<ModelHome> list) {
                    //Log.e("before delete vals", "mrp: " + CartActivity.this.sum1 + "\nprice: " + CartActivity.this.sum2);
                    new AlertDialog.Builder(getApplicationContext()).setMessage((CharSequence) "Are you sure to delete?").setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            dbHelper.deleteContactFav(list.get(positon).getDishId());
                            list.remove(positon);
                            favAdapter.notifyDataSetChanged();

                        }
                    }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).show();
                }
            };

            favAdapter = new FavAdapter(getApplicationContext(), favItems, r1);
            favRecycler.setAdapter(favAdapter);
        }else{
            Toast.makeText(getApplicationContext(), "Fav don't have data.", Toast.LENGTH_SHORT).show();
            empty_list.setVisibility(View.VISIBLE);
            favRecycler.setVisibility(View.GONE);
        }

        back_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}