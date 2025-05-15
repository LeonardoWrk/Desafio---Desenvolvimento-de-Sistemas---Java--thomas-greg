@echo off
setlocal

echo === Checando WildFly em C:\ ===

set "WILDFLY_DIR=C:\wildfly-preview-26.1.3.Final"

echo Iniciando standalone.bat...

call "%WILDFLY_DIR%\bin\standalone.bat"

endlocal
pause
