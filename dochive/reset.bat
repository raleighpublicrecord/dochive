@echo on

rd input /q /s
mkdir input

rd output /q /s
mkdir output

copy debug\*.pdf input

pause