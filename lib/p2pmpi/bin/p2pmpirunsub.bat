@echo off
set runcmd=

:getruncmd
if "%1"=="" goto runcmd
set runcmd=%runcmd% %1
shift
goto getruncmd
:runcmd

java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%"  p2pmpi.tools.MPIArgs %runcmd%

