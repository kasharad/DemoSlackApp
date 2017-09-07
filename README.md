About
-----
This App allows a team to play the classic tic tac toe on any channel. More about the game here -
https://en.wikipedia.org/wiki/Tic-tac-toe

Commands
--------

    /ttt challenge [@username] To challenge a member to a game. 
    /ttt move [1-3] [1-3] Place 'X' or 'O' on a cell.
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
1. Clone the repo
2. Build the slack-api project using instructions at https://github.com/allbegray/slack-api/blob/master/README.md
3. cd DemoSlackApp
4. run ant
5. To run the application - ant TttApplication
6. The server is running locally at 8080 port at this point and accepts requests.

Testing
-------
1. To run unit tests - ant test 


 
