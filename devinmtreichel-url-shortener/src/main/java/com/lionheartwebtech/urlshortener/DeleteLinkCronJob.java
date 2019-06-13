package com.lionheartwebtech.urlshortener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.UnavailableException;
import org.apache.log4j.*;
import org.quartz.*;


public class DeleteLinkCronJob implements Job {
    
    private static final Logger logger = Logger.getLogger(DeleteLinkCronJob.class.getName());
    private static Connection jdbcConnection = null;
    
    
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        logger.info("Connecting to the database...");
        
        String jdbcDriver = "org.mariadb.jdbc.Driver";
        logger.info("Loading JDBC Driver: " + jdbcDriver);
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find JDBC driver on classpath.");
            return;
        }
        
        String connString = "jdbc:mariadb://";
        connString += "lionheartwebtech-db.cv18zcsjzteu.us-west-2.rds.amazonaws.com:3306";
        connString += "/bainbridge";
        connString += "?user=bainbridge&password=bainbridge";
        connString += "&useSSL=true&trustServerCertificate=true";
     
        try {
            jdbcConnection = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            logger.error("Unable to connect to SQL Database with JDBC string: " + connString);
            try {
                throw new UnavailableException("Unable to connect to database.");
            } catch (UnavailableException ex) {
                java.util.logging.Logger.getLogger(DeleteLinkCronJob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        logger.info("...connected!");
        
        logger.info("Finding and deleting ");
        boolean worked = UrlShortenerDAL.deleteLinkCronJob(jdbcConnection);
        
        if (worked) {
            logger.info("Successfully ran cron job");
        }
    }
    
}
