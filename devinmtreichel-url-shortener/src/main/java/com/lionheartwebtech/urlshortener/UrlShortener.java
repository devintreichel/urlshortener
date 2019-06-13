/*
 * POJO CLASS
 */
package com.lionheartwebtech.urlshortener;

import java.util.*;

/**
 *
 * @author devint
 */
public class UrlShortener {
    
    private String username;
    private String longLink;
    private String shortLink;
    private int statCount;
       
    
    public UrlShortener (String username, String longLink, String shortLink, int statCount) {
        
        this.username = username;
        this.longLink = longLink;
        this.shortLink = shortLink;
        this.statCount = statCount;
    }
    
    
    public String getUsername() {
        return username;
    }
    
    public String getLongLink() {
        return longLink;
    }
    
    public String getShortLink() {
        return shortLink;
    }
    
    public int getStatCount() {
        return statCount;
    }
    
    
    @Override
    public String toString() {
        return "Short Link: " + shortLink;
    }
    
}
