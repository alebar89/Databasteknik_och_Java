package SkodatabasProgram;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddProductsToCartApp extends Repository {
    private int customerId = 0;
    private int orderId = 0;
    private int productId = 0;
    private Scanner sc = new Scanner(System.in);
    private final List<Kund> allClients = getAllClients();
    private final List<Order> allOrders = getAllOrders();
    private State currentState = State.LOGIN_MENU;
    private String userMail;

    public void runProgram() {

        //LAMBDAS

        Map<State, Runnable> stateHandlers = new HashMap<>();
        stateHandlers.put(State.LOGIN_MENU, () -> {
            handleLoginMenu();
        });
        stateHandlers.put(State.CHOOSE_ORDER_MENU, () -> {
            handleChooseOrderMenu();
        });
        stateHandlers.put(State.CHOOSE_PRODUCT_MENU, () -> {
            handleChooseProductMenu();
        });

        while (currentState != State.EXIT) {
            stateHandlers.get(currentState).run();
        }
    }

    private void handleLoginMenu() {
        sc = new Scanner(System.in);
        System.out.println("Sign in using your email and password");
        System.out.println("Email:");
        userMail = sc.nextLine().trim().toLowerCase();
        System.out.println("Password:");
        String userPassword = sc.nextLine();

        boolean authenticated = authenticateClient(userMail, userPassword);
        if (authenticated) {
            System.out.println("Login successful!");

            //LAMBDA

            Kund client = allClients.stream()
                    .filter(c -> c.getClientMail().equalsIgnoreCase(userMail))
                    .findFirst()
                    .orElse(null);
            if (client != null) {
                System.out.println("Welcome " + client.getFirstName() + " " + client.getLastName() + "\n");
                customerId = client.getId();
                currentState = State.CHOOSE_ORDER_MENU;
            }
        } else {
            System.out.println("Login failed! Invalid email or password!");
        }
    }

    private void handleChooseOrderMenu() {
        List<Order> customerOrders = getCustomerOrders(customerId);
        if (!customerOrders.isEmpty()) {
            int option;
            while (true) {
                try {
                    System.out.println("Do you want to make a new order or add to an existing order?");
                    System.out.println("1. Make a new order");
                    System.out.println("2. Add to an existing order");
                    option = sc.nextInt();

                    //LAMBDA

                    int finalOption = option;
                    if (IntStream.of(1, 2).anyMatch(num -> num == finalOption)) {
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again.\n");
                    }
                } catch (InputMismatchException e) {
                    sc.nextLine();
                    System.out.println("Invalid input. Please enter a number.\n");
                }
            }
            if (option == 1) {
                System.out.println("You chose to make a new order.");
                orderId = getAllOrders().size() + 1;
            } else {
                int chosenOrderNumber;
                while (true) {
                    System.out.println("Enter the number of the order you want to add a product to by typing its number:");
                    System.out.println("You have " + getCustomerOrders(customerId).size() + " existing order(s)");
                    try {
                        chosenOrderNumber = sc.nextInt();

                        //LAMBDA

                        int finalChosenOrderNumber = chosenOrderNumber;
                        if (IntStream.rangeClosed(1, customerOrders.size()).anyMatch(num -> num == finalChosenOrderNumber)) {
                            break;
                        } else {
                            System.out.println("Invalid order number. Please try again:");
                        }
                    } catch (InputMismatchException e) {
                        sc.nextLine();
                        System.out.println("Invalid input. Please enter a number.\n");
                    }
                }
                Order chosenOrder = customerOrders.get(chosenOrderNumber - 1);
                orderId = chosenOrder.getId();
            }
        } else {
            System.out.println("You have no orders.");
        }
        currentState = State.CHOOSE_PRODUCT_MENU;
    }

    private void handleChooseProductMenu() {
        try {
            List<Produkt> productsInStock = getProductsInStock();
            Set<String> availableBrands = productsInStock.stream()
                    .map(Produkt::getBrand)
                    .collect(Collectors.toSet());

            sc = new Scanner(System.in);
            String chosenBrand;
            boolean validBrand = false;
            do {
                System.out.println("Choose one of these products by typing its brand:");
                String userInputBrand = sc.nextLine().trim();
                chosenBrand = userInputBrand.substring(0, 1).toUpperCase() + userInputBrand.substring(1).toLowerCase();
                if (availableBrands.contains(chosenBrand)) {
                    validBrand = true;
                } else {
                    System.out.println("Invalid brand chosen. Please try again.");
                }
            } while (!validBrand);

            List<Produkt> filteredProducts = new ArrayList<>();
            for (Produkt product : productsInStock) {
                if (product.getBrand().equalsIgnoreCase(chosenBrand)) {
                    filteredProducts.add(product);
                }
            }

            System.out.println("Available sizes for " + chosenBrand + ":");
            Set<Integer> availableSizes = new HashSet<>();
            for (Produkt product : filteredProducts) {
                availableSizes.add(product.getSize());
            }
            System.out.println("Available sizes: " + availableSizes);

            int chosenSize;
            boolean validSize = false;
            do {
                System.out.println("Choose a size:");
                try {
                    chosenSize = Integer.parseInt(sc.nextLine());
                    for (Produkt product : filteredProducts) {
                        if (product.getBrand().equalsIgnoreCase(chosenBrand) && product.getSize() == chosenSize) {
                            validSize = true;

                            productId = product.getId();

                            System.out.println("You chose a pair of: " + product.getBrand() + ", size " + product.getSize());

                            // TEST orderId = 0;

                            addToCart(customerId, orderId, productId);
                            System.out.println("Product successfully added!");
                            break;
                        }
                    }
                    if (!validSize) {
                        System.out.println("Invalid size chosen. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid size.");
                }
            } while (!validSize);

            String addAnotherProduct;
            do {
                System.out.println("Do you want to add another product? (yes/no)");
                addAnotherProduct = sc.nextLine().trim().toLowerCase();
                if (addAnotherProduct.equals("no")) {
                    currentState = State.EXIT;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            } while (!addAnotherProduct.equals("yes") && !addAnotherProduct.equals("no"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
