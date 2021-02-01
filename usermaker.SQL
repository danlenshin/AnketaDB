CREATE USER 'anketauser'@'192.168.2.%' IDENTIFIED BY 'dostoyevsky';
GRANT INSERT, SELECT, UPDATE, DELETE ON anketadb.* TO 'anketauser'@'localhost';
FLUSH PRIVILEGES;