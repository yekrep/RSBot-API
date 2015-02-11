@ECHO OFF

SET cmd=%1
IF "%cmd%"=="" (
	SETLOCAL
	CALL :setvars
	SET cmd=all
)
CALL :%cmd%
GOTO :eof

:setvars
SET name=RSBot
SET cc=javac
SET cflags=-target 1.6 -source 1.6
SET src=src
SET lib=lib
SET res=resources
SET out=%TEMP%/%name%/sbin
SET dist=%lib%\%name%.jar
SET lstf=temp.txt
SET manifest=%res%\Manifest.txt
CALL "%res%\jdk.bat"
GOTO :eof

:all
CALL :clean 2>NUL
ECHO Compiling bot
CALL :Bot
ECHO Packing JAR
CALL :pack
CALL :end
GOTO :eof

:Bot
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
FOR /F "usebackq tokens=*" %%G IN (`DIR /B /S "%src%\*.java"`) DO CALL :append "%%G"
IF EXIST "%out%" RMDIR /S /Q "%out%" > NUL
MKDIR "%out%"
"%cc%" %cflags% -d "%out%" "@%lstf%" 2>NUL
DEL /F /Q "%lstf%"
GOTO :eof

:pack
IF EXIST "%dist%" DEL /F /Q "%dist%"
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
COPY "%manifest%" "%lstf%" > NUL
jar cfm "%dist%" "%lstf%" -C "%out%" .
DEL /F /Q "%lstf%"
GOTO :eof

:end
CALL :clean 2>NUL
ECHO Compilation successful.
GOTO :eof

:append
SET gx=%1
SET gx=%gx:\=\\%
ECHO %gx% >> %lstf%
GOTO :eof

:clean
RMDIR /S /Q "%out%" 2>NUL
GOTO :eof
