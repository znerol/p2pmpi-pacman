@echo off
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%" -Dlog4j.configuration="file:%P2PMPI_HOME%/log4j.properties" -DP2PMPI_HOME="%P2PMPI_HOME%" p2pmpi.tools.LocalIPCheck
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%" -Dlog4j.configuration="file:%P2PMPI_HOME%/log4j.properties" -DP2PMPI_HOME="%P2PMPI_HOME%" p2pmpi.tools.P2PMPI_Boot
IF ERRORLEVEL 1 GOTO errormessage
del /F "%P2PMPI_HOME%\tmp\mpd[%COMPUTERNAME%].log" 2> nul
start /B runFT.bat
start /B runFD.bat
start /B runRS.bat
start /B runMPD.bat
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%"  -Dlog4j.configuration="file:%P2PMPI_HOME%/log4j.properties" -DP2PMPI_HOME="%P2PMPI_HOME%" -DHOSTNAME="%COMPUTERNAME%" p2pmpi.tools.MPDStatus
IF ERRORLEVEL 1 GOTO problemstart
GOTO end

:problemstart
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%" -DP2PMPI_HOME="%P2PMPI_HOME%" p2pmpi.tools.MPIShutdown nodisplay
GOTO end
:errormessage 
echo ***                                      ***
echo ***       There is some problems.        ***
echo ***  FD, FT, and MPD can not be started. ***
echo ***                                      ***
:end

