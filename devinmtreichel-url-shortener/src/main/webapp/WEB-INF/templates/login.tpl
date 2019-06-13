<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
        <title>URL Shortener - LogIn</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <header>
            <a href="/web?cmd=home"><h1>shrtn.de</h1>
            <h3>Shorten Your Links!</h3></a>
            <hr>
        </header>
        <main>
            <div class="centerContainer">
                <div class="signupDivs">
                    <h3>Log-In!</h3>
                    <form action="/web?cmd=login" method="post">
                        <label for="username">Username:</label><br>
                        <input type="text" id="username" name="username"><br>
                        <label for="password">Password:</label><br>
                        <input type="password" id="password" name="password"><br>
                        <#if loginError ??>
                            <p class="errorText">${loginError}</p>
                        </#if>
                        <input type="submit" id="submitLoginInfo" value="Log-In">
                    </form>
                </div>
                <div class="gotoDivs">
                    <h3>Don't already have an account? <br> Create a new one here!</h3>
                    <button type="button" value="GoToSignUp" id="gotoSignUpButton" name="gotoSignUpButton" onclick="location.href='/web?cmd=signup';">Sign-Up!</button>
                </div>
            </div>
        </main>
    </body>
</html>
