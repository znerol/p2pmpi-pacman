@echo off
set runcmd=
set flist=
set numargs=0
:getruncmd
if "%1"=="" goto runcmd
set runcmd=%runcmd% %1
set /A numargs=%numargs%+1
if "%1"=="-l" goto filelist
shift
goto getruncmd
:filelist
shift
set flist=%1
goto getruncmd

:runcmd
if %numargs% LSS 3 goto printUsage

set args=
FOR /F "delims=@" %%i IN ('p2pmpirunsub %runcmd%') DO set args=%%i
set jarFilesPath=
FOR /F "tokens=* delims=;" %%i IN ('java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar" p2pmpi.tools.JarReader %flist%') Do set jarFilesPath=%%i

java -cp ".\;%P2PMPI_HOME%\p2pmpi.jar;%P2PMPI_HOME%\log4j.jar;%jarFilesPath%" -Xmx512m -DP2PMPI_HOME="%P2PMPI_HOME%" -Dmode=server -Ddevice=niodevice %args%
goto exit

:printUsage
echo Usage: p2pmpirun -n ^<numproc^> ^[-r ^<numreplica^> -l ^<dependance filelist^> -w ^<time^> -a ^<strategy^>] ^<command^> [args]
echo  -a ^<strategy^> : name of allocation strategy (spread|concentrate|dataspread|dataconcentrate) (default is spread)
echo  -n ^<numproc^> : number of processes MPI
echo  -r ^<numreplica^> : number of replica per rank (not needed for 1 replica per rank)
echo  -l ^<filelist^> : list of input file (not needed if only the executable file is to be transfered)
echo  -w ^<time^> : maximum time in second to wait for searching nodes
echo  ^<command^> : executable file without .class
echo  args : argument of executable file
:exit
