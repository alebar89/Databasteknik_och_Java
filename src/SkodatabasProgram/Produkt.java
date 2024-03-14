package SkodatabasProgram;

public class Produkt {
    private int id;
    private String brand;
    private String color;
    private int price;
    private int size;
    private int inventoryBalance;

    public Produkt(int id, String brand, String color, int price, int size, int inventoryBalance) {
        this.id = id;
        this.brand = brand;
        this.color = color;
        this.price = price;
        this.size = size;
        this.inventoryBalance = inventoryBalance;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public int getInventoryBalance() {
        return inventoryBalance;
    }
}
