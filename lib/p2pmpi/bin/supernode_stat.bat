@echo off
java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMI_HOME%\log4j.jar;%CLASSPATH%" -DP2PMPI_HOME="%P2PMPI_HOME%" p2pmpi.tools.SupernodeHostCacheDisplay 
