@echo off
setlocal

:: Nome da pasta do Maven (deve estar no mesmo diretório deste .bat ou fornecer caminho completo)
set "MAVEN_FOLDER=apache-maven-3.9.9"

:: Caminho de destino
set "DEST_DIR=C:\%MAVEN_FOLDER%"

:: Verificar se a pasta já existe em C:\
if exist "%DEST_DIR%" (
    echo Pasta %DEST_DIR% ja existe.
) else (
    echo Copiando pasta %MAVEN_FOLDER% para %DEST_DIR%...
    xcopy "%MAVEN_FOLDER%" "%DEST_DIR%" /E /I /H
)

@echo off
echo Adicionando C:\apache-maven-3.9.9\bin ao PATH do usuario via PowerShell...

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"^
$oldPath = [Environment]::GetEnvironmentVariable('Path', 'User'); ^
$newEntry = 'C:\apache-maven-3.9.9\bin'; ^
if ([string]::IsNullOrEmpty($oldPath)) { ^
    $newPath = $newEntry; ^
} elseif ($oldPath -notmatch [regex]::Escape($newEntry)) { ^
    $newPath = $oldPath + ';' + $newEntry; ^
} else { ^
    Write-Output 'O caminho ja esta no PATH.'; ^
    exit ^
} ^
[Environment]::SetEnvironmentVariable('Path', $newPath, 'User'); ^
Write-Output 'Caminho adicionado com sucesso.' ^
"

pause
echo Concluido.
pause