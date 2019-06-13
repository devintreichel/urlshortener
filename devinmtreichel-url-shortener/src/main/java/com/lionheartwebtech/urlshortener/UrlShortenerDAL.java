package com.lionheartwebtech.urlshortener;

import java.sql.*;
import java.util.*;

        
import org.apache.log4j.Logger;


public class UrlShortenerDAL {
    
    private static final Logger logger = Logger.getLogger(UrlShortenerDAL.class.getName());
    
    
    public static RegisteredUser createUser(Connection conn, RegisteredUser newUser) {

        String query = "INSERT INTO Users";
        query += " (username, password)";
        query += " VALUES (?,?)";       
        
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        
        int newUserID = executeSQLInsert(conn, query, username, password);
        
        return new RegisteredUser(newUserID, username, password);
    }
    
    public static boolean checkForUser(Connection conn, String user) {

        String query = "SELECT * FROM Users WHERE username = ?";
        
        List<Map<String,String>> results = executeSQL(conn, query, user);

        if (results.isEmpty()) {  
            return true;
        }         
        else {
            return false;
        }
    }
    
    
    public static RegisteredUser getMemberID(Connection conn, String user) {
        
        int id;
        String query = "SELECT * FROM Users WHERE username = ?";
        
        List<Map<String,String>> results = executeSQL(conn, query, user);
        
        if (!results.isEmpty()) {
            id = Integer.parseInt(results.get(0).get("userID"));
            return new RegisteredUser(id, user, "");
        }         
        else
            return null;
    }
    
    
    
    public static String checkPassword(Connection conn, String user) {

        String dbPassword;
        String query = "SELECT password FROM Users WHERE username = ?";
        
        List<Map<String,String>> results = executeSQL(conn, query, user);
        
        if (!results.isEmpty()) {
            dbPassword = results.get(0).get("password");
            return dbPassword;
        }         
        else
            return null;
    }
    
    public static boolean checkForShortLink(Connection conn, String shortLink) {

        String query = "SELECT * FROM Links WHERE shortLink = ?";
        
        List<Map<String,String>> results = executeSQL(conn, query, shortLink);

        if (results.isEmpty()) {  
            return true;
        }         
        else {
            return false;
        }
    }
    
    
    public static String insertMemberShortLink(Connection conn, String user, String shortLink, String longLink) {
        String query = "INSERT INTO Links";
        query += " (username, longLink, shortLink)";
        query += " VALUES (?, ?, ?)";
        
        executeSQLInsert(conn, query, user, longLink, shortLink);
        
        return shortLink;
    }
    
    
    public static String insertShortLink(Connection conn, String shortLink, String longLink) {
        String query = "INSERT INTO Links";
        query += " (longLink, shortLink)";
        query += " VALUES (?, ?)";
        
        executeSQLInsert(conn, query, longLink, shortLink);
        
        return shortLink;
    }
    
    public static void insertLinkStats(Connection conn, String longlink, String shortlink, String ip, String location, String reference) {
        
        String query = "INSERT INTO LinkDetails (longlink, shortlink, ip, location, reference)";
        query += "VALUES (?, ?, ?, ?, ?)";
        
        executeSQLInsert(conn, query, longlink, shortlink, ip, location, reference);
    }
    
    
    public static List<LinkDetails> getLinkStats(Connection conn, String shortlink) {
        
        String query = "SELECT id, ip, location, reference, DATE_FORMAT(timeaccessed, '%c-%d-%Y %r') AS time FROM LinkDetails WHERE shortlink = ?";
        List<LinkDetails> linkStats = new ArrayList<>();
        List<Map<String,String>> results = executeSQL(conn, query, shortlink);
        
       for (Map<String,String> row : results) {
           
            linkStats.add(new LinkDetails(Integer.parseInt(row.get("id")), row.get("ip"), row.get("location"), row.get("reference"), row.get("time")));
        }
        return linkStats; 
    }
    
    public static List<BrowserCount> getBrowserCount(Connection conn, String shortlink) {
        
        String query = "SELECT reference, COUNT(*) AS count FROM LinkDetails WHERE shortlink = ? GROUP BY reference ORDER BY count DESC";
        
        List<BrowserCount> browserCounts = new ArrayList<>();
        
        List<Map<String,String>> results = executeSQL(conn, query, shortlink);
        
        for (Map<String,String> row : results) {
          
            browserCounts.add(new BrowserCount(row.get("reference"), Integer.parseInt(row.get("count"))));
        }
        
        return browserCounts;
    }
    
