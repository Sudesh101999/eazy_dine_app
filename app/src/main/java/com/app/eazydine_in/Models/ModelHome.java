package com.app.eazydine_in.Models;

import java.util.Comparator;

public class ModelHome implements Comparable<ModelHome>{
    int dishId;
    String image, name, price, mrp, description,type, category;

    public ModelHome() {
    }

    public ModelHome(int dishId, String image, String name, String price, String mrp, String description, String type, String category) {
        this.dishId = dishId;
        this.image = image;
        this.name = name;
        this.price = price;
        this.mrp = mrp;
        this.description = description;
        this.type = type;
        this.category = category;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    @Override
    public int compareTo(ModelHome modelHome) {
        return Integer.parseInt(this.price)-Integer.parseInt(modelHome.getPrice());
    }

    public static Comparator<ModelHome> shortByName = new Comparator<ModelHome>() {
        @Override
        public int compare(ModelHome t0, ModelHome t1) {
            return t0.getPrice().compareToIgnoreCase(t1.getPrice());
        }
    };
}