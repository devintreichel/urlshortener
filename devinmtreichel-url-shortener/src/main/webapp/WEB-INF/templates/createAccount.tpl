<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
        <title>URL Shortener - Create Account</title>
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
                    <h3>Sign-Up!</h3>
                    <form action="/web?cmd=createUser" method="post">
                        <label for="username">Username:</label><br>
                        <input type="text" id="username" name="username"><br>
                        
                        <label for="password">Password:</label><br>
                        <input type="password" id="password" name="password"><br>
                        <label for="confirmPassword">Confirm Password:</label><br>
                        <input type="password" id="confirmPassword" name="confirmPassword"><br>
                        <#if createUserError??>
                            <p class="errorText">${createUserError}</p>
                        </#if>
                        <input type="submit" id="submitsignupInfo" value="Sign-Up">
                    </form>
                </div>
                <div class="gotoDivs">
                    <h3>Already have an account? <br> Log-In here!</h3>
                    <button type="button" value="GoToLogIn" id="gotoLogInButton" name="gotoLogInButton" onclick="location.href='/web?cmd=login';">Log-In!</button>
                </div>
            </div>
        </main>
    </body>
</html>
