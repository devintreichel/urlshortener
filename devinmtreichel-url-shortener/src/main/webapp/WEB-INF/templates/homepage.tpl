<!DOCTYPE html>
<html>
    <head>
        <#include "CSS/main.css">
        <script>
            function copyLink() {
              var copyText = document.getElementById("shortLink");
              copyText.select();
              document.execCommand("copy");
            }
        </script>
        <title>URL Shortener - Home</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <header>
            <a href="/web?cmd=home"><h1>shrtn.de</h1>
            <h3>Shorten Your Links!</h3></a>

            <div class="userCorner">
                <h4>You are not currently logged in</h4>
                <button type="button" value="LogIn" id="logInButton" name="logInButton" onclick="location.href='/web?cmd=login';">Log-In</button>
                <button type="button" value="SignUp" id="signUpButton" name="signUpButton" onclick="location.href='/web?cmd=signup';">Sign-Up</button>
            </div>
            <hr>
        </header>
        <main>
            <p>Welcome to shrtn.de! Shorten your long complicated web addresses into nice short links anyone can share! Please see <a href="/web?cmd=aboutus">our about page</a> for details.</p>
            
            <br>
            <div class="centerContainer">
                <form action="/web?cmd=createShortLink" method="post">
                    <label for="longLink">Long Link:</label>
                    <#if emptyLong??>
                    <input type="url" id="longLink" name="longLink" value=${emptyLong}>
                    </#if>
                    <#if longLink??>
                    <input type="url" id="longLink" name="longLink" value=${longLink}>
                    </#if>
                    <input type="submit" id="submitLink" value="submit">
                    <br>
                    <label for="shortLink">Short Link:</label>
                    <#if shortLink??>
                    <input type="url" id="shortLink" readonly value="${shortLink}">
                    </#if>
                    <button type="button" value="copy" name="copy" id="copyButton" onclick="copyLink()">Copy</button>
                </form>
            </div>
        </main>
    </body>
</html>
