@echo off

setlocal
set CLASSPATH=bin

if "%1" == "-h" goto usage

if "%1" == "" (
	set fontsize=3
) else (
	set fontsize=%1
)
shift
if "%1" == "" (
	set loadFile=
) else (
	set loadFile=-file %1
)
set debug=-Xdebug -Xrunjdwp:transport=dt_shmem,server=y,address=dnd,suspend=n
start /MIN java %debug% DnD.DnDMain %loadFile% -font %fontsize%
goto done

:usage
echo Usage: run [fontsize] [filename]
echo fontsize: increment (positive or negative) to adjust font size
echo filename: path/filename of char file to load (if any)

:done
