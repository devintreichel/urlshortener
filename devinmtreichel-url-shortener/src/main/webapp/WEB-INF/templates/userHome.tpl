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
                <h4>You are currently logged in as ${username}</h4>
                <button type="button" value="stats" name="stats" id="statsbutton" onclick="location.href='/web?cmd=stats';">Stats</button>
                <button type="button" value="LogOut" id="logOutButton" name="logOutButton" onclick="location.href='/web?cmd=logout';">Log-Out</button>
            </div>
            <hr>
        </header>
        <main>
            <p>Welcome to your shrtn.de member homepage! Shorten your long complicated web addresses into nice short links anyone can share! Please see <a href="/web?cmd=aboutus">our about page</a> for details.</p>
            
            <div class="centerContainer">
                <form action="/web?cmd=createShortLink" method="post">
                    <label for="longLink">Long Link:</label><br>
                    <#if longLink??>
                        <input type="url" id="longLink" name="longLink" value=${longLink}>
                    <#else>
                        <input type="url" id="longLink" name="longLink">
                    </#if>
                    <br>
                    <label for="customText">Add custom url or leave blank for us to create one for you</label>
                    <br>
                    <input type="text" id="customText" name="customText" value="">
                    <input type="submit" id="submitLink" value="submit">
                    <br>
                    <label for="shortLink">Short Link:</label><br>
                    <#if shortLink??>
                    <input type="url" id="shortLink" readonly value="${shortLink}">
                    <#else>
                    <input type="url" id="shortLink" readonly value="shrtn.de">
                    </#if>
                    <button type="button" value="copy" name="copy" id="copyButton" onclick="copyLink()">Copy</button>
                </form>
            </div>
        </main>
    </body>
</html>
