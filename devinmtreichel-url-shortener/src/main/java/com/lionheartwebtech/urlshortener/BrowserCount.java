/*
 * POJO CLASS
 */
package com.lionheartwebtech.urlshortener;

import java.util.*;


public class BrowserCount {
    
    
    private String browser;
    private int counter;
       
    
    public BrowserCount (String browser, int counter) {
        
        this.browser = browser;
        this.counter = counter;
    }
    
    public String getBrowser() {
        return browser;
    }
    
    public int getBrowserCount() {
        return counter;
    }
    
    
    
    @Override
    public String toString() {
        return browser + ": " + counter;
    }    
    
}
