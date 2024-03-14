package SkodatabasProgram;

import java.sql.Date;

public class Order {
    private int id;
    private int customerID;
    private Date date;
    public Order(int id, int customerID) {
        this.id = id;
        this.customerID = customerID;
    }

    public Order(int id, int customerID, Date date) {
        this.id = id;
        this.customerID = customerID;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getCustomerID() {
        return customerID;
    }

    public Date getDate() {
        return date;
    }
}
