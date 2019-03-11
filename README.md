Development Instructions
------------------------

Requirements for full installation:
- local MySQL database called "test"
- local clone of playframework, with remote called origin, symlinked from data/main.
- local clone of play-generated-docs, with remote called origin, symlinked from data/generated.
- local clone of each doc translation in data/translationname, e.g. data/ja, data/tr, data/fr, data/bg. See application.conf for list of translation repos.

Git should ignore the symlinked repos but you may need to tell your IDE to exclude them.

You may see exceptions in the logs about failing to authenticate, ignore them, they are just the periodic git fetch failing because the Play app doesn't have your GitHub credentials.

Requirements for minimal local installation (e.g. for previewing changes when releasing a new version of Play):

- local MySQL database called "test", e.g. on Ubuntu 17.10:

  ```
  $ sudo apt-get install mysql-server
  $ sudo mysql -u root
  mysql> CREATE DATABASE test;
  mysql> USE mysql;
  mysql> UPDATE user SET plugin='mysql_native_password' WHERE User='root';
  mysql> FLUSH PRIVILEGES;
  mysql> exit;
  $ service mysql restart
  ```

  Test connection:
  ```
  $ mysql -u root -p
  Enter password: <empty>
  mysql>
  ```

  Credit: https://stackoverflow.com/a/42742610/49630

- or a local MariaDB database called "test" using Docker:

  ```
  $ docker run -d --name play.com-mariadb -p 3306:3306 -e MYSQL_DATABASE=test -e MYSQL_ALLOW_EMPTY_PASSWORD=true mariadb:10.4
  ```

  Test connection:
  ```
  $ docker run -it --rm --link play.com-mariadb:mysql mariadb sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" test'
  ```

  Stop and remove the container:
  ```
  $ docker stop play.com-mariadb && docker rm $_
  ```

Requirements for unit tests:
- local MySQL database called "playunittest" (some tests will fail if this DB doesn't exist)
