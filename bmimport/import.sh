#!/bin/sh

echo "Compiling and running"

javac BanManagerImport.java
java -classpath /usr/share/java/mysql-connector-java.jar:. BanManagerImport
