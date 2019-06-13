package com.lionheartwebtech.urlshortener;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


import freemarker.core.ParseException;
import freemarker.template.*;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import org.quartz.*;
import org.quartz.impl.*;

import net.sf.uadetector.*;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class UrlShortenerServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(UrlShortenerServlet.class.getName());
    
    private static Connection jdbcConnection = null;
    private static Configuration fmConfig = new Configuration(Configuration.getVersion());
    private static final String TEMPLATE_DIR = "/WEB-INF/templates";
    
    public static String CRON_SCHEDULE = "0 0 5 1/1 * ? *";     // Every day at 5 AM.   
    public static Scheduler quartzScheduler = null;
    
    public static UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
    
    @Override
    public void init(ServletConfig config) throws UnavailableException {
        logger.info("==============================");
        logger.info("Starting " + UrlShortenerServlet.class.getSimpleName() + " servlet init");
        logger.info("==============================");
        
        logger.info("Getting real path for templateDir");
        String templateDir = config.getServletContext().getRealPath(TEMPLATE_DIR);
        logger.info("...real path is: " + templateDir);
        
        logger.info("Initializing Freemarker, templateDir: " + templateDir);
        try {
            fmConfig.setDirectoryForTemplateLoading(new File(templateDir));
            logger.info("Successfully Loaded Freemarker");
        } catch (IOException e) {
            logger.error("Template directory not found, directory: " + templateDir + ", exception: " + e);
        }
        
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
            throw new UnavailableException("Unable to connect to database.");
        }
        
        logger.info("...connected!");
        
        logger.info("Creating scheduler...");
        try {
            quartzScheduler = new StdSchedulerFactory().getScheduler();
            
            JobDetail jobSpec = JobBuilder.newJob(DeleteLinkCronJob.class)
                .withIdentity(new JobKey(DeleteLinkCronJob.class.getName()))
                .build();
        
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(DeleteLinkCronJob.class.getName())
                    .withSchedule(CronScheduleBuilder.cronSchedule(CRON_SCHEDULE))
                    .build();
            
            quartzScheduler.scheduleJob(jobSpec, trigger);
            
            quartzScheduler.start();
                        
        } catch (SchedulerException e) {
            logger.info("Error creating scheduler!", e);
            throw new UnavailableException("Unable to start scheduler.");
        }
        
        
        
        logger.info("==============================");
        logger.info("Finished init");
        logger.info("==============================");
    }
    
    @Override
    public void destroy() {
        logger.info("##############################");
        logger.info("Destroying " + UrlShortenerServlet.class.getSimpleName() + " servlet");
        logger.info("##############################");

        logger.info("Disconnecting from the database.");
        try {
            jdbcConnection.close();
        } catch (SQLException e) {
            logger.error("Exception thrown while trying to close SQL Connection: " + e, e);
        }
        logger.info("Disconneced!");
        
        logger.info("##############################");
        logger.info("...done");
        logger.info("##############################");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doGet()");       
        String command = "";
        String template = "";
        Map<String, Object> model = new HashMap<>();
        
//        String browser = request.getHeader("User-Agent");
//        logger.info("Header info: " + browser);
//        browser = parser.parse(browser).getName();
//        logger.info("browser info: " + browser);
        
               
        if (!isShortLink(request)) {
            command = request.getParameter("cmd");
            String username = request.getParameter("username");
            model.put("username", username);
            if (command == null) command = "home";
        } else {
            String shortURL = request.getRequestURI();
            if (shortURL.startsWith("/")) {
                shortURL = shortURL.substring(1);
            }
            // find longLink in DB from shortLinkS
            String longURL = UrlShortenerDAL.getLongLink(jdbcConnection, shortURL);
            //if returns null, return to homepage
            if (longURL != "") {
                // Get IP
                String ip = request.getRemoteAddr();
                // Get Browser
                String browser = request.getHeader("User-Agent");
                browser = parser.parse(browser).getName();
                if (browser.equals("Mozilla")) browser = "Internet Explorer";
                // Get Location
                Locale locale = request.getLocale();
                String location = locale.getDisplayCountry();
                
                // Add to LinkDetails table               
                UrlShortenerDAL.insertLinkStats(jdbcConnection, longURL, shortURL, ip, location, browser);
                
                response.encodeRedirectURL(longURL);
                response.sendRedirect(longURL);
                return;
            } else
            command = "home";
        }

        Cookie ck[] = request.getCookies();

        switch (command) {
            case "home":
                //sets default page to homepage.tpl
                model.put("emptyLong", "");
                model.put("shortLink", "http://shrtn.de/");
                template = "homepage.tpl";
                
                //if a user is logged in, show userHome
                if (ck!=null) { 
                    String username = ck[0].getValue();  
                    
                     if (!username.equals("") || username != null) {
                         
                        logger.info("Logged in user: " + username); 
                         
                        template = "userHome.tpl";
                        model.put("username", username);
                    }  
                }
                break;
                
            case "login":
                template = "login.tpl";
                break;
            
            case "signup":    
                template = "createAccount.tpl";
                break;
                
            case "aboutus":
                if(ck != null) {
                    String username = ck[0].getValue();
                    model.put("username", username);
                }
                template = "about.tpl";
                break;
            
            case "stats":
                String loginError = "Please log in to see your stats page";
                
                if (ck != null) {
                    String username = ck[0].getValue();  
                     if (!username.equals("") || username != null) {
                        model.put("username", username);
                        List<UrlShortener> links = UrlShortenerDAL.getAllLongLinks(jdbcConnection, username);
                        model.put("links", links);
                        model.put("linkCount", links.size());
                        template = "stats.tpl";
                    }  
                } else {   
                    template = "login.tpl";
                    model.put("loginError", loginError);
                }
                break;
                
            case "logout":
                
                Cookie cookie = new Cookie("username", ""); 
                cookie.setMaxAge(0);  
                response.addCookie(cookie);
                
                template = "homepage.tpl";
                model.put("emptyLong", "");
                model.put("shortLink", "http://shrtn.de/");
                
                break;
                
            case "linkDetails":

                String shortLink = request.getQueryString();
                shortLink = shortLink.substring(shortLink.indexOf("&id=") + 4);

                loginError = "Please log in to see your stats page";
                
                if (ck!=null) {  
                    String username = ck[0].getValue();  
                     if (!username.equals("") || username!=null) {
                        List<LinkDetails> details  = UrlShortenerDAL.getLinkStats(jdbcConnection, shortLink);
                        List<CountryCount> country = UrlShortenerDAL.getCountryCount(jdbcConnection, shortLink);
                        List<BrowserCount> browser = UrlShortenerDAL.getBrowserCount(jdbcConnection, shortLink);
                        model.put("details", details);
                        model.put("country",country);
                        model.put("browser",browser);
                        model.put("shortLink", shortLink);
                        model.put("clickCount", details.size());
                        model.put("username", username);
                        template = "details.tpl";
                    }  
                } else {   
                    template = "login.tpl";
                    model.put("loginError", loginError);
                }
                
                break;
                
            case "deleteLink":
                
                loginError = "Please log in to delete a link";
                String username = ck[0].getValue();
                if (!username.equals("") || username != null) {
                    
                    shortLink = request.getParameter("shortLink");
                    UrlShortenerDAL.deleteLink(jdbcConnection, shortLink);
                    List<UrlShortener> links = UrlShortenerDAL.getAllLongLinks(jdbcConnection, username);
                    model.put("links", links);
                    model.put("username",username);
                    template = "stats.tpl";
                } else {   
                    template = "login.tpl";
                    model.put("loginError", loginError);
                }
                break;
            
            default:
                logger.info("Invalid GET command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.info("OUT - doGet() - " + time + "ms");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doPost()");

        String command = request.getParameter("cmd");
        if (command == null) {
            logger.info("No cmd parameter received");
            command = "";
        }

        String template = "";
        Map<String, Object> model = new HashMap<>();

        switch (command) {
            case "login":
                String user = request.getParameter("username");
                String userpass = request.getParameter("password");
                String loginError = "User name or password does not match";               
                
                String hashedPassword = Hashing.sha256().hashString(userpass, Charsets.UTF_8).toString();               
                String dbPassword = UrlShortenerDAL.checkPassword(jdbcConnection, user);
                
                logger.info("DBPW: " + dbPassword);
                logger.info("USER: " + hashedPassword);
                
                if (dbPassword == null) {
                    logger.info("user not found");
                    
                    template = "login.tpl";                 
                    model.put("loginError", loginError);
                    
                } else if (dbPassword.equals(hashedPassword)) {
                    logger.info("passwords matched");
                    
                    Cookie ck = new Cookie("username", user);
                    ck.setMaxAge(3600);
                    response.addCookie(ck);
                    
                    logger.info("Successful user login - " + user);
                    
                    //String ip = request.getRemoteAddr();
                    //logger.info("USER IP ADDRESS: " + ip);
                    
                    template = "userHome.tpl";
                    model.put("username", user);           
                    
                } else {
                    logger.info("Password was incorrect");
                    
                    template = "login.tpl";
                    model.put("loginError", loginError);
                }
                break;
                
            case "createUser":
                //put new user into DB
                user = request.getParameter("username");               
                hashedPassword = Hashing.sha256().hashString(request.getParameter("password"), Charsets.UTF_8).toString();
                
                
                //search users should return false if username found in DB
                boolean isNewUser = UrlShortenerDAL.checkForUser(jdbcConnection, user);
                if (isNewUser == true) {
                    logger.debug("isNewUser was true");
                    
                    String hashedConfirm = Hashing.sha256().hashString(request.getParameter("confirmPassword"), Charsets.UTF_8).toString();
                    
                    if(hashedPassword.contentEquals(hashedConfirm)) {
                        RegisteredUser newUser = new RegisteredUser(1, user, hashedPassword);
                        newUser = UrlShortenerDAL.createUser(jdbcConnection, newUser);

                        Cookie ck = new Cookie("username", newUser.getUsername());
                        ck.setMaxAge(3600);
                        response.addCookie(ck);

                        model.put("username", newUser.getUsername());
                        template = "userHome.tpl";
                    } else {
                        logger.debug("passwords do not match");
                        String createUserError = "The passwords do not match";
                        template = "createAccount.tpl";
                        model.put("createUserError", createUserError);
                    }
                } else if (isNewUser == false) {
                    logger.debug("isNewUser was false");
                    String createUserError = "That username already exists. Please try another username";
                    template = "createAccount.tpl";
                    model.put("createUserError", createUserError);
                }
                
                break;
                
            case "createShortLink":
                Cookie ck[] = request.getCookies();
                String username = null;
                if(ck != null)
                    username = ck[0].getValue();
                
                String longLink = request.getParameter("longLink");
                logger.info("Longlink: " + longLink);
                
                //this will be moved into if/else statement???
                //UrlShortenerDAL.insertShortLink(jdbcConnection, shortLink, longLink);
                logger.info("USER BEING PASSED: " + username);
                //breaks for non-users (only uncommented to test user stuff)
                //if (username == null) user = "Guest";
                
                if (username == null || username == "") {
                    
                    template = "homepage.tpl";
                    
                    logger.info("LOADING HOMEPAGE.TPL");
                    //put link into DB with no username
                    String shortLink = createShortLink();
                    
                    logger.info("Shortlink: " + shortLink);
                    
                    UrlShortenerDAL.insertShortLink(jdbcConnection, shortLink, longLink);
                    
                    model.put("username", username);
                    model.put("longLink", longLink);
                    model.put("shortLink", "http://shrtn.de/" + shortLink);
                } else {
                    
                    template = "userHome.tpl";
                    
                    logger.info("LOADING userHome.TPL");
                    //put Link into DB with username
                    
                    String customShort = request.getParameter("customText");
                    
                    if(customShort == null || customShort == "")
                    {
                        String shortLink = createShortLink();
                        logger.info("Shortlink: " + shortLink);
                        UrlShortenerDAL.insertMemberShortLink(jdbcConnection, username, shortLink, longLink);
                        model.put("shortLink", "http://shrtn.de/" + shortLink);
                    }
                    else{
                        if (UrlShortenerDAL.checkForShortLink(jdbcConnection, customShort)){
                            UrlShortenerDAL.insertMemberShortLink(jdbcConnection, username, customShort, longLink);
                            model.put("shortLink", "http://shrtn.de/" + customShort);
                        }
                        else{
                            model.put("shortLink", "custom already exists");
                        }
                    }
                        
                    model.put("longLink", longLink);
                    model.put("username", username);
                }
                break;
            
            default:
                logger.info("Invalid POST command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.debug("OUT - doPost() - " + time + "ms");
    }        


private void processTemplate(HttpServletResponse response, String template, Map<String, Object> model) {
        logger.debug("Processing Template: " + template);
        
        try (PrintWriter out = response.getWriter()) {
            Template view = fmConfig.getTemplate(template);
            view.process(model, out);
        } catch (TemplateException e) {
            logger.error("Template Error:", e);
        } catch (MalformedTemplateNameException e) {
            logger.error("Malformed Template Error:", e);
        } catch (ParseException e) {
            logger.error("Parsing Error:", e);
        } catch (IOException e) {
            logger.error("IO Error:", e);
        } 
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
    private String createShortLink(){
        //short link will never have a 'w' in it
        boolean check = true;
        String code = "";
        String charList = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rng = new Random();
        
        while (check) {
        StringBuilder sb = new StringBuilder();
            while (sb.length() < 5) {
               int index = (int) (rng.nextFloat() * charList.length());
               sb.append(charList.charAt(index));
            }      
        
            code = sb.toString();
            logger.debug("Created shortlink code: " + code);
        
            if (UrlShortenerDAL.checkForShortLink(jdbcConnection, code))    
            check = false;
        }
        
        return code;
    }
    
    private String createShortLink(String custLink) {
        boolean check = true;
        logger.debug("Custom shortlink code: " + custLink);
        while(check){
            if (UrlShortenerDAL.checkForShortLink(jdbcConnection, custLink));
            check = false;
        }
        return custLink;
    }
    
    private boolean isShortLink(HttpServletRequest request){
        String receivedRequest = request.getRequestURI();
        logger.debug(receivedRequest + " from isShortLink");
        //if next 3 characters after 3rd / are web the stay in servlet (http://shrtn.de/web)
        //returns true if the 
//        return false;
        if("/web".equals(receivedRequest)){
            
            return false;
        }else{
            return true;
        }
    }
}
