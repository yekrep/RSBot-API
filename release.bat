@ECHO OFF

SETLOCAL

SET /p copyright= < readme.txt
::SET copyright=%copyright:'=^&apos;%

CALL make.bat
CALL make.bat :setvars

CD "%lib%"

ECHO Obfuscating
CALL java -D"ZKM_OPEN=%name%.jar" -D"ZKM_SAVE=.\\" -D"ZKM_CHANGELOG=ZKM_ChangeLog-%version%.txt" -jar ZKM.jar script.txt
DEL /F "%name%.jar.BACKUP"
MOVE /Y "%name%.jar" "%name%-%version%.jar"

ECHO Packing
SET l4j=launch4j.xml
SET vx=%version%
FOR /F "delims=" %%G in ('ECHO %version%^| sed -e "s/\(.\)/\1./g"') DO SET vx=%%G
SET vx=%vx:~0,-1%
ECHO ^<launch4jConfig^> > "%l4j%"
ECHO ^<dontWrapJar^>false^</dontWrapJar^> >> "%l4j%"
ECHO ^<headerType^>gui^</headerType^> >> "%l4j%"
ECHO ^<jar^>%name%-%version%.jar^</jar^> >> "%l4j%"
ECHO ^<outfile^>%name%-%version%.exe^</outfile^> >> "%l4j%"
ECHO ^<errTitle^>%name% Error^</errTitle^> >> "%l4j%"
ECHO ^<cmdLine^>^</cmdLine^> >> "%l4j%"
ECHO ^<chdir^>^</chdir^> >> "%l4j%"
ECHO ^<priority^>normal^</priority^> >> "%l4j%"
ECHO ^<downloadUrl^>http://links.powerbot.org/jre^</downloadUrl^> >> "%l4j%"
ECHO ^<supportUrl^>http://www.powerbot.org/^</supportUrl^> >> "%l4j%"
ECHO ^<customProcName^>true^</customProcName^> >> "%l4j%"
ECHO ^<stayAlive^>false^</stayAlive^> >> "%l4j%"
ECHO ^<manifest^>^</manifest^> >> "%l4j%"
ECHO ^<icon^>..\resources\images\icon.ico^</icon^> >> "%l4j%"
ECHO ^<singleInstance^> >> "%l4j%"
ECHO ^<mutexName^>%name%^</mutexName^> >> "%l4j%"
ECHO ^<windowTitle^>^</windowTitle^> >> "%l4j%"
ECHO ^</singleInstance^> >> "%l4j%"
ECHO ^<jre^> >> "%l4j%"
ECHO ^<path^>^</path^> >> "%l4j%"
ECHO ^<minVersion^>1.6.0_24^</minVersion^> >> "%l4j%"
ECHO ^<maxVersion^>^</maxVersion^> >> "%l4j%"
ECHO ^<jdkPreference^>preferJre^</jdkPreference^> >> "%l4j%"
ECHO ^</jre^> >> "%l4j%"
ECHO ^<versionInfo^> >> "%l4j%"
ECHO ^<fileVersion^>%vx%^</fileVersion^> >> "%l4j%"
ECHO ^<txtFileVersion^>%version%^</txtFileVersion^> >> "%l4j%"
ECHO ^<fileDescription^>%name%^</fileDescription^> >> "%l4j%"
ECHO ^<copyright^>%copyright%^</copyright^> >> "%l4j%"
ECHO ^<productVersion^>%vx%^</productVersion^> >> "%l4j%"
ECHO ^<txtProductVersion^>%version%^</txtProductVersion^> >> "%l4j%"
ECHO ^<productName^>%name%^</productName^> >> "%l4j%"
::ECHO ^<companyName^>%company%^</companyName^> >> "%l4j%"
ECHO ^<internalName^>%version%^</internalName^> >> "%l4j%"
ECHO ^<originalFilename^>%name%-%version%.exe^</originalFilename^> >> "%l4j%"
ECHO ^</versionInfo^> >> "%l4j%"
ECHO ^</launch4jConfig^> >> "%l4j%"
CALL launch4jc "%l4j%"
DEL /F "%l4j%"

CD ..

GOTO :eof
