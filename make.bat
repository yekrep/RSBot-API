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
SET cflags=-g:none
SET src=src
SET lib=lib
SET res=resources
SET out=sbin
SET dist=%lib%\%name%.jar
SET lstf=temp.txt
SET imgdir=%res%\images
SET manifest=%res%\Manifest.txt
CALL "jdk.bat"
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
jar cfm "%dist%" "%lstf%" -C "%out%" . %imgdir%\*.png
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

:remove
IF EXIST "%APPDATA%\%name%_Accounts.ini" DEL "%APPDATA%\%name%_Accounts.ini"
IF EXIST "%APPDATA%\%name% Accounts.ini" DEL "%APPDATA%\%name% Accounts.ini"
IF EXIST "%RSBOT_HOME%" RMDIR /S /Q "%RSBOT_HOME%"
FOR /F "tokens=3" %%G IN ('REG QUERY "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" /v "Personal"') DO (SET docs=%%G)
IF EXIST "%docs%\%name%" RMDIR /S /Q "%docs%\%name%"
GOTO :eof
