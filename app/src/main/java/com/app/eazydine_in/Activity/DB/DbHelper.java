package com.app.eazydine_in.Activity.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DbDishList.db";
    public static final String TABLE_NAME = "cart";
    public static final String TABLE_NAME_FAV = "fav";

    public static final String listId = "id"; //0
    public static final String listName = "dishName";//1
    public static final String listImage = "dishImg";//2
    public static final String listMrp = "dishMrp";//3
    public static final String listPrice = "dishPrice";//4
    public static final String listQty = "dishQty";//5
    public static final String listDisc = "dishDisc";//6
    public static final String listType = "dishType";//7
    public static final String listCategory = "dishCategory";//8

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cart (" +
                "id integer primary key," + //0
                "dishName text," + //1
                "dishImg text," + //2
                "dishMrp text," + //3
                "dishPrice text," + //4
                "dishQty text," + //5
                "dishDisc text," + //6
                "dishType text," + //7
                "dishCategory text)"); //8
/*
        db.execSQL("create table fav (" +
                "id integer primary key," + //0
                "dishName text," + //1
                "dishImg text," + //2
                "dishMrp text," + //3
                "dishPrice text," + //4
                "dishDisc text," + //5
                "dishType text," + //6
                "dishCategory text)"); //7*/

        db.execSQL("create table fav(id integer primary key,dishName text,dishImg text,dishMrp text,dishPrice text,dishDisc text,dishType text,dishCategory text)");

        // Category table create query
        /*String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + listId + " INTEGER PRIMARY KEY," + listName + " TEXT," + listImage + " TEXT," + listMrp + " TEXT," + listPrice + " TEXT," + listQty + " TEXT," + listDisc + " TEXT," + listType + " TEXT," + listCategory + " TEXT)";
        db.execSQL(CREATE_CATEGORIES_TABLE);*/

//        db.execSQL("CREATE TABLE " + TABLE_NAME + "("+ listId + " INTEGER PRIMARY KEY," + listName + " TEXT," + listImage + " TEXT," + listMrp + " TEXT," + listPrice + " TEXT," + listQty + " TEXT," + listDisc + " TEXT," + listType + " TEXT," + listCategory + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAV);
        onCreate(db);
    }

    public String insertDishList(int item_id, String name, String img, String mrp, String price, String qty, String disc, String type, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(listId, item_id);
        contentValues.put(listName, name);
        contentValues.put(listImage, img);
        contentValues.put(listMrp, mrp);
        contentValues.put(listPrice, price);
        contentValues.put(listQty, qty);
        contentValues.put(listDisc, disc);
        contentValues.put(listType, type);
        contentValues.put(listCategory, category);
        /*db.insert(TABLE_NAME, (String) null, contentValues);
        return true;*/

        try {
            db.insert(TABLE_NAME, null, contentValues);
            return "Inserted";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("insert error: ", e.getMessage());
            return e.getMessage();
        }

    }
    public String insertDishListFav(int item_id, String name, String img, String mrp, String price, String disc, String type, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(listId, item_id);
        contentValues.put(listName, name);
        contentValues.put(listImage, img);
        contentValues.put(listMrp, mrp);
        contentValues.put(listPrice, price);
        contentValues.put(listDisc, disc);
        contentValues.put(listType, type);
        contentValues.put(listCategory, category);
        /*db.insert(TABLE_NAME, (String) null, contentValues);
        return true;*/

        try {
            db.insert(TABLE_NAME_FAV, null, contentValues);
            return "Inserted";
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("insert error: ", e.getMessage());
            return e.getMessage();
        }

    }

    @SuppressLint("Range")
    public String countDishes() {
        String dishCountInt = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select (SUM(dishQty)) AS total_dish from " + TABLE_NAME, null);
        res.moveToFirst();

        dishCountInt = res.getString(res.getColumnIndex("total_dish"));
        res.moveToNext();
        return dishCountInt;
    }

    public int countCart() {
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from cart", (String[]) null);
            res.moveToFirst();
        } catch (Exception e) {
            Log.d("Cursor Exception: ", e.getMessage());
        } finally {
            res.close();
        }
        return res.getCount();
    }

    public int countFav() {
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from fav", (String[]) null);
            res.moveToFirst();
        } catch (Exception e) {
            Log.d("Cursor Exception: ", e.getMessage());
        } finally {
            res.close();
        }
        return res.getCount();
    }

    public boolean isInCart(int id) {
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from cart where id = ?", new String[]{String.valueOf(id)});
            res.moveToFirst();
        } catch (Exception e) {
            Log.e("cart count exception: ", e.getMessage());
        } finally {
            res.close();
        }
        if (res.getCount() < 1)
            return false;
        else
            return true;

    }

    public boolean isInFav(int id) {
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from fav where id = ?", new String[]{String.valueOf(id)});
            res.moveToFirst();
        } catch (Exception e) {
            Log.e("cart count exception: ", e.getMessage());
        } finally {
            res.close();
        }
        if (res.getCount() < 1)
            return false;
        else
            return true;

    }

    public boolean updateDishList(Integer id, String qty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(listQty, qty);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[]{Integer.toString(id.intValue())});
        return true;
    }

    public Integer deleteContact(Integer id) {
        return Integer.valueOf(getWritableDatabase().delete(TABLE_NAME, "id = ?", new String[]{Integer.toString(id.intValue())}));
    }

    public Integer deleteContactFav(Integer id) {
        return Integer.valueOf(getWritableDatabase().delete(TABLE_NAME_FAV, "id = ?", new String[]{Integer.toString(id.intValue())}));
    }

    public Cursor getDishList() {
        return getReadableDatabase().rawQuery("select * from cart", (String[]) null);
    }

    public Cursor getFavData() {
        return getReadableDatabase().rawQuery("select * from fav", (String[]) null);
    }


    public ArrayList<String> getAllCart() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from cart", (String[]) null);
            res.moveToFirst();
            while (!res.isAfterLast()) {
                array_list.add(res.getString(res.getColumnIndexOrThrow(listId)));
                res.moveToNext();
            }
        } catch (Exception e) {
            Log.e("fav count exception: ", e.getMessage());
        } finally {
            res.close();
        }
        return array_list;
    }
    public ArrayList<String> getAllFav() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = null;
        try {
            res = getReadableDatabase().rawQuery("select * from fav", (String[]) null);
            res.moveToFirst();
            while (!res.isAfterLast()) {
                array_list.add(res.getString(res.getColumnIndexOrThrow(listId)));
                res.moveToNext();
            }
        } catch (Exception e) {
            Log.e("fav count exception: ", e.getMessage());
        } finally {
            res.close();
        }
        return array_list;
    }

    public boolean fireSql(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql); //delete all rows in a table
        db.close();
        return true;
    }

}
