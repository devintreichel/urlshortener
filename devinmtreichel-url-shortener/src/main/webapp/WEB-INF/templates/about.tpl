<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
        <title>URL Shortener - Marketing</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <header>
            <a href="/shortener/web?cmd=home"><h1>shrtn.de</h1>
            <h3>Shorten Your Links!</h3></a>

            <#if username??>
                <div class="userCorner">
                    <h4>You are currently logged in as ${username}</h4>
                    <button type="button" value="stats" name="stats" id="statsbutton" onclick="location.href='/web?cmd=stats';">Stats</button>
                    <button type="button" value="LogOut" id="logOutButton" name="logOutButton" onclick="location.href='/web?cmd=logout';">Log-Out</button>
                </div>
            <#else>
                <div class="userCorner">
                    <h4>You are not currently logged in</h4>
                    <button type="button" value="LogIn" id="logInButton" name="logInButton" onclick="location.href='/web?cmd=login';">Log-In</button>
                    <button type="button" value="SignUp" id="signUpButton" name="signUpButton" onclick="location.href='/web?cmd=signup';">Sign-Up</button>
                </div>
            </#if>
            <hr>
        </header>
        <main>
            <div id="aboutContainer">
                <h3>Marketing!</h3>
                <p>Shrtn.de was created as a group project in 2019 by 4 students. The goal was to create a simple efficient system to shorten long URL links.</p>
                <p>Anyone has access to create a shrtn.de link that lasts for 60 days. However registered users have extra features such as being able to look at the stats of a link.</p>
            </div>
        </main>
    </body>
</html>
