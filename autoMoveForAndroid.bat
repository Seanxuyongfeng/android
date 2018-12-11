
goto toStart

:toRight
adb shell input draganddrop  459 853 659 853 5000
goto toStart

:toTop
adb shell input draganddrop  459 853 459 653 5000
goto toStart

:toLeft:
adb shell input draganddrop  459 853 259 853 5000
goto toStart

:toBottom

adb shell input draganddrop  459 853 459 1053 5000
goto toStart

:toStart
@REM
@echo off 
REM
setlocal enabledelayedexpansion
REM
set min=0
set max=3
set /a mod=!max!-!min!+1

set /a r=!random!%%!mod!+!min!
echo !r!
set /a right=0
set /a top=1
set /a left=2
set /a bottom=3

if %r% equ %right% (
    echo toRight...
    goto toRight
)
if %r% equ %top% (
    echo toTop...
    goto toTop
)
if %r% equ %left% (
    echo toLeft...
    goto toLeft
)
if %r% equ %bottom% (
    echo toBottom...
    goto toBottom
)
