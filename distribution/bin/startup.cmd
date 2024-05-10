@echo off

if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=file:%BASE_DIR%/conf/

set SERVER=coCloud-server

set "COCLOUD_JVM_OPTS=-Xms512m -Xmx512m -Xmn256m"

rem set coCloud server options
set "COCLOUD_OPTS=%COCLOUD_OPTS% -jar %BASE_DIR%\target\%SERVER%.jar"

rem set coCloud server spring config location
set "COCLOUD_CONFIG_OPTS=--spring.config.additional-location=%CUSTOM_SEARCH_LOCATIONS%"

rem set coCloud server log4j file location
set "COCLOUD_LOG4J_OPTS=--logging.config=%BASE_DIR%/conf/coCloud-server-logback.xml"


set COMMAND="%JAVA%" %COCLOUD_JVM_OPTS% %COCLOUD_OPTS% %COCLOUD_CONFIG_OPTS% %COCLOUD_LOG4J_OPTS% coCloud.server %*

echo "coCloud server is starting..."
rem start coCloud server command
%COMMAND%
echo "coCloud server is started!"