    public static List<CountryCount> getCountryCount(Connection conn, String shortlink) {
        
        String query = "SELECT location, COUNT(*) AS count FROM LinkDetails WHERE shortlink = ? GROUP BY location ORDER BY count DESC";
        
        List<CountryCount> countryCounts = new ArrayList<>();
        
        List<Map<String,String>> results = executeSQL(conn, query, shortlink);
        
        for (Map<String,String> row : results) {
          
            countryCounts.add(new CountryCount(row.get("location"), Integer.parseInt(row.get("count"))));
        }
        
        return countryCounts;
    }
    
    
    public static String getLongLink(Connection conn, String shortURL) {
        
        String emptyURL = "";       
        String query = "SELECT * FROM Links WHERE shortlink = ?";
        
        List<Map<String,String>> results = executeSQL(conn, query, shortURL);
        
        if (results.size() > 0) {
            
            String longURL = results.get(0).get("longLink");
            updateCount(conn, shortURL);

            return longURL;
            
        }  else
                return emptyURL;

    }
    
    public static void updateCount(Connection conn, String shortURL) {

        String query = "UPDATE Links SET useCount = useCount + 1 WHERE shortLink = ?";
        
        executeSQL(conn, query, shortURL);
       
    }
    
    public static List<UrlShortener> getAllLongLinks(Connection conn, String username) {

        String query = "SELECT * FROM Links WHERE username = ?";
        List<UrlShortener> listOfUrls = new ArrayList<>();
        List<Map<String,String>> results = executeSQL(conn, query, username);
        
       for (Map<String,String> row : results) {
           
            listOfUrls.add(new UrlShortener(row.get("username"), row.get("longLink"), row.get("shortLink"), Integer.parseInt(row.get("useCount"))));
        }
        return listOfUrls;
    }
    
    
    public static boolean deleteLinkCronJob(Connection conn) {
        return executeSQLDelete(conn, "DELETE FROM Links WHERE username IS NULL AND createDate < NOW() - INTERVAL 60 DAY");
    }
    
    public static void deleteLink(Connection conn, String shortLink) {
        String query1 = "DELETE FROM Links WHERE shortLink = ?";
        String query2 = "DELETE FROM LinkDetails WHERE shortLink = ?";
        executeSQLDelete(conn, query1);
        executeSQLDelete(conn, query2);
    }
    
    
    private static List<Map<String,String>> executeSQL(Connection conn, String query, String... arguments) {

        logger.debug("Executing SQL Query: " + query);
        
        List<Map<String,String>> results = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            int position = 1;
            for (String arg : arguments) {
                stmt.setString(position++, arg);
            }

            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int nColumns = rsMetaData.getColumnCount();
            String[] columns = new String[nColumns];

            for (int i = 0; i < nColumns; i++) {
                columns[i] = rsMetaData.getColumnName(i+1);
            }

            Map<String, String> row;
            while (rs.next()) {
                row = new HashMap<>();
                for (int i = 0; i < nColumns; i++) {
                    row.put(columns[i], rs.getString(i+1));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception :" + e, e);
            return null;
        }
        
        return results;
    }
     
    
    
    private static int executeSQLInsert(Connection conn, String query, String... arguments) {

        logger.debug("Executing SQL Insert: " + query);
        
        int newLinkID = -1;
        String columnNames[] = new String[] { "LinkID" }; 
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query, columnNames);
            int position = 1;
            for (String arg : arguments) {
                stmt.setString(position++, arg);
            }

            int numRows = stmt.executeUpdate();
            if (numRows != 1)
                throw new SQLException("Error inserting new row into table: " + query);
            
            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            newLinkID = keys.getInt(1);

        } catch (SQLException e) {
            logger.error("SQL Expection: " + e, e);
            return -1;
        }
            
        return newLinkID;
    }
    
    private static boolean executeSQLDelete(Connection conn, String query, String... arguments) {

        logger.debug("Executing SQL Delete: " + query);
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            int position = 1;
            for (String arg : arguments) {
                stmt.setString(position++, arg);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Expection: " + e, e);
            return false;
        }
            
        return true;
    }
   
      
}
