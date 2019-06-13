<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
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
            <p>Here is your link page. You can look at all the links you have created here and look at their stats.</p>
            
            <div class="centerContainer">
                <h4 id="statsTableLabel">${username}'s Links:</h4>
                
                <#if linkCount == 0>
                    <p>You have not created any links!</p>
                <#else>
                    <table>
                        <th>Link Code</th>
                        <th>Long Link</th>
                        <th>Times Used</th>
                        <th>More Details</th>
                        <th>Delete</th>
                        <#list links as link>
                        <tr>
                            <td><a href="${link.getShortLink()}">${link.getShortLink()}</a> </td>
                            <td><a href="${link.getLongLink()}">${link.getLongLink()}</a></td>
                            <td>${link.getStatCount()}</td>
                            <td><a href ="/web?cmd=linkDetails&id=${link.getShortLink()}">Details</a></td>
                            <td><a href ="/web?cmd=deleteLink&id=${link.getShortLink()}">Delete Link</a></td>
                        </tr>
                        </#list>
                    </table>
                </#if>
            </div>
        </main>
    </body>
</html>
