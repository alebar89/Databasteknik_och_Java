package SkodatabasProgram;

import java.util.Map;

public class SalesSupportApp extends Repository {

    //Metod för att ta fram rapport nummer 2 (lista kunder och antal ordrar per kund)
    public void reportOfNumberOfOrdersPerCustomer() {
        numberOfOrdersPerCustomer(getAllClients());
    }

    //Metod för att fram rapport nummer 3 (lista kunder och beställningssumma per kund)
    public void reportOfAmountSpentByCustomer() {
        Map<String, Double> spentAmountByCustomer = spentAmountPerCustomer();
        printSpentAmountPerCustomer(spentAmountByCustomer);
    }

    //Metod för att fram rapport nummer 4 (lista orter och beställningssumma per ort)
    public void reportOfAmountSpentByCity() {
        Map<String, Double> spentAmountByCity = spentAmountPerCity();
        printSpentAmountPerCity(spentAmountByCity);
    }
}
