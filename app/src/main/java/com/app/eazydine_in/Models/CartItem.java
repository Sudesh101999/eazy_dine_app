package com.app.eazydine_in.Models;

public class CartItem {

    int id;
    private String image, name, description, mrp, offerPrice,type,category;
    private int qty;

    public CartItem(int id, String image, String name, String description, String mrp, String offerPrice, String type, String category, int qty) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.mrp = mrp;
        this.offerPrice = offerPrice;
        this.type = type;
        this.category = category;
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

}