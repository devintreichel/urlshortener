/*
 * POJO CLASS
 */
package com.lionheartwebtech.urlshortener;

import java.util.*;


public class CountryCount {
    
    
    private String country;
    private int counter;
       
    
    public CountryCount (String country, int counter) {
        
        this.country = country;
        this.counter = counter;
    }
    
    public String getCountry() {
        return country;
    }
    
    public int getCountryCount() {
        return counter;
    }
    
    
    
    @Override
    public String toString() {
        return country + ": " + counter;
    }    
    
}
