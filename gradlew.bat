@echo off
set "GRADLE_CMD=C:\Users\harsh\.gradle\wrapper\dists\gradle-8.14-bin\38aieal9i53h9rfe7vjup95b9\gradle-8.14\bin\gradle.bat"
if exist "%GRADLE_CMD%" (
    "%GRADLE_CMD%" %*
) else (
    gradle %*
)
