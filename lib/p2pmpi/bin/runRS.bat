@echo off
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%CLASSPATH%" -DP2PMPI_HOME="%P2PMPI_HOME%" -DP2PMPI_HOME="%P2PMPI_HOME%" -DHOSTNAME=%COMPUTERNAME% -Dlog4j.configuration="file:%P2PMPI_HOME%/log4j.properties" p2pmpi.rs.ReservationServer
EXIT
