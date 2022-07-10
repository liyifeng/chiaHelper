@echo off
set curDir=%cd%
set JAVA_HOME=%curDir%\jdk8u332-b09-jre
set PATH=%JAVA_HOME%\bin
set CLASSPATH=.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\tools.jar

start javaw -jar chiaAutoTransfer.jar
