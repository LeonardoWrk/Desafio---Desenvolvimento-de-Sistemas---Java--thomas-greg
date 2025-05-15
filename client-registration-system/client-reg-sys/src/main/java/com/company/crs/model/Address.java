package com.company.crs.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class for Address entity
 */
public class Address implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    @JsonProperty("rua")
    private String street;

    @JsonProperty("numero")
    private String number;

    @JsonProperty("complemento")
    private String complement;

    @JsonProperty("bairro")
    private String neighborhood;

    @JsonProperty("cidade")
    private String city;

    @JsonProperty("estado")
    private String state;

    @JsonProperty("cep")
    private String zipCode;    
    /**
     * Default constructor
     */
    public Address() {
    }
    
    /**
     * Constructor with fields
     * 
     * @param id The address ID
     * @param street The street name
     * @param number The address number
     * @param city The city
     * @param state The state
     * @param zipCode The ZIP code
     */
    public Address(Long id, String street, String number, String city, String state, String zipCode) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Gets the address ID
     * 
     * @return The address ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the address ID
     * 
     * @param id The address ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the street name
     * 
     * @return The street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street name
     * 
     * @param street The street name to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the address number
     * 
     * @return The address number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the address number
     * 
     * @param number The address number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the address complement
     * 
     * @return The address complement
     */
    public String getComplement() {
        return complement;
    }

    /**
     * Sets the address complement
     * 
     * @param complement The address complement to set
     */
    public void setComplement(String complement) {
        this.complement = complement;
    }

    /**
     * Gets the neighborhood
     * 
     * @return The neighborhood
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood
     * 
     * @param neighborhood The neighborhood to set
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Gets the city
     * 
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city
     * 
     * @param city The city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state
     * 
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state
     * 
     * @param state The state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the ZIP code
     * 
     * @return The ZIP code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the ZIP code
     * 
     * @param zipCode The ZIP code to set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Address other = (Address) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Address [street=" + street + ", number=" + number + ", city=" + city + ", state=" + state + "]";
    }
    
    /**
     * Gets the full address as a formatted string
     * 
     * @return The full address
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(number);
        
        if (complement != null && !complement.isEmpty()) {
            sb.append(" - ").append(complement);
        }
        
        if (neighborhood != null && !neighborhood.isEmpty()) {
            sb.append(", ").append(neighborhood);
        }
        
        sb.append(", ").append(city).append(" - ").append(state);
        
        if (zipCode != null && !zipCode.isEmpty()) {
            sb.append(", ").append(zipCode);
        }
        
        return sb.toString();
    }
}
