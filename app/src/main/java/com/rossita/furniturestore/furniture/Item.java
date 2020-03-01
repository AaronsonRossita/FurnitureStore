package com.rossita.furniturestore.furniture;

public class Item {

    private String img;
    private String name;
    private String price;
    private String category;
    private String furniture;

    public Item() {
    }

    public Item(String price, String img, String name, String category) {
        this.img = img;
        this.name = name;
        this.price = price;
        this.category = category;
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

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }
}
