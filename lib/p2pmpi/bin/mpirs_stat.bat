@echo off
set runcmd=
set numargs=0

if not "%2"=="" goto printUsage

if "%1"=="" goto assignLocalHost
set host=%1
goto doOperation

:assignLocalHost
set host=127.0.0.1

:doOperation
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%" -DP2PMPI_HOME="%P2PMPI_HOME%" p2pmpi.tools.RSStatus %host%
goto end

:printUsage
echo Usage : mpirs_stat ^<hostname^>
echo ^<hostname^> : the queried MPD host name"
:end
