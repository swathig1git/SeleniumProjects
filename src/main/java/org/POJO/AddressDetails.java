package org.POJO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDetails{
    String street;
    String city;

    public AddressDetails(String street, String city){
        this.street = street;
        this.city = city;
    }

    public AddressDetails(String city){
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}