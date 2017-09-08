About
-----
This App allows a team to play the classic tic tac toe on any channel. More about the game here -
https://en.wikipedia.org/wiki/Tic-tac-toe

Commands
--------

    /ttt challenge [@username] To challenge a member to a game. 
    /ttt move [1-3] [1-3] Place 'X' or 'O' on a cell.
    /ttt quit Either player can quit the game
    /ttt show See the status of the current board and who's turn in is.
    /ttt help See what commands are available.

How it works
------------
- The app hosts a webserver that accepts incoming requests. This model can be changed to using AWS API Gateway and Lamda.
- It maintains a pluggable database of users, game and uses Sqlite database. The DB can be changed to a NoSql store like AWS DynamoDB.
- Every user is requested for authorization before using the App
- Slack callbacks are made to get the necessary auth tokens
- Every command is validated for security and correctness before being handled by a processor 
- This App uses Java Slack-Api https://github.com/allbegray/slack-api

Installation
------------
1. Clone the repo from https://github.com/kasharad/DemoSlackApp
2. cd to the repo location
4. To build project - run ant

<console>

init:
    [mkdir] Created dir: /Users/kasharad/slack-app/DemoSlackApp/bin
     [copy] Copying 1 file to /Users/kasharad/slack-app/DemoSlackApp/bin

build-project:
     [echo] DemoSlackApp: /Users/kasharad/slack-app/DemoSlackApp/build.xml
    [javac] Compiling 20 source files to /Users/kasharad/slack-app/DemoSlackApp/bin

build:

BUILD SUCCESSFUL
Total time: 2 seconds

</console>

5. To run the application - ant TttApplication

<console>

TttApplication:
     [java] Sep 08, 2017 11:00:19 AM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
     [java] INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4bf558aa: startup date [Fri Sep 08 11:00:19 PDT 2017]; root of context hierarchy
     [java] Sep 08, 2017 11:00:19 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
     [java] INFO: Loading XML bean definitions from class path resource [beans.xml]
     [java] [main] INFO org.eclipse.jetty.util.log - Logging initialized @824ms to org.eclipse.jetty.util.log.Slf4jLog
     [java] [main] INFO org.eclipse.jetty.server.Server - jetty-9.4.z-SNAPSHOT
     [java] [main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@1283bb96{/,null,AVAILABLE}
     [java] [main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@5656be13{HTTP/1.1,[http/1.1]}{0.0.0.0:8080}
     [java] [main] INFO org.eclipse.jetty.server.Server - Started @1020ms

</console>

6. The server is running locally at 8080 port at this point and accepts requests. The code does not contain the App Secret and Verification Code. It creates a sqlite db called ttt.db and reads values from a table nameed AppStore. Use a sql client and run the following
<query>
insert into AppStore Values ('APP_ID', 'APP_SECRET', 'APP_VERIFICATION_CODE', 'https://slack.com/oauth/authorize?&client_id=APP_ID&scope=commands,im:write,users:read,team:read,chat:write:bot,channels:read');
</query>
Only as a one time restart the server after doing the insertion.

7. Testing
-------
1. To run unit tests - ant test 

<console>
init:

build-project:
     [echo] DemoSlackApp: /Users/kasharad/slack-app/DemoSlackApp/build.xml

test:
   [testng] [TestNGContentHandler] [WARN] It is strongly recommended to add "<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >" at the top of your file, otherwise TestNG may fail or not work as expected.
   [testng] 
   [testng] ===============================================
   [testng] test suite
   [testng] Total tests run: 11, Failures: 0, Skips: 0
   [testng] ===============================================
   [testng] 

BUILD SUCCESSFUL
Total time: 2 seconds
</console> 
