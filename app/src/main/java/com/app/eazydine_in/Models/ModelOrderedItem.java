package com.app.eazydine_in.Models;

public class ModelOrderedItem {
    private String item_img,item_name,item_price,item_mrp,item_qty,item_disc,item_type,item_category;

    public ModelOrderedItem() {
    }

    public ModelOrderedItem(String item_img, String item_name, String item_price, String item_mrp, String item_qty, String item_disc, String item_type, String item_category) {
        this.item_img = item_img;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_mrp = item_mrp;
        this.item_qty = item_qty;
        this.item_disc = item_disc;
        this.item_type = item_type;
        this.item_category = item_category;
    }

    public String getItem_img() {
        return item_img;
    }

    public void setItem_img(String item_img) {
        this.item_img = item_img;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public String getItem_mrp() {
        return item_mrp;
    }

    public void setItem_mrp(String item_mrp) {
        this.item_mrp = item_mrp;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }

    public String getItem_disc() {
        return item_disc;
    }

    public void setItem_disc(String item_disc) {
        this.item_disc = item_disc;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public String getItem_category() {
        return item_category;
    }

    public void setItem_category(String item_category) {
        this.item_category = item_category;
    }
}
