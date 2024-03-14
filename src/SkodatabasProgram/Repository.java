package SkodatabasProgram;

import java.io.FileInputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Repository implements IRepository {

    private final Properties p = new Properties();
    String source = "C:\\Users\\alex_\\IdeaProjects\\Databasteknik och Java\\src\\SkodatabasProgram\\Settings.properties";

    public Repository(){
        try {
            p.load(new FileInputStream(source));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




    //TA FRAM RELEVANT DATA FRÅN DATABASEN
    public List<Kund> getAllClients() {
        List<Kund> allClients = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from kund")){

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("förnamn");
                String lastName = rs.getString("efternamn");
                String clientMail = rs.getString("email");
                String phoneNumber = rs.getString("telefonnummer");
                String address = rs.getString("gatuadress");
                String postalCode = rs.getString("postnummer");
                String city = rs.getString("postort");
                String password = rs.getString("lösenord");
                allClients.add(new Kund(id, firstName, lastName, clientMail, phoneNumber, address, postalCode, city, password));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return allClients;
    }

    public List<Order> getAllOrders() {
        List<Order> allOrders = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from beställning")){

            while (rs.next()) {
                int id = rs.getInt("id");
                int customerID = rs.getInt("kundid");
                Date date = rs.getDate("datum");
                allOrders.add(new Order(id, customerID, date));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allOrders;
    }

    public List<Produkt> getAllProducts() {
        List<Produkt> allProducts = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from produkt")){

            while (rs.next()) {
                int id = rs.getInt("id");
                String brand = rs.getString("märke");
                String color = rs.getString("färg");
                int price = rs.getInt("pris");
                int size = rs.getInt("storlek");
                int inventoryBalance = rs.getInt("lagersaldo");
                allProducts.add(new Produkt(id, brand, color, price, size, inventoryBalance));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allProducts;
    }

    public List<Produkt> getProductsInStock() throws SQLException {
        List<Produkt> listOfProducts = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, märke, färg, pris, storlek, lagersaldo FROM produkt WHERE lagersaldo > 0")){

            System.out.println("Available Products:");
            System.out.println("------------------------------------------");
            System.out.printf("%-15s %-15s %-10s %-8s %-10s\n",
                    "Brand", "Color", "Price", "Size", "Stock");

            while (rs.next()) {
                int id = rs.getInt("id");
                String brand = rs.getString("märke");
                String color = rs.getString("färg");
                int price = rs.getInt("pris");
                int size = rs.getInt("storlek");
                int stock = rs.getInt("lagersaldo");

                System.out.printf("%-15s %-15s %-10s %-8s %-10s\n",
                        brand, color, price, size, stock);

                Produkt product = new Produkt(id, brand, color, price, size, stock);
                listOfProducts.add(product);
            }
        }
        return listOfProducts;
    }




    //LOGIN-PROCEDUREN
    public Map<String, String> loginMap() {
        List<Kund> listOfClients = getAllClients();
        Map<String, String> loginMap = new HashMap<>();
        for (Kund client : listOfClients){
            loginMap.put(client.getClientMail(), client.getPassword());
        }
        return loginMap;
    }

    @Override
    public boolean authenticateClient(String username, String password) {
        Map<String, String> loginCredentials = loginMap();
        String storedPassword = loginCredentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }



    //TA FRAM KUNDERNAS BEFINTLIGA BESTÄLLNINGAR FÖR ATT GE KUNDEN VALET ATT LÄGGA TILL I BEFINTLIG BESTÄLLNING
    public List<Order> getCustomerOrders(int kundid) {
        List<Order> customerOrders = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM beställning WHERE kundid = ?")) {

            stmt.setInt(1, kundid);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int beställningsid = rs.getInt("id");
                customerOrders.add(new Order(beställningsid, kundid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerOrders;
    }




    //STORED PROCEDURE
    public void addToCart(int kundid, int beställningsid, int produktid) {

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             CallableStatement callableStatement = con.prepareCall("{call AddToCart(?, ?, ?)}")) {

            callableStatement.setInt(1, kundid);
            callableStatement.setInt(2, beställningsid);
            callableStatement.setInt(3, produktid);

            callableStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    //VG-DELEN FRÅGA 2 (HÖGRE ORDNINGENS FUNKTIONER!)
    public void numberOfOrdersPerCustomer(List<Kund> allClients) {
        allClients.forEach(client -> {
            int numberOfOrders = getCustomerOrders(client.getId()).size();
            System.out.println(client.getFirstName() + " " + client.getLastName()
                        + ": " + numberOfOrders + " order(s)");
        });
    }




    //VG-DELEN FRÅGA 3

    public Map<String, Double> spentAmountPerCustomer() {
        Map<String, Double> spentAmountPerCustomer = new HashMap<>();

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement
                     ("SELECT kund.id, kund.förnamn, kund.efternamn, produkt.pris " +
                             "FROM kund " +
                             "JOIN beställning ON kund.id = beställning.kundid " +
                             "JOIN ingår ON beställning.id = ingår.beställningsid " +
                             "JOIN produkt ON ingår.produktid = produkt.id")) {

            ResultSet rs = stmt.executeQuery();

            Map<Integer, Double> totalSpentMap = new HashMap<>();

            while (rs.next()) {
                int customerId = rs.getInt("id");
                double productPrice = rs.getDouble("pris");

                totalSpentMap.merge(customerId, productPrice, Double::sum);
            }

            spentAmountPerCustomer = totalSpentMap
                    .entrySet()
                    .stream()
                    .collect(
                            HashMap::new,
                            (map, entry) -> map.put(getCustomerFullName(entry.getKey()), entry.getValue()),
                            HashMap::putAll
                    );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spentAmountPerCustomer;
    }

    private String getCustomerFullName(int customerId) {
        String fullName;

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement("SELECT förnamn, efternamn FROM kund WHERE id = ?")){

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            fullName = Optional.ofNullable(rs.next())
                    .filter(hasNext -> hasNext)
                    .map(hasNext -> {
                        try {
                            return rs.getString("förnamn") + " " + rs.getString("efternamn");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return fullName;
    }

    public void printSpentAmountPerCustomer(Map<String, Double> spentAmountPerCustomer) {
        spentAmountPerCustomer.forEach((fullName, spentAmount) ->
                System.out.println("Customer: " + fullName + ", Money Spent: " + spentAmount + " SEK"));
    }






    //   VG-DELEN FRÅGA 4 ------------------------------------------


    public Map<String, Double> spentAmountPerCity() {
        Map<String, Double> spentAmountPerCity = new HashMap<>();

        try (Connection con = DriverManager.getConnection(p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement
                     ("SELECT kund.id, kund.postort, produkt.pris " +
                             "FROM kund " +
                             "JOIN beställning ON kund.id = beställning.kundid " +
                             "JOIN ingår ON beställning.id = ingår.beställningsid " +
                             "JOIN produkt ON ingår.produktid = produkt.id")) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String city = rs.getString("postort");
                double productPrice = rs.getDouble("pris");

                spentAmountPerCity.merge(city, productPrice, Double::sum);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spentAmountPerCity;
    }

    public void printSpentAmountPerCity(Map<String, Double> spentAmountPerCity) {
        spentAmountPerCity.forEach((city, spentAmount) ->
                System.out.println("City: " + city + ", Total Spending by city: " + spentAmount + " SEK"));
    }
}