<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
        <#include "CSS/details.css">
        <title>URL Shortener - Stats</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <header>
            <a href="/web?cmd=home"><h1>shrtn.de</h1>
            <h3>Shorten Your Links!</h3></a>

            <div class="userCorner">
                <h4>You are currently logged in as ${username}</h4>
                <button type="button" value="stats" name="stats" id="statsbutton" onclick="location.href='/web?cmd=stats';">Stats</button>
                <button type="button" value="LogOut" id="logOutButton" name="logOutButton" onclick="location.href='/web?cmd=logout';">Log-Out</button>
            </div>
            <hr>
        </header>
        <main>
            <p>Here are the usage details of '${shortLink}'</p>
            <a href="/web?cmd=stats">Click here to go back to stats page.</a>
            <br>
            <div class="centerContainer">
                <#if clickCount != 0>
                    <table id="countryTable">
                        <th>Country</th><th>Count</th>
                        <#list country as countrycount>
                            <tr>
                              <td> ${countrycount.getCountry()}</td><td>${countrycount.getCountryCount()}</td>
                            </tr>
                        </#list>
                    </table>
                    
                    <table id="browserTable">
                        <th>Browser</th><th>Count</th>
                        <#list browser as browsercount>
                            <tr>
                              <td> ${browsercount.getBrowser()}</td><td>${browsercount.getBrowserCount()}</td>
                            </tr>
                        </#list>
                    </table>

                    <table id="ipTable">
                        <th>Click ID</th><th>IP Address</th><th>Country</th><th>Browser</th><th>Time</th>
                        <#list details as detail>
                            <tr>
                              <td>${detail.getID()}</td> <td>${detail.getIP()}</td> <td>${detail.getLocation()}</td> <td>${detail.getReference()}</td> <td>${detail.getTime()}</td>
                            </tr>
                        </#list>
                    </table>
                <#else>
                    <p>There is no data for this link available.</p>
                </#if>
            </div>
        </main>
    </body>
</html>
