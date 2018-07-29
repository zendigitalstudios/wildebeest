@echo off

REM Wildebeest Migration Framework
REM Copyright © 2013 - 2018, Matheson Ventures Pte Ltd
REM
REM This file is part of Wildebeest
REM
REM Wildebeest is free software: you can redistribute it and/or modify it under
REM the terms of the GNU General Public License v2 as published by the Free
REM Software Foundation.
REM
REM Wildebeest is distributed in the hope that it will be useful, but WITHOUT ANY
REM WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
REM A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
REM
REM You should have received a copy of the GNU General Public License along with
REM Wildebeest.  If not, see http://www.gnu.org/licenses/gpl-2.0.html

if "%WB_HOME%"=="" goto noWbHome

if defined JAVA_HOME (
  java -classpath "%WB_HOME%\lib\*" co.mv.wb.cli.WildebeestCommand %*
  goto exit
)

if not defined JAVA_HOME (
  if exist "%WB_HOME%\jdk-10.0.2\bin\java.exe" (
    set JAVA_HOME="%WB_HOME%\jdk-10.0.2\"
    %WB_HOME%\jdk-10.0.2\bin\java.exe -classpath "%WB_HOME%\lib\*" co.mv.wb.cli.WildebeestCommand %*
    goto exit
  )
  if not exist %WB_HOME%\openjdk-10.0.2\bin\java.exe (
  echo Please install Java before using this tool
  goto exit
  )
)


goto exit

:noWbHome
echo Please set WB_HOME to the home directory of your Wildebeest installation
goto exit

:exit
