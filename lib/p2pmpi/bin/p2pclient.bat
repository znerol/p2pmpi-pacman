@echo off

set jar=%1
set ip=%2
set port=%3
set hashkey=%4
set rank=%5
shift
shift
shift
shift
shift

FOR /F "tokens=* delims=;" %%i IN ('java -cp "%P2PMPI_HOME%\p2pmpi.jar" p2pmpi.tools.WindowClassPath %jar%') DO set cp=%%i

REM if (jar=_) means there is no jar file.
if %jar%==_ set cp=

set runcmd=
:getruncmd
if "%1"=="" goto runcmd
set runcmd=%runcmd% %1
shift
goto getruncmd

:runcmd
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%cp%" -Xmx512m -DP2PMPI_HOME="%P2PMPI_HOME%" -Dhashkey=%hashkey% -Drank=%rank% -Dip=%ip% -Dport=%port% -Dmode=client -Ddevice=niodevice -DrunCmd="%runcmd%" %runcmd%