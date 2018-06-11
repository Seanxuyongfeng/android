@echo off

@echo Usage: push project dir files

:loop
if "%1"=="" (
    @echo please enter project dir
    goto :exit
)
if "%2"=="frameworks" (
    if "%3"=="all" (
        @echo push all frameworks jar
        adb push Z:\work\Projects\%1\out\target\product\shark\system\framework /system
        goto :exit
    )
    if not "%3"=="" (
        @echo push %3 in framworks
        adb push Z:\work\Projects\%1\out\target\product\shark\system\framework\%3 /system/framework
        goto :exit
    )
    goto :exit
)
if "%2"=="priv-app" (
    @echo push priv-app %3
    adb push Z:\work\Projects\%1\out\target\product\shark\system\priv-app\%3\%3.apk /system/priv-app/%3
    goto :exit
)
if "%2"=="app" (
    @echo push app %3
    adb push Z:\work\Projects\%1\out\target\product\shark\system\app\%3\%3.apk /system/app/%3
    goto :exit
)

:exit
    @echo exit