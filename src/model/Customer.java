package model;

public class Customer {
    private int id;
    private String name;
    private String phoneNumber;
    private String zip;
    private String address;
    private String email;
    
    public Customer(int id, String name, String phoneNumber, String zip, String address, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.zip = zip;
        this.address = address;
        this.email = email;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getZip() {
        return zip;
    }
    public String getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }
}
