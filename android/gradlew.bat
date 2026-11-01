@echo off
setlocal
set "APP_HOME=%~dp0"
set "VERSION=8.14.6"
set "SHA256=7988ed071b2a07900e2ec715fca15c6ab72bce6db433af301fa7fa7e9407bd24"
set "CACHE_ROOT=%USERPROFILE%\.gradle\wrapper\dists\gradle-%VERSION%-bin\gemini-gg"
set "GRADLE_BIN=%CACHE_ROOT%\gradle-%VERSION%\bin\gradle.bat"
if not exist "%GRADLE_BIN%" (
  if not exist "%CACHE_ROOT%" mkdir "%CACHE_ROOT%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $z=Join-Path '%CACHE_ROOT%' 'gradle.zip'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip' -OutFile $z; $h=(Get-FileHash $z -Algorithm SHA256).Hash.ToLower(); if ($h -ne '%SHA256%') { throw 'SHA-256 do Gradle não confere' }; Expand-Archive -Path $z -DestinationPath '%CACHE_ROOT%' -Force; Remove-Item $z"
  if errorlevel 1 exit /b 1
)
call "%GRADLE_BIN%" -p "%APP_HOME%" %*
exit /b %ERRORLEVEL%
