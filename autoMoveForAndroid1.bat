@REM
@echo off 
REM
setlocal enabledelayedexpansion
REM
set min=0
set max=4
set /a right=0
set /a top=1
set /a left=2
set /a bottom=3
set /a attach=4

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


:toAttach
adb shell input tap 1852 832
goto toStart

:toStart

set /a mod=!max!-!min!+1

set /a r=!random!%%!mod!+!min!
echo !r!

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
if %r% equ %attach% (
    echo attck...
    goto toAttach
)
