package org.POJO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalDetails {
    private String name;
    ArrayList<AddressDetails> allAddresses;

    public PersonalDetails(String name, ArrayList<AddressDetails> allAddresses) {
        this.name = name;
        this.allAddresses = new ArrayList<AddressDetails>();
        if (allAddresses != null && !allAddresses.isEmpty()) {
            this.allAddresses.addAll(allAddresses);
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<AddressDetails> getAllAddresses() {
        return allAddresses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: ").append(name).append("\n");
        sb.append("Addresses:\n");

        for (AddressDetails address : allAddresses) {
            sb.append("  Street: ").append(address.street)
                    .append(", City: ").append(address.city)
                    .append("\n");
        }

        return sb.toString();
    }
}
