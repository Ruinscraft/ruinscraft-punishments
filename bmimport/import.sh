#!/bin/sh

if [[ "$#" -ne 2 ]]; then
    echo "You must enter a database username and password"
fi

echo "Compiling and running..."

javac BanManagerImport.java
java -classpath /usr/share/java/mysql-connector-java.jar:. BanManagerImport $1 $2
