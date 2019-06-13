/*
 * POJO CLASS
 */
package com.lionheartwebtech.urlshortener;

import java.util.*;


public class LinkDetails {
    
    
    private int id;
    private String ip;
    private String location;
    private String reference;
    private String time;
    
       
    
    public LinkDetails (int id, String ip, String location, String reference, String time) {
        
        this.id = id;
        this.ip = ip;
        this.location = location;
        this.reference = reference;
        this.time = time;
    }
    
    public int getID() {
        return id;
    }
    
    public String getTime() {
        return time;
    }
    
    public String getIP() {
        return ip;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getReference() {
        return reference;
    }
    
    
    @Override
    public String toString() {
        return "LinkID: " + id;
    }    
    
}
