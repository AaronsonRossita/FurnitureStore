package com.rossita.furniturestore.store;

public class Category {

    private String categoryName;
    private int categoryImg;

    public Category(){

    }

    public Category(String categoryName, int categoryImg) {
        this.categoryName = categoryName;
        this.categoryImg = categoryImg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(int categoryImg) {
        this.categoryImg = categoryImg;
    }
}
