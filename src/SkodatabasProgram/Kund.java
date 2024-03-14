package SkodatabasProgram;

public class Kund {

    private int id;
    private String firstName;
    private String lastName;
    private String clientMail;
    private String phoneNumber;
    private String address;
    private String postalCode;
    private String city;
    private String password;

    public Kund(int id, String firstName, String lastName,
                String clientMail, String phoneNumber, String address,
                String postalCode, String city, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientMail = clientMail;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getClientMail() {
        return clientMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getPassword() {
        return password;
    }
}