AnketaDB<a name = "TOP"></a>
===================

- - - - 
## A Russian language survey management software ##

Anketa DB is a survey database management software. Within the program, users are able to create, edit, and delete surveys and responses to those surveys. All of the data is stored on an external MySQL server. 

### Dependencies
In order to run the packaged .jar program, the machine running AnketaDB must have Java installed. 

In order to run the source code, a JDK is required as well as two libraries that are not in the Java standard libraries. These are:
- [org.json](https://github.com/stleary/JSON-java)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)

### Setup
Create a MySQL server that the machine running AnketaDB is able to connect to. This server can be on the machine itself, on the same local network as the machine, or on the internet. In the `Database Setup` folder you will find two SQL scripts. Run `tablemaker.SQL` on your database.

Next, configure `usermaker.SQL` to match your server configuration. This script will create a user on your SQL server which AnketaDB will use to make updates and queries. In the provided example, `usermaker.sql` will create a user which can be logged into from any machine on the same class C local network as the SQL server. **Make sure to change the password of the user from 'dostoyevsky' to something else before running this script on your server.**

Next, configure the file `settings.json`. This file must be in the same directory as `AnketaDB.jar`. Change the "password" value to match that of the user that you created on the server. Change the "address" value to match that of the address of the server. Unless you customized `tablemaker.sql`, the "username" and "schema" values can most likely be left as-is in the example.
