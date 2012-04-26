@ECHO OFF

SETLOCAL

CALL make.bat
CALL make.bat :setvars

CD "%lib%"

ECHO Obfuscating
CALL java -D"ZKM_OPEN=%name%.jar" -D"ZKM_SAVE=.\\" -D"ZKM_CHANGELOG=ZKM_ChangeLog.txt" -jar ZKM.jar script.txt
DEL /F "%name%.jar.BACKUP"

CD ..

GOTO :eof
