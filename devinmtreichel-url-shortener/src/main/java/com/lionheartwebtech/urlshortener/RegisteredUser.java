/*
 * POJO CLASS
 */
package com.lionheartwebtech.urlshortener;

import java.util.*;

/**
 *
 * @author devint
 */

public class RegisteredUser {
    
    
    private int userID;
    private String username;
    private String password;
       
    
    public RegisteredUser (int userID, String username, String password) {
        
        this.userID = userID;
        this.username = username;
        this.password = password;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    
    @Override
    public String toString() {
        return "Username: " + username;
    }    
    
}
