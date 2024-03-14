package SkodatabasProgram;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IRepository {
    List<Kund> getAllClients();
    public List<Order> getAllOrders();
    public List<Produkt> getAllProducts();
    public List<Produkt> getProductsInStock() throws SQLException;
    Map<String, String> loginMap();
    boolean authenticateClient(String username, String password);
    public void addToCart(int kundid, int beställningsid, int produktid);
    public List<Order> getCustomerOrders(int kundid);
    public void numberOfOrdersPerCustomer (List<Kund> allClients);
    public Map<String, Double> spentAmountPerCustomer();
    public void printSpentAmountPerCustomer(Map<String, Double> spentAmountPerCustomer);
    private String getCustomerFullName(int customerId) {
        return null;
    }
    public Map<String, Double> spentAmountPerCity();
    public void printSpentAmountPerCity(Map<String, Double> spentAmountPerCity);
}