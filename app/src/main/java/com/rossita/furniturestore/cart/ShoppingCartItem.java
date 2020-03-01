package com.rossita.furniturestore.cart;

public class ShoppingCartItem {

    private String img;
    private String name;
    private int quantity;
    private String price;
    private String category;
    private String userEmail;

    public ShoppingCartItem() {
    }

    public ShoppingCartItem(String price, String img, String name, String category, int quantity, String userEmail) {
        this.img = img;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.userEmail = userEmail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}
